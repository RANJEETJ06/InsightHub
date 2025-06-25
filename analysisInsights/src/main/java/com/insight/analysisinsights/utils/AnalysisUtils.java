package com.insight.analysisinsights.utils;

import com.insight.analysisinsights.models.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnalysisUtils {

    // ✅ 1. Null Counts
    public static Map<String, Long> computeNullCounts(List<Map<String, Object>> data) {
        Map<String, Long> nullCounts = new HashMap<>();
        if (data.isEmpty()) return nullCounts;

        for (Map<String, Object> row : data) {
            for (var entry : row.entrySet()) {
                if (entry.getValue() == null) {
                    nullCounts.merge(entry.getKey(), 1L, Long::sum);
                }
            }
        }
        return nullCounts;
    }

    // ✅ 2. Numeric Summary Stats
    public static Map<String, NumericSummary> numericSummaries(List<Map<String, Object>> rows) {
        Map<String, List<Double>> numericData = extractNumericColumns(rows);
        Map<String, NumericSummary> result = new HashMap<>();

        for (var entry : numericData.entrySet()) {
            List<Double> values = entry.getValue();
            if (values.isEmpty()) continue;

            Collections.sort(values);
            double min = values.get(0);
            double max = values.get(values.size() - 1);
            double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double median = values.size() % 2 == 0
                    ? (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2.0
                    : values.get(values.size() / 2);
            double std = Math.sqrt(
                    values.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().orElse(0.0)
            );

            result.put(entry.getKey(), new NumericSummary(min, max, mean, median, std));
        }

        return result;
    }

    // ✅ 3. Categorical Top-K with "__OTHER__"
    public static Map<String, List<ValueCount>> categoricalTopK(
            List<Map<String, Object>> rows,
            int k,
            Set<String> smallCategoricalColumns // <-- Pass an empty set and this will be filled
    ) {
        Map<String, Map<String, Long>> frequencyMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            for (var entry : row.entrySet()) {
                Object val = entry.getValue();
                if (!(val instanceof Number)) {
                    String strVal = val == null ? "null" : val.toString().trim();
                    frequencyMap
                            .computeIfAbsent(entry.getKey(), c -> new HashMap<>())
                            .merge(strVal, 1L, Long::sum);
                }
            }
        }

        Map<String, List<ValueCount>> result = new HashMap<>();

        for (var entry : frequencyMap.entrySet()) {
            List<ValueCount> sorted = entry.getValue().entrySet().stream()
                    .map(e -> new ValueCount(e.getKey(), e.getValue()))
                    .sorted((a, b) -> Long.compare(b.count(), a.count()))
                    .toList();

            List<ValueCount> topK = new ArrayList<>();
            long otherCount = 0;
            for (int i = 0; i < sorted.size(); i++) {
                if (i < k) topK.add(sorted.get(i));
                else otherCount += sorted.get(i).count();
            }

            if (otherCount > 0) {
                topK.add(new ValueCount("__OTHER__", otherCount));
            }

            result.put(entry.getKey(), topK);

            // ✅ Add column to set if it has ≤12 unique non-OTHER values
            long uniqueCount = topK.stream()
                    .filter(v -> !v.value().equals("__OTHER__"))
                    .count();
            if (uniqueCount <= 12) {
                smallCategoricalColumns.add(entry.getKey());
            }
        }

        return result;
    }

    // ✅ 4. Temporal Coverage (Date column auto-detection)
    public static TemporalCoverage detectDateRange(List<Map<String, Object>> rows) {
        Map<String, List<LocalDate>> dateCandidates = new HashMap<>();

        for (Map<String, Object> row : rows) {
            for (var entry : row.entrySet()) {
                String col = entry.getKey();
                LocalDate parsed = tryParseDate(entry.getValue());
                if (parsed != null) {
                    dateCandidates
                            .computeIfAbsent(col, k -> new ArrayList<>())
                            .add(parsed);
                }
            }
        }

        for (var entry : dateCandidates.entrySet()) {
            List<LocalDate> dates = entry.getValue();
            if (dates.size() > 5) {
                return new TemporalCoverage(
                        entry.getKey(),
                        Collections.min(dates),
                        Collections.max(dates)
                );
            }
        }

        return null;
    }

    private static LocalDate tryParseDate(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        try {
            return LocalDate.parse(str); // ISO
        } catch (Exception ignored1) {}
        try {
            return LocalDate.parse(str, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception ignored2) {}
        try {
            long epochMillis = Long.parseLong(str);
            return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception ignored3) {}
        return null;
    }

    // ✅ 5. Strong Correlations (Pearson)
    public static List<CorrelationPair> strongCorrelations(List<Map<String, Object>> rows,
                                                           double threshold) {

        // 1️⃣  Extract all numeric columns → Map<columnName, List<Double>>
        Map<String, List<Double>> numericColumns = extractNumericColumns(rows);
        List<String> cols = new ArrayList<>(numericColumns.keySet());

        // 2️⃣ Prepare a grouping map: x ➜ List<RelatedCorrelation>
        Map<String, List<RelatedCorrelation>> grouped = new HashMap<>();

        // 3️⃣ Pair-wise Pearson correlations
        for (int i = 0; i < cols.size(); i++) {
            for (int j = i + 1; j < cols.size(); j++) {

                String col1 = cols.get(i);
                String col2 = cols.get(j);

                List<Double> xList = numericColumns.get(col1);
                List<Double> yList = numericColumns.get(col2);

                int n = Math.min(xList.size(), yList.size());
                if (n < 5) continue;   // not enough data

                double[] x = xList.stream().limit(n).mapToDouble(Double::doubleValue).toArray();
                double[] y = yList.stream().limit(n).mapToDouble(Double::doubleValue).toArray();

                double r = pearsonCorrelation(x, y);

                if (Math.abs(r) >= threshold) {
                    // add col2 as a related variable for col1
                    grouped
                            .computeIfAbsent(col1, k -> new ArrayList<>())
                            .add(new RelatedCorrelation(col2, r));

                    // OPTIONAL: also store the reverse (makes graph/h-map symmetrical)
                    grouped
                            .computeIfAbsent(col2, k -> new ArrayList<>())
                            .add(new RelatedCorrelation(col1, r));
                }
            }
        }

        // 4️⃣  Convert the map into List<CorrelationPair>
        List<CorrelationPair> result = new ArrayList<>();
        grouped.forEach((x, relatedList) -> result.add(new CorrelationPair(x, relatedList)));

        return result;
    }

    private static double pearsonCorrelation(double[] x, double[] y) {
        int n = x.length;
        if (n != y.length || n < 2) return 0;

        double meanX = Arrays.stream(x).average().orElse(0);
        double meanY = Arrays.stream(y).average().orElse(0);

        double numerator = 0, sumSqX = 0, sumSqY = 0;

        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            numerator += dx * dy;
            sumSqX += dx * dx;
            sumSqY += dy * dy;
        }

        return (sumSqX == 0 || sumSqY == 0) ? 0 : numerator / Math.sqrt(sumSqX * sumSqY);
    }

    // 🔄 Shared util: Extract numeric data columns
    private static Map<String, List<Double>> extractNumericColumns(List<Map<String, Object>> rows) {
        Map<String, List<Double>> numericData = new HashMap<>();

        for (Map<String, Object> row : rows) {
            for (var entry : row.entrySet()) {
                Object val = entry.getValue();
                try {
                    double num = val instanceof Number
                            ? ((Number) val).doubleValue()
                            : Double.parseDouble(val.toString());
                    numericData.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(num);
                } catch (Exception ignored) {}
            }
        }

        return numericData;
    }
}
