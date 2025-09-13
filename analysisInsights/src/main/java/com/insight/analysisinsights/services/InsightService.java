package com.insight.analysisinsights.services;

import com.insight.analysisinsights.models.CleanedDataEvent;

public interface InsightService {
    void analyze(CleanedDataEvent event);
    String getReportStatus(String filename);
}
