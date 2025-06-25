package com.insight.analysisinsights.models;

import java.time.LocalDate;

public record TemporalCoverage(String column, LocalDate earliest, LocalDate latest) {}
