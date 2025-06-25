package com.insight.uploadclean.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document("cleaned_samples")
@Data
public class CleanedSample {
    @Id
    private String id;
    private String originalFilename;
    private Instant cleanedAt;
    private List<String> columns;
    private long totalRows;
    private List<Map<String, Object>> sampleRows;
    private Map<String, Long> nullCountPerColumn;
}
