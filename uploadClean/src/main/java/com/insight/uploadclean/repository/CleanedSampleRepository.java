package com.insight.uploadclean.repository;

import com.insight.uploadclean.model.CleanedSample;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CleanedSampleRepository extends MongoRepository<CleanedSample,String> {
}
