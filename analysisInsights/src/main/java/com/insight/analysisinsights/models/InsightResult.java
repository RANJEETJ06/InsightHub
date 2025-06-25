package com.insight.analysisinsights.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "insight_results")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class InsightResult {
    @Id            private String id;
    private String  originalFilename;
    private long    rowCount;
    private int     colCount;

    private Map<String, Long>               nullCounts;
    private Map<String, NumericSummary>     numeric;
    private Map<String, List<ValueCount>>   categorical;
    private TemporalCoverage                temporal;
    private List<CorrelationPair>           correlations;
    private LocalDateTime                   analyzedAt;
}

