package com.insight.report.utils;

import com.insight.report.model.CorrelationPair;
import com.insight.report.model.RelatedCorrelation;
import com.insight.report.model.ReportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ExcelDashboardGenerator {

    public static void generateExcel(ReportData data, File excelFile) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet dataSheet = wb.createSheet("Cleaned Dataset");
            loadCsvToSheet(data.getFilepath(), dataSheet);

            XSSFSheet summary = wb.createSheet("Insight Summary");
            buildSummarySheet(wb, summary, data);

            XSSFSheet dash = wb.createSheet("Dashboard");
            buildDashboardSheet(dash, data);

            autosize(dataSheet);
            autosize(summary);
            autosize(dash);

            try (FileOutputStream out = new FileOutputStream(excelFile)) {
                wb.write(out);
            }
        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }

    private static void loadCsvToSheet(String path, Sheet sheet) throws IOException {
        int r = 0;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                Row row = sheet.createRow(r++);
                String[] cells = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int c = 0; c < cells.length; c++)
                    row.createCell(c).setCellValue(cells[c].replaceAll("^\"|\"$", ""));
            }
        }
    }

    private static void buildSummarySheet(XSSFWorkbook wb, XSSFSheet sh, ReportData d) {
        int r = 0;
        CellStyle bold = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        bold.setFont(f);

        sh.createRow(r++).createCell(0).setCellValue("Insight Report for " + d.getOriginalFilename());
        sh.createRow(r++).createCell(0).setCellValue("Rows: " + d.getRowCount());
        sh.createRow(r++).createCell(0).setCellValue("Columns: " + d.getColCount());
        sh.createRow(r++).createCell(0).setCellValue("Analyzed At: " + d.getAnalyzedAt());
        r++;

        Row h = sh.createRow(r++);
        h.createCell(0).setCellValue("Null Counts");
        h.getCell(0).setCellStyle(bold);
        Row nh = sh.createRow(r++);
        nh.createCell(0).setCellValue("Column");
        nh.createCell(1).setCellValue("Nulls");
        nh.setRowStyle(bold);
        for (var e : d.getNullCounts().entrySet()) {
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(e.getKey());
            row.createCell(1).setCellValue(e.getValue());
        }
        r++;

        h = sh.createRow(r++);
        h.createCell(0).setCellValue("Numeric Summary");
        h.getCell(0).setCellStyle(bold);
        Row nh2 = sh.createRow(r++);
        String[] hdr = {"Column", "Min", "Max", "Mean", "Median", "Std"};
        for (int i = 0; i < hdr.length; i++) nh2.createCell(i).setCellValue(hdr[i]);
        nh2.setRowStyle(bold);
        for (var e : d.getNumeric().entrySet()) {
            var sm = e.getValue();
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(e.getKey());
            row.createCell(1).setCellValue(toD(sm.get("min")));
            row.createCell(2).setCellValue(toD(sm.get("max")));
            row.createCell(3).setCellValue(toD(sm.get("mean")));
            row.createCell(4).setCellValue(toD(sm.get("median")));
            row.createCell(5).setCellValue(toD(sm.get("std")));
        }
        r++;

        h = sh.createRow(r++);
        h.createCell(0).setCellValue("Categorical Top Values");
        h.getCell(0).setCellStyle(bold);
        for (var e : d.getCategorical().entrySet()) r = dumpCategoryWithChart(sh, r, e);

        h = sh.createRow(r++);
        h.createCell(0).setCellValue("Temporal Coverage");
        h.getCell(0).setCellStyle(bold);
        Row tRow = sh.createRow(r++);
        tRow.createCell(0).setCellValue("Column");
        tRow.createCell(1).setCellValue(d.getTemporal().get("column"));
        tRow = sh.createRow(r++);
        tRow.createCell(0).setCellValue("Earliest");
        tRow.createCell(1).setCellValue(d.getTemporal().get("earliest"));
        tRow = sh.createRow(r++);
        tRow.createCell(0).setCellValue("Latest");
        tRow.createCell(1).setCellValue(d.getTemporal().get("latest"));

        Row corrHeader = sh.createRow(r++);
        corrHeader.createCell(0).setCellValue("Column");
        corrHeader.createCell(1).setCellValue("Correlated With");
        corrHeader.createCell(2).setCellValue("Pearson r");
        int corrStartRow = r;
        for (CorrelationPair pair : d.getCorrelations()) {
            for (RelatedCorrelation rel : pair.related()) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(pair.x());
                row.createCell(1).setCellValue(rel.y());
                row.createCell(2).setCellValue(rel.r());
            }
        }

        SheetConditionalFormatting cf = sh.getSheetConditionalFormatting();
        ConditionalFormattingRule rule = cf.createConditionalFormattingColorScaleRule();
        ColorScaleFormatting csf = rule.getColorScaleFormatting();
        csf.setNumControlPoints(3);
        csf.getThresholds()[0].setRangeType(ConditionalFormattingThreshold.RangeType.MIN);
        csf.getThresholds()[1].setRangeType(ConditionalFormattingThreshold.RangeType.PERCENTILE);
        csf.getThresholds()[1].setValue(50.0);
        csf.getThresholds()[2].setRangeType(ConditionalFormattingThreshold.RangeType.MAX);
        XSSFColor green = new XSSFColor(new java.awt.Color(0, 255, 0), new DefaultIndexedColorMap());
        XSSFColor yellow = new XSSFColor(new java.awt.Color(255, 255, 0), new DefaultIndexedColorMap());
        XSSFColor red = new XSSFColor(new java.awt.Color(255, 0, 0), new DefaultIndexedColorMap());

        csf.getColors()[0] = green;
        csf.getColors()[1] = yellow;
        csf.getColors()[2] = red;
        cf.addConditionalFormatting(new CellRangeAddress[]{new CellRangeAddress(corrStartRow, r - 1, 2, 2)}, rule);
    }

    private static int dumpCategoryWithChart(Sheet sh, int r, Map.Entry<String, List<Map<String, Object>>> e) {
        Row head = sh.createRow(r++);
        head.createCell(0).setCellValue(e.getKey());
        int chartStartRow = r;
        for (var m : e.getValue()) {
            String v = m.get("value").toString();
            if (v.equalsIgnoreCase("unknown") || v.equalsIgnoreCase("__other__")) continue;
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(v);
            row.createCell(1).setCellValue(Long.parseLong(m.get("count").toString()));
        }
        int chartEndRow = r - 1;
        drawPieChart(sh, e.getKey(), chartStartRow, chartEndRow);
        return r + 15;
    }

    private static void drawPieChart( Sheet sheet, String title, int fromRow, int toRow) {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, fromRow, 9, fromRow + 15);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(title);
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);

        XDDFDataSource<String> cat = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                new CellRangeAddress(fromRow, toRow, 0, 0));
        XDDFNumericalDataSource<Double> val = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                new CellRangeAddress(fromRow, toRow, 1, 1));

        XDDFPieChartData data = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
        XDDFPieChartData.Series series = (XDDFPieChartData.Series) data.addSeries(cat, val);
        series.setTitle(title, null);
        chart.plot(data);
    }

    private static double toD(Object o) {
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private static void autosize(Sheet sh) {
        if (sh.getPhysicalNumberOfRows() == 0) return;
        Row row = sh.getRow(0);
        if (row == null) return;
        for (int i = 0; i < row.getLastCellNum(); i++) sh.autoSizeColumn(i);
    }

    private static void buildDashboardSheet(XSSFSheet dash, ReportData data) {
        int r = 0;
        dash.createRow(r++).createCell(0).setCellValue("🔍 Dashboard Summary");
        Row head = dash.createRow(r++);
        head.createCell(0).setCellValue("X");
        head.createCell(1).setCellValue("Y");
        head.createCell(2).setCellValue("Pearson r");

        for (CorrelationPair pair : data.getCorrelations()) {
            for (RelatedCorrelation rel : pair.related()) {
                if (Math.abs(rel.r()) >= 0.7) {
                    Row row = dash.createRow(r++);
                    row.createCell(0).setCellValue(pair.x());
                    row.createCell(1).setCellValue(rel.y());
                    row.createCell(2).setCellValue(rel.r());
                }
            }
        }
    }
}
