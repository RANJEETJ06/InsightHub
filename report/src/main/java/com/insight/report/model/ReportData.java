package com.insight.report.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    private String id;
    private String originalFilename;
    private String filepath;
    private long rowCount;
    private int colCount;

    private Map<String, Long> nullCounts;
    private Map<String, Map<String, Object>> numeric;
    private Map<String, List<Map<String, Object>>> categorical;
    private Map<String, String> temporal;
    private List<CorrelationPair> correlations;
    private String analyzedAt;
}