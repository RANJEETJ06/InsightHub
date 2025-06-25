package com.insight.analysisinsights.repository;

import com.insight.analysisinsights.models.InsightResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InsightResultRepository extends MongoRepository<InsightResult,String> {
}
