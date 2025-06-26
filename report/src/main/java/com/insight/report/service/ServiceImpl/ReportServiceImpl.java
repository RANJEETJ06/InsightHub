package com.insight.report.service.ServiceImpl;

import com.insight.report.model.CorrelationPair;
import com.insight.report.model.RelatedCorrelation;
import com.insight.report.model.ReportData;
import com.insight.report.service.ReportService;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// iText 7 imports
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;



import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public void generateReports(ReportData data) {
        try {
            // 1️⃣ Define the target folder path
            String baseName = data.getOriginalFilename().replaceAll("\\.[^.]+$", "");
            File folder = new File("./reports/" + baseName);

            // 2️⃣ Create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();  // creates parent directories if needed
            }

            // 3️⃣ Create PDF and Excel files inside that folder
            File pdfFile = new File(folder, "report.pdf");
            File excelFile = new File(folder, "report.xlsx");

            // 4️⃣ Generate content
            generatePDF(data, pdfFile);
            generateExcel(data, excelFile);

            System.out.println("✅ Reports generated at: " + folder.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate reports: " + e.getMessage(), e);
        }
    }

    private void generatePDF(ReportData data, File pdfFile) {
        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("📄 Insight Report").setBold().setFontSize(16));
            document.add(new Paragraph("Filename: " + data.getOriginalFilename()));
            document.add(new Paragraph("Rows: " + data.getRowCount()));
            document.add(new Paragraph("Columns: " + data.getColCount()));
            document.add(new Paragraph("Analyzed At: " + data.getAnalyzedAt()));
            document.add(new Paragraph("\n"));

            // Null Counts
            document.add(new Paragraph("🧮 Null Counts:").setBold());
            for (Map.Entry<String, Long> entry : data.getNullCounts().entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
            }

            document.add(new Paragraph("\n📊 Numeric Summary:").setBold());
            for (Map.Entry<String, Map<String, Object>> entry : data.getNumeric().entrySet()) {
                String col = entry.getKey();
                Map<String, Object> summary = entry.getValue();
                document.add(new Paragraph("🔹 " + col + " → min=" + summary.get("min")
                        + ", max=" + summary.get("max")
                        + ", mean=" + summary.get("mean")
                        + ", median=" + summary.get("median")
                        + ", std=" + summary.get("std")));
            }

            document.add(new Paragraph("\n🏷️ Categorical Top Values:").setBold());
            for (Map.Entry<String, List<Map<String, Object>>> entry : data.getCategorical().entrySet()) {
                document.add(new Paragraph("🔸 " + entry.getKey()));
                for (Map<String, Object> val : entry.getValue()) {
                    document.add(new Paragraph("   - " + val.get("value") + " : " + val.get("count")));
                }
            }

            document.add(new Paragraph("\n🕒 Temporal Coverage:").setBold());
            Map<String, String> temporal = data.getTemporal();
            document.add(new Paragraph("Column: " + temporal.get("column")));
            document.add(new Paragraph("Earliest: " + temporal.get("earliest")));
            document.add(new Paragraph("Latest: " + temporal.get("latest")));

            document.add(new Paragraph("\n🔗 Strong Correlations:").setBold());
            for (CorrelationPair pair : data.getCorrelations()) {
                String x = pair.x();
                for (RelatedCorrelation related : pair.related()) {
                    document.add(new Paragraph(" - " + x + " ↔ " + related.y() + " (r=" + related.r() + ")"));
                }
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }


    private void generateExcel(ReportData data, File excelFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Insight Summary");
            int rowIdx = 0;

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Insight Report for " + data.getOriginalFilename());

            sheet.createRow(rowIdx++).createCell(0).setCellValue("Rows: " + data.getRowCount());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Columns: " + data.getColCount());
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Analyzed At: " + data.getAnalyzedAt());

            rowIdx++;

            // Null Counts
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Null Counts");
            Row nullHeader = sheet.createRow(rowIdx++);
            nullHeader.createCell(0).setCellValue("Column");
            nullHeader.createCell(1).setCellValue("Null Count");

            for (Map.Entry<String, Long> entry : data.getNullCounts().entrySet()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            rowIdx++;

            // Numeric Summary
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Numeric Summary");
            Row numHeader = sheet.createRow(rowIdx++);
            numHeader.createCell(0).setCellValue("Column");
            numHeader.createCell(1).setCellValue("Min");
            numHeader.createCell(2).setCellValue("Max");
            numHeader.createCell(3).setCellValue("Mean");
            numHeader.createCell(4).setCellValue("Median");
            numHeader.createCell(5).setCellValue("Std");

            for (Map.Entry<String, Map<String, Object>> entry : data.getNumeric().entrySet()) {
                Row row = sheet.createRow(rowIdx++);
                Map<String, Object> summary = entry.getValue();
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(Double.parseDouble(summary.get("min").toString()));
                row.createCell(2).setCellValue(Double.parseDouble(summary.get("max").toString()));
                row.createCell(3).setCellValue(Double.parseDouble(summary.get("mean").toString()));
                row.createCell(4).setCellValue(Double.parseDouble(summary.get("median").toString()));
                row.createCell(5).setCellValue(Double.parseDouble(summary.get("std").toString()));
            }

            rowIdx++;

            // Categorical
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Categorical Top Values");
            for (Map.Entry<String, List<Map<String, Object>>> entry : data.getCategorical().entrySet()) {
                sheet.createRow(rowIdx++).createCell(0).setCellValue(entry.getKey());
                for (Map<String, Object> item : entry.getValue()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(item.get("value").toString());
                    row.createCell(1).setCellValue(Long.parseLong(item.get("count").toString()));
                }
            }

            rowIdx++;

            // Temporal
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Temporal Coverage");
            Map<String, String> temporal = data.getTemporal();
            Row tRow = sheet.createRow(rowIdx++);
            tRow.createCell(0).setCellValue("Column");
            tRow.createCell(1).setCellValue(temporal.get("column"));
            tRow = sheet.createRow(rowIdx++);
            tRow.createCell(0).setCellValue("Earliest");
            tRow.createCell(1).setCellValue(temporal.get("earliest"));
            tRow = sheet.createRow(rowIdx++);
            tRow.createCell(0).setCellValue("Latest");
            tRow.createCell(1).setCellValue(temporal.get("latest"));

            rowIdx++;

            // Correlations
            Row corrHeader = sheet.createRow(rowIdx++);
            corrHeader.createCell(0).setCellValue("Column");
            corrHeader.createCell(1).setCellValue("Correlated With");
            corrHeader.createCell(2).setCellValue("Pearson r");

            for (CorrelationPair pair : data.getCorrelations()) {
                for (RelatedCorrelation related : pair.related()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(pair.x());
                    row.createCell(1).setCellValue(related.y());
                    row.createCell(2).setCellValue(Double.parseDouble(String.valueOf(related.r())));
                }
            }

            // Save file
            try (FileOutputStream out = new FileOutputStream(excelFile)) {
                workbook.write(out);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel: " + e.getMessage(), e);
        }
    }

}
