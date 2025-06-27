package com.insight.analysisinsights.services.serviceImpl;

import com.insight.analysisinsights.exceptions.ProcessFailureException;
import com.insight.analysisinsights.models.CleanedDataEvent;
import com.insight.analysisinsights.models.InsightResult;
import com.insight.analysisinsights.models.ReportData;
import com.insight.analysisinsights.repository.InsightResultRepository;
import com.insight.analysisinsights.services.InsightService;
import com.insight.analysisinsights.utils.AnalysisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.insight.analysisinsights.utils.CSVUtility.readCsvForAnalysis;

@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {

    private final InsightResultRepository insightRepository;
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Value("${report.rabbitmq.exchange}")
    private String reportExchange;

    @Value("${report.rabbitmq.routing-key}")
    private String reportRoutingKey;

    @Override
    public void analyze(CleanedDataEvent event) {
        try {
            List<Map<String, Object>> cleanedData = readCsvForAnalysis(event.getFilePath());

            InsightResult result = InsightResult.builder()
                    .originalFilename(event.getOriginalFilename())
                    .rowCount(event.getTotalRows())
                    .colCount(event.getColumns().size())
                    .nullCounts(AnalysisUtils.computeNullCounts(cleanedData))
                    .numeric(AnalysisUtils.numericSummaries(cleanedData))
                    .categorical(AnalysisUtils.categoricalTopK(cleanedData, 10, new HashSet<>()))
                    .temporal(AnalysisUtils.detectDateRange(cleanedData))
                    .correlations(AnalysisUtils.strongCorrelations(cleanedData, 0.3))
                    .analyzedAt(LocalDateTime.now(ZoneOffset.UTC))
                    .build();

            insightRepository.save(result);
            ReportData reportData = convertToReportData(result, event);
            rabbitTemplate.convertAndSend(reportExchange, reportRoutingKey, reportData);

        } catch (IOException ioEx) {
            throw new ProcessFailureException("analyze cleaning CSV", event.getOriginalFilename(), "IOException reading file: " + ioEx.getMessage());

        } catch (Exception e) {
            throw new ProcessFailureException("analyze cleaning CSV", event.getOriginalFilename(), e.getMessage());
        }
    }


    private ReportData convertToReportData(InsightResult result,CleanedDataEvent event) {
        ReportData data = new ReportData();

        data.setId(result.getId());
        data.setOriginalFilename(result.getOriginalFilename());
        data.setFilepath(event.getFilePath());
        data.setRowCount(result.getRowCount());
        data.setColCount(result.getColCount());
        data.setNullCounts(result.getNullCounts());

        // Flatten NumericSummary → Map<String, Object>
        Map<String, Map<String, Object>> flatNumeric = new HashMap<>();
        result.getNumeric().forEach((col, summary) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("min", summary.min());
            stats.put("max", summary.max());
            stats.put("mean", summary.mean());
            stats.put("median", summary.median());
            stats.put("std", summary.std());
            flatNumeric.put(col, stats);
        });
        data.setNumeric(flatNumeric);

        // Flatten ValueCount → Map<String, Object>
        Map<String, List<Map<String, Object>>> flatCategorical = new HashMap<>();
        result.getCategorical().forEach((col, counts) -> {
            List<Map<String, Object>> simplified = counts.stream().map(vc -> {
                Map<String, Object> item = new HashMap<>();
                item.put("value", vc.value());
                item.put("count", vc.count());
                return item;
            }).toList();
            flatCategorical.put(col, simplified);
        });
        data.setCategorical(flatCategorical);

        // Flatten TemporalCoverage → Map<String, String>
        Map<String, String> flatTemporal = new HashMap<>();
        if (result.getTemporal() != null) {
            flatTemporal.put("column", result.getTemporal().column());
            flatTemporal.put("earliest", result.getTemporal().earliest().toString());
            flatTemporal.put("latest", result.getTemporal().latest().toString());
        }
        data.setTemporal(flatTemporal);

        data.setCorrelations(result.getCorrelations());
        data.setAnalyzedAt(result.getAnalyzedAt().toString());

        return data;
    }
}


