package com.insight.uploadclean.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.util.*;

public class ExcelUtils {

    /**
     * Extracts only headers from the first row of any Excel sheet (.xls or .xlsx).
     */
    public static List<String> extractHeaders(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) return Collections.emptyList();

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }
            return headers;
        }
    }

    /**
     * Reads all rows in an Excel sheet and returns a list of row maps with null count tracking.
     */
    public static List<Map<String, Object>> readExcelAsMaps(File file, List<String> columns, Map<String, Long> nullCountsOut) throws IOException {
        List<Map<String, Object>> allRows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return allRows; // No header row

            rowIterator.next(); // Skip header

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> rowMap = new LinkedHashMap<>();

                for (int i = 0; i < columns.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Object value = getCellValue(cell);
                    rowMap.put(columns.get(i), value);

                    if (value == null || value.toString().trim().isEmpty()) {
                        nullCountsOut.put(columns.get(i), nullCountsOut.getOrDefault(columns.get(i), 0L) + 1);
                    }
                }

                allRows.add(rowMap);
            }

            return allRows;
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    /**
     * Reads only a limited number of sample rows (used for preview).
     */
    public static List<Map<String, Object>> readSheet(File file, int sampleLimit, Map<String, Long> nullCountsOut) throws IOException {
        List<String> headers = extractHeaders(file);
        List<Map<String, Object>> allRows = readExcelAsMaps(file, headers, nullCountsOut);
        return allRows.subList(0, Math.min(sampleLimit, allRows.size()));
    }
}
