package com.insight.uploadclean.service.ServiceImpl;

import com.insight.uploadclean.exceptions.ProcessFailureException;
import com.insight.uploadclean.exceptions.ResourceNotFoundException;
import com.insight.uploadclean.model.CleanedDataEvent;
import com.insight.uploadclean.model.CleanedSample;
import com.insight.uploadclean.repository.CleanedSampleRepository;
import com.insight.uploadclean.service.CleaningService;
import com.insight.uploadclean.util.CSVUtils;
import com.insight.uploadclean.util.ExcelUtils;
import com.insight.uploadclean.util.FileWriteUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.*;

import static com.insight.uploadclean.config.RabbitMQConfig.CLEANED_DATA_QUEUE;

@RequiredArgsConstructor
@Service
public class CleaningServiceImpl implements CleaningService {

    private final RabbitTemplate rabbitTemplate;
    private final CleanedSampleRepository sampleRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.clean.dir:cleaned}")
    private String cleanedDir;

    @Override
    public Map<String, Object> cleanAndProcess(String fileId) {
        File file = findUploadedFile(fileId);
        if (file == null) {
            throw new ResourceNotFoundException("File",fileId,fileId);
        }

        try {
            CleanedSample sample = cleanFileAndExtractSample(fileId, file);

            sampleRepository.save(sample);
            File cleanedFile = new File(cleanedDir, "cleaned_" + fileId + ".csv");
            CleanedDataEvent event = new CleanedDataEvent(
                    sample.getId(),
                    cleanedFile.getAbsolutePath(),
                    sample.getOriginalFilename(),
                    sample.getTotalRows(),
                    sample.getColumns()
            );
            rabbitTemplate.convertAndSend(CLEANED_DATA_QUEUE, event);

            return Map.of(
                    "message", "File cleaned and event published",
                    "fileId", sample.getId(),
                    "sample", sample.getSampleRows(),
                    "nullCounts", sample.getNullCountPerColumn()
            );

        } catch (Exception e) {
            throw new ProcessFailureException("cleanAndProcess",fileId,e.getMessage());
        }
    }

    private CleanedSample cleanFileAndExtractSample(String fileId, File file) {
        try {
            List<String> columns;
            List<Map<String, Object>> allRows;
            Map<String, Long> nullCounts = new HashMap<>();

            String name = file.getName().toLowerCase();
            if (name.endsWith(".csv")) {
                columns = CSVUtils.extractHeaders(file);
                allRows = CSVUtils.readCsvAsMaps(file, columns, nullCounts);
            } else if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
                columns = ExcelUtils.extractHeaders(file);
                allRows = ExcelUtils.readExcelAsMaps(file, columns, nullCounts);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + name);
            }

            // Step 1: Remove duplicate rows
            allRows = new ArrayList<>(new LinkedHashSet<>(allRows));

            // Step 2: Fill null values
            for (Map<String, Object> row : allRows) {
                for (String col : columns) {
                    Object val = row.get(col);
                    if (val == null || val.toString().trim().isEmpty()) {
                        row.put(col, isNumericColumn(allRows, col) ? 0 : "Unknown");
                    }
                }
            }

            // Step 3: Remove outliers
            removeOutliers(allRows, columns);

            FileWriteUtil.writeCleanedDataToCsv(cleanedDir,fileId, columns, allRows);

            // Step 4: Take 5 sample rows
            List<Map<String, Object>> sampleRows = allRows.subList(0, Math.min(5, allRows.size()));

            CleanedSample sample = new CleanedSample();
            sample.setId(fileId);
            sample.setOriginalFilename(file.getName());
            sample.setCleanedAt(Instant.now());
            sample.setColumns(columns);
            sample.setSampleRows(sampleRows);
            sample.setNullCountPerColumn(nullCounts);
            sample.setTotalRows(allRows.size());

            return sample;

        } catch (Exception e) {
            throw new RuntimeException("Error processing file: " + e.getMessage(), e);
        }
    }

    private boolean isNumericColumn(List<Map<String, Object>> rows, String col) {
        for (Map<String, Object> row : rows) {
            Object val = row.get(col);
            if (val instanceof Number) return true;
            try {
                Double.parseDouble(String.valueOf(val));
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    private void removeOutliers(List<Map<String, Object>> rows, List<String> columns) {
        for (String col : columns) {
            List<Double> values = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Object val = row.get(col);
                if (val instanceof Number) {
                    values.add(((Number) val).doubleValue());
                } else {
                    try {
                        values.add(Double.parseDouble(String.valueOf(val)));
                    } catch (Exception ignored) {}
                }
            }

            if (values.size() < 5) continue;

            Collections.sort(values);
            double q1 = values.get(values.size() / 4);
            double q3 = values.get(values.size() * 3 / 4);
            double iqr = q3 - q1;
            double lower = q1 - 1.5 * iqr;
            double upper = q3 + 1.5 * iqr;

            rows.removeIf(row -> {
                Object val = row.get(col);
                try {
                    double d = val instanceof Number ? ((Number) val).doubleValue() : Double.parseDouble(String.valueOf(val));
                    return d < lower || d > upper;
                } catch (Exception e) {
                    return false;
                }
            });
        }
    }

    private File findUploadedFile(String fileId) {
        File dir = new File(uploadDir);
        if (!dir.exists()) return null;

        File[] matched = dir.listFiles((file, name) -> name.startsWith(fileId));
        return matched != null && matched.length > 0 ? matched[0] : null;
    }
}
