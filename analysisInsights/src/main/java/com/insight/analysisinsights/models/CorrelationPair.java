package com.insight.analysisinsights.models;

import java.util.List;

public record CorrelationPair(String x, List<RelatedCorrelation> related) {}
