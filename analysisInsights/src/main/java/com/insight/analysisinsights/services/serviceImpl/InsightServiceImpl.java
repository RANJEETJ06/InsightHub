package com.insight.analysisinsights.services.serviceImpl;

import com.insight.analysisinsights.models.CleanedDataEvent;
import com.insight.analysisinsights.models.InsightResult;
import com.insight.analysisinsights.repository.InsightResultRepository;
import com.insight.analysisinsights.services.InsightService;
import com.insight.analysisinsights.utils.AnalysisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.insight.analysisinsights.utils.CSVUtility.readCsvForAnalysis;

@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {

    private final InsightResultRepository insightRepository;

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
                    .categorical(AnalysisUtils.categoricalTopK(cleanedData, 10,new HashSet<>()))
                    .temporal(AnalysisUtils.detectDateRange(cleanedData))
                    .correlations(AnalysisUtils.strongCorrelations(cleanedData, 0.3))
                    .analyzedAt(LocalDateTime.now(ZoneOffset.UTC))
                    .build();

            insightRepository.save(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze cleaned CSV: " + e.getMessage(), e);
        }
    }
}


