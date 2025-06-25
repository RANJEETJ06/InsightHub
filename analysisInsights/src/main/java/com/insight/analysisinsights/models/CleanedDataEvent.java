package com.insight.analysisinsights.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CleanedDataEvent{

    private String cleanedDataId;
    private String filePath;
    private String originalFilename;
    private int totalRows;
    private List<String> columns;
}

