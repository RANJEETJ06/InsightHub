package com.insight.analysisinsights.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVUtility {
    public static List<Map<String, Object>> readCsvForAnalysis(String filePath) throws IOException {
        File file = new File(filePath);
        List<String> headers = extractHeaders(file);
        Map<String, Long> nullCounts = new HashMap<>();
        return readCsvAsMaps(file, headers, nullCounts);  // already implemented
    }

    public static List<String> extractHeaders(File file) throws IOException {
        try (
                Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build())
        ) {
            return new ArrayList<>(parser.getHeaderMap().keySet());
        }
    }

    public static List<Map<String, Object>> readCsvAsMaps(File file, List<String> columns, Map<String, Long> nullCountsOut) throws IOException {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (
                Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build())
        ) {
            for (CSVRecord record : parser.getRecords()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (String col : columns) {
                    String value = record.get(col);
                    if (value == null || value.trim().isEmpty()) {
                        nullCountsOut.put(col, nullCountsOut.getOrDefault(col, 0L) + 1);
                    }
                    row.put(col, value != null ? value.trim() : null);
                }
                rows.add(row);
            }
        }

        return rows;
    }
}
