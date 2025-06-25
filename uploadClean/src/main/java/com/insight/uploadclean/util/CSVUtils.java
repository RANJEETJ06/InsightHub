package com.insight.uploadclean.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVUtils {

    /**
     * Reads a CSV file and returns all rows as List of Map<String, Object>,
     * while also collecting null counts.
     */
    public static List<Map<String, Object>> readCsvAsMaps(File file, List<String> columns, Map<String, Long> nullCountsOut) throws IOException {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(reader, format);
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

    /**
     * Extracts the column headers from a CSV file.
     */
    public static List<String> extractHeaders(File file) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(reader, format);
            return new ArrayList<>(parser.getHeaderMap().keySet());
        }
    }

    /**
     * Reads the raw CSV records (if you want to work with Apache Commons CSV directly).
     */
    public static List<CSVRecord> readCsvRecords(File file) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(reader, format);
            return parser.getRecords();
        }
    }

    /**
     * Converts a single CSVRecord to a Map<String, Object>.
     */
    public static Map<String, Object> toRowMap(CSVRecord record, List<String> columns) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (String col : columns) {
            String value = record.get(col);
            row.put(col, value != null ? value.trim() : null);
        }
        return row;
    }

    /**
     * Counts null/empty values per column in a CSV list.
     */
    public static Map<String, Long> countNulls(List<CSVRecord> records, List<String> columns) {
        Map<String, Long> nullCounts = new HashMap<>();
        for (CSVRecord record : records) {
            for (String col : columns) {
                String value = record.get(col);
                if (value == null || value.trim().isEmpty()) {
                    nullCounts.put(col, nullCounts.getOrDefault(col, 0L) + 1);
                }
            }
        }
        return nullCounts;
    }
}
