package com.insight.report.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVUtils {

    public static List<Map<String, Object>> readCsvAsList(File file) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : parser) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (String header : parser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }
                result.add(row);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV as list: " + file.getAbsolutePath(), e);
        }

        return result;
    }
}
