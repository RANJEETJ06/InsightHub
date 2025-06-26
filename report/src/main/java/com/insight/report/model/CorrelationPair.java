package com.insight.report.model;

import java.util.List;

public record CorrelationPair(String x, List<RelatedCorrelation> related) {}