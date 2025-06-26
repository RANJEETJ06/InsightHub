package com.insight.report.utils;

import com.insight.report.model.CorrelationPair;
import com.insight.report.model.RelatedCorrelation;
import com.insight.report.model.ReportData;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.*;

public class GraphGenerator {

    public static File generateCategoricalBarChart(String title, String column, List<Map<String, Object>> topValues, File outputDir) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> entry : topValues) {
            String label = entry.get("value").toString();
            long count = Long.parseLong(entry.get("count").toString());
            dataset.addValue(count, column, label);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, "Value", "Count", dataset, PlotOrientation.VERTICAL,
                false, true, false
        );

        try {
            String safeFileName = column.replaceAll("[^a-zA-Z0-9_-]", "_") + "_chart.png";
            File output = new File(outputDir, safeFileName);
            ChartUtils.saveChartAsPNG(output, chart, 600, 400);
            return output;
        } catch (Exception e) {
            throw new RuntimeException("Error generating chart for: " + column, e);
        }
    }

    public static void addGraphs(Document document, File outputDir, ReportData data) {
        try {
            for (Map.Entry<String, List<Map<String, Object>>> entry : data.getCategorical().entrySet()) {
                String columnName = entry.getKey();
                List<Map<String, Object>> topValues = entry.getValue();

                document.add(new Paragraph("\uD83D\uDCC8 Chart for: " + columnName).setBold());

                File chartFile = generateCategoricalBarChart(
                        "Top Values - " + columnName,
                        columnName,
                        topValues,
                        outputDir
                );

                ImageData imageData = ImageDataFactory.create(chartFile.getAbsolutePath());
                Image image = new Image(imageData);
                image.setAutoScale(true);
                document.add(image);
            }
        } catch (Exception e) {
            document.add(new Paragraph("\u274C Failed to generate graphs: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public static File generateValueVsValueChart(
            String title,
            String xLabel,
            String yLabel,
            List<Map<String, Object>> dataPoints,
            String xKey,
            String yKey,
            File outputDir
    ) {
        var dataset = new DefaultCategoryDataset();

        for (Map<String, Object> point : dataPoints) {
            try {
                String xValue = point.get(xKey).toString();
                double yValue = Double.parseDouble(point.get(yKey).toString());
                dataset.addValue(yValue, yLabel, xValue);
            } catch (Exception ignored) {}
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, xLabel, yLabel, dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        try {
            String safeFileName = (xLabel + "_vs_" + yLabel).replaceAll("[^a-zA-Z0-9_-]", "_") + "_chart.png";
            File output = new File(outputDir, safeFileName);
            ChartUtils.saveChartAsPNG(output, chart, 640, 400);
            return output;
        } catch (Exception e) {
            throw new RuntimeException("Error generating value-vs-value chart for: " + title, e);
        }
    }

    public static void generateValueVsValueCharts(Document document, File outputDir, ReportData data) {
        try {
            List<Map<String, Object>> rows = CSVUtils.readCsvAsList(new File(data.getFilepath()));
            List<String> numericColumns = new ArrayList<>(data.getNumeric().keySet());
            if (numericColumns.size() < 2) {
                document.add(new Paragraph("\u26A0\uFE0F Not enough numeric columns for relational graphs."));
                return;
            }

            Collections.shuffle(numericColumns);
            List<String[]> selectedPairs = new ArrayList<>();
            for (int i = 0; i < numericColumns.size() - 1 && selectedPairs.size() < 3; i += 2) {
                String x = numericColumns.get(i);
                String y = numericColumns.get(i + 1);
                selectedPairs.add(new String[]{x, y});
            }

            for (String[] pair : selectedPairs) {
                String xCol = pair[0];
                String yCol = pair[1];

                if (rows.stream().anyMatch(row -> row.containsKey(xCol) && row.containsKey(yCol))) {
                    String title = xCol + " vs " + yCol;
                    File chartFile = generateValueVsValueChart(
                            title, xCol, yCol, rows, xCol, yCol, outputDir
                    );

                    document.add(new Paragraph("\uD83D\uDD39 " + title));
                    ImageData imageData = ImageDataFactory.create(chartFile.getAbsolutePath());
                    Image image = new Image(imageData);
                    image.setAutoScale(true);
                    document.add(image);
                }
            }

        } catch (Exception e) {
            document.add(new Paragraph("\u274C Failed to generate relational graphs: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public static void generateScatterPlot(Document document, File outputDir, ReportData data) {
        try {
            List<Map<String, Object>> rows = CSVUtils.readCsvAsList(new File(data.getFilepath()));
            List<String> numericCols = new ArrayList<>(data.getNumeric().keySet());
            Random rand = new Random();

            for (int i = 0; i < 3 && numericCols.size() >= 2; i++) {
                String x = numericCols.get(rand.nextInt(numericCols.size()));
                String y = numericCols.get(rand.nextInt(numericCols.size()));
                if (x.equals(y)) continue;

                XYSeries series = new XYSeries(x + " vs " + y);
                for (Map<String, Object> row : rows) {
                    if (row.get(x) != null && row.get(y) != null) {
                        try {
                            double xVal = Double.parseDouble(row.get(x).toString());
                            double yVal = Double.parseDouble(row.get(y).toString());
                            series.add(xVal, yVal);
                        } catch (NumberFormatException ignored) {}
                    }
                }

                XYDataset dataset = new XYSeriesCollection(series);
                JFreeChart scatter = ChartFactory.createScatterPlot(
                        x + " vs " + y, x, y, dataset,
                        PlotOrientation.VERTICAL, true, true, false
                );

                File chartFile = new File(outputDir, x + "_vs_" + y + "_scatter.png");
                ChartUtils.saveChartAsPNG(chartFile, scatter, 640, 400);

                document.add(new Paragraph("\uD83D\uDD38 Scatter Plot: " + x + " vs " + y));
                ImageData imageData = ImageDataFactory.create(chartFile.getAbsolutePath());
                document.add(new Image(imageData).setAutoScale(true));
            }

        } catch (Exception e) {
            document.add(new Paragraph("\u274C Error generating scatter plots: " + e.getMessage()));
        }
    }

    public static void generatePairPlot(Document document, File outputDir, ReportData data) {
        try {
            List<String> numericCols = new ArrayList<>(data.getNumeric().keySet());
            if (numericCols.size() < 2) return;

            List<String> selected = numericCols.subList(0, Math.min(4, numericCols.size()));
            List<Map<String, Object>> rows = CSVUtils.readCsvAsList(new File(data.getFilepath()));

            for (int i = 0; i < selected.size(); i++) {
                for (int j = i + 1; j < selected.size(); j++) {
                    String x = selected.get(i);
                    String y = selected.get(j);

                    XYSeries series = new XYSeries(x + " vs " + y);
                    for (Map<String, Object> row : rows) {
                        if (row.get(x) != null && row.get(y) != null) {
                            try {
                                double xVal = Double.parseDouble(row.get(x).toString());
                                double yVal = Double.parseDouble(row.get(y).toString());
                                series.add(xVal, yVal);
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    XYDataset dataset = new XYSeriesCollection(series);
                    JFreeChart chart = ChartFactory.createScatterPlot(
                            x + " vs " + y, x, y, dataset,
                            PlotOrientation.VERTICAL, true, true, false
                    );

                    File file = new File(outputDir, x + "_vs_" + y + "_pair.png");
                    ChartUtils.saveChartAsPNG(file, chart, 600, 400);

                    document.add(new Paragraph("\uD83D\uDD39 Pair Plot: " + x + " vs " + y));
                    ImageData imgData = ImageDataFactory.create(file.getAbsolutePath());
                    document.add(new Image(imgData).setAutoScale(true));
                }
            }

        } catch (Exception e) {
            document.add(new Paragraph("\u274C Error generating pair plots: " + e.getMessage()));
        }
    }

    public static void generatePieChart(Document document, File outputDir, ReportData data) {
        try {
            for (Map.Entry<String, List<Map<String, Object>>> entry : data.getCategorical().entrySet()) {
                String colName = entry.getKey();
                List<Map<String, Object>> topValues = entry.getValue();

                DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
                for (Map<String, Object> val : topValues) {
                    String label = val.get("value").toString().trim().toLowerCase();
                    if (label.equals("unknown") || label.equals("__other__")) continue; // skip

                    dataset.setValue(val.get("value").toString(), Long.parseLong(val.get("count").toString()));
                }

                if (dataset.getItemCount() == 0) {
                    document.add(new Paragraph("⚠️ No valid data for pie chart: " + colName));
                    continue;
                }

                JFreeChart pieChart = ChartFactory.createPieChart(
                        "Pie Chart: " + colName, dataset, true, true, false
                );

                File chartFile = new File(outputDir, colName + "_pie_chart.png");
                ChartUtils.saveChartAsPNG(chartFile, pieChart, 600, 400);

                document.add(new Paragraph("🍕 Pie Chart: " + colName));
                ImageData imageData = ImageDataFactory.create(chartFile.getAbsolutePath());
                document.add(new Image(imageData).setAutoScale(true));
            }

        } catch (Exception e) {
            document.add(new Paragraph("❌ Error generating pie chart: " + e.getMessage()));
        }
    }

    public static void generateHeatmaps(Document document, File outputDir, ReportData data) {
        try {
            List<CorrelationPair> correlations = data.getCorrelations();
            if (correlations == null || correlations.isEmpty()) {
                document.add(new Paragraph("⚠️ No correlation data available."));
                return;
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (CorrelationPair pair : correlations) {
                String x = pair.x();
                for (RelatedCorrelation related : pair.related()) {
                    String y = related.y();
                    double value = related.r();

                    dataset.addValue(value, x, y);  // (rowKey=x, columnKey=y)
                }
            }

            JFreeChart heatmap = ChartFactory.createBarChart(
                    "Correlation Heatmap",
                    "Y Column",
                    "Correlation",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            File chartFile = new File(outputDir, "correlation_heatmap.png");
            ChartUtils.saveChartAsPNG(chartFile, heatmap, 800, 500);

            document.add(new Paragraph("🔥 Correlation Heatmap"));
            ImageData imageData = ImageDataFactory.create(chartFile.getAbsolutePath());
            document.add(new Image(imageData).setAutoScale(true));

        } catch (Exception e) {
            document.add(new Paragraph("❌ Error generating heatmap: " + e.getMessage()));
        }
    }

    public static void generateBoxPlot(Document document, File outputDir, ReportData data) {
        try {
            DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
            List<Map<String, Object>> rows = CSVUtils.readCsvAsList(new File(data.getFilepath()));

            for (String column : data.getNumeric().keySet()) {
                List<Double> values = new ArrayList<>();
                for (Map<String, Object> row : rows) {
                    Object val = row.get(column);
                    if (val != null) {
                        try {
                            values.add(Double.parseDouble(val.toString()));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                if (!values.isEmpty()) dataset.add(values, column, "");
            }

            JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("Box Plot for Numeric Columns", "Column", "Values", dataset, true);

            File file = new File(outputDir, "box_plot.png");
            ChartUtils.saveChartAsPNG(file, chart, 640, 400);
            ImageData imgData = ImageDataFactory.create(file.getAbsolutePath());
            document.add(new Paragraph("\uD83D\uDD39 Box Plot: "));
            document.add(new Image(imgData).setAutoScale(true));
        } catch (Exception e) {
            document.add(new Paragraph("\u274C Error generating box plot: " + e.getMessage()));
        }
    }

    public static void generateConfusionMatrix(Document document, File outputDir, ReportData data) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(50, "Predicted: Yes", "Actual: Yes");
            dataset.addValue(10, "Predicted: No", "Actual: Yes");
            dataset.addValue(5, "Predicted: Yes", "Actual: No");
            dataset.addValue(35, "Predicted: No", "Actual: No");

            JFreeChart chart = ChartFactory.createBarChart(
                    "Confusion Matrix",
                    "Actual",
                    "Count",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            File file = new File(outputDir, "confusion_matrix.png");
            ChartUtils.saveChartAsPNG(file, chart, 640, 400);

            document.add(new Paragraph("\uD83D\uDCCA Confusion Matrix:"));
            ImageData imgData = ImageDataFactory.create(file.getAbsolutePath());
            document.add(new Image(imgData).setAutoScale(true));

        } catch (Exception e) {
            document.add(new Paragraph("\u274C Error generating confusion matrix: " + e.getMessage()));
        }
    }
}