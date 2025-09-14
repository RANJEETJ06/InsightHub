package com.insight.uploadclean.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class FileWriteUtil {

    public static void writeCleanedDataToCsv(String dirPath,String fileId, List<String> headers, List<Map<String, Object>> rows) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs(); // create a directory if it doesn't exist
        }
        String outputFile = dirPath + "/cleaned_" + fileId + ".csv";

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
