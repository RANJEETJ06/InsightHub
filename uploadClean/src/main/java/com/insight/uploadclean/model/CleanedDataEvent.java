package com.insight.uploadclean.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanedDataEvent implements Serializable {
    private String id;
    private String filePath;
    private String originalFilename;
    private long totalRows;
    private List<String> columns;
}
