package com.insight.uploadclean.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class FileWriteUtil {

    public static void writeCleanedDataToCsv(String fileId, List<String> headers, List<Map<String, Object>> rows) throws IOException {
        String outputDir = "cleaned";
        String outputFile = outputDir + "/cleaned_" + fileId + ".csv";

        // Create a directory if it doesn't exist
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(outputDir));

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Write header
            writer.println(String.join(",", headers));

            // Write rows
            for (Map<String, Object> row : rows) {
                StringBuilder line = new StringBuilder();
                for (String col : headers) {
                    Object value = row.getOrDefault(col, "");
                    String clean = String.valueOf(value).replaceAll(",", " ").replaceAll("\n", " ").trim();
                    line.append(clean).append(",");
                }
                writer.println(line.substring(0, line.length() - 1));
            }
        }

    }
}
