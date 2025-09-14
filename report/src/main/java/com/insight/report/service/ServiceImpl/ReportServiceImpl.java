package com.insight.report.service.ServiceImpl;

import com.insight.report.model.CorrelationPair;
import com.insight.report.model.RelatedCorrelation;
import com.insight.report.model.ReportData;
import com.insight.report.service.ReportService;
import com.insight.report.utils.GraphGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// iText 7 imports
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Value("${app.report.dir:/app/reports}")
    private String reportDir;

    @Override
    public void generateReports(ReportData data) {
        try {
            // 1️⃣ Define the target folder path
            String baseName = data.getOriginalFilename().replaceAll("\\.[^.]+$", "");
            File folder = new File(reportDir,baseName);

            // 2️⃣ Create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();  // creates parent directories if needed
            }

            // 3️⃣ Create PDF and Excel files inside that folder
            File pdfFile = new File(folder, "report.pdf");

            // 4️⃣ Generate content
            generatePDF(data, pdfFile);

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

            //graphs
            document.add(new Paragraph("\n📊 Graphs:").setBold());
            GraphGenerator.addGraphs(document, pdfFile.getParentFile(), data);

            document.add(new Paragraph("\n📊 specific vs specific relational Graphs:").setBold());
            GraphGenerator.generateValueVsValueCharts(document, pdfFile.getParentFile(), data);

            document.add(new Paragraph("\n📊 specific vs specific plots").setBold());
            GraphGenerator.generateScatterPlot(document, pdfFile.getParentFile(), data);
            GraphGenerator.generatePairPlot(document, pdfFile.getParentFile(), data);

            document.add(new Paragraph("\n🍕 Pie Chart").setBold());
            GraphGenerator.generatePieChart(document, pdfFile.getParentFile(), data);

            document.add(new Paragraph("\n🔥 Heatmaps").setBold());
            GraphGenerator.generateHeatmaps(document, pdfFile.getParentFile(), data);

            document.add(new Paragraph("\n\uD83D\uDD39 Box Plot").setBold());
            GraphGenerator.generateBoxPlot(document, pdfFile.getParentFile(), data);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }



}
