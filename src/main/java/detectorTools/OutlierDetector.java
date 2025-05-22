/**
 * Statistical anomaly detection system using hybrid thresholding.
 * Provides a way to use the detection functionality
 */
package detectorTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import DataProcessor.TransactionAnalyzer;

public class OutlierDetector {

    private static Map<String, Double> convertToTargetMap(Map<LocalDate, BigDecimal> originalMap) {
        return originalMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)  // 过滤空值
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(DateTimeFormatter.ISO_DATE),  // LocalDate→String
                        entry -> entry.getValue().doubleValue()                       // BigDecimal→Double
                ));
    }
    private static Map<String, Double> normalizeData(Map<String, Double> rawData) {
        double mean = rawData.values().stream().mapToDouble(d->d).average().orElse(0);
        double std = Math.sqrt(rawData.values().stream()
                .mapToDouble(d -> Math.pow(d - mean, 2)).average().orElse(0));
        return rawData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (e.getValue() - mean) / std
                ));
    }



    /**
     * Detects anomalous transactions using normalized KDE thresholds.
     * <p>
     * Workflow:
     * 1. Convert transaction data to daily summaries
     * 2. Normalize transaction amounts
     * 3. Calculate dynamic thresholds using IQR method
     * 4. Apply time-sensitive threshold adjustments
     *
     * @param analyzer transaction data source
     * @return map of detected anomalies (date → normalized amount)
     */
    // 静态方法直接处理外部数据
    public static Map<String, Double> detectAnomalies(TransactionAnalyzer analyzer) {
        Map<String, Double> dailyData = convertToTargetMap(analyzer.getExpenseDailySummary());
        dailyData = normalizeData(dailyData);
        List<Double> amounts = new ArrayList<>(dailyData.values());



        // 核心检测逻辑（网页7 KDE + 动态阈值）
        StatisticalThresholdCalculator calculator = new StatisticalThresholdCalculator(amounts);
        PureJavaKDEAnomalyDetector kde = new PureJavaKDEAnomalyDetector(amounts);
        double baseThreshold = calculator.calculateDynamicThreshold();

        // 时间敏感调整
        TimeSensitiveAdjuster adjuster = new TimeSensitiveAdjuster();

        return dailyData.entrySet().stream()
                .filter(entry -> isAnomaly(entry.getValue(), entry.getKey(), baseThreshold, kde, adjuster))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    /**
     * Holiday-aware anomaly detection variant.
     *
     * @param analyzer transaction data source
     * @param holidaylist custom holidays in MM-DD format
     * @return map of detected anomalies with holiday adjustments
     */
    public static Map<String, Double> detectAnomalies(TransactionAnalyzer analyzer, List<String> holidaylist) {
        Map<String, Double> dailyData = convertToTargetMap(analyzer.getExpenseDailySummary());
        dailyData = normalizeData(dailyData);
        List<Double> amounts = new ArrayList<>(dailyData.values());



        // 核心检测逻辑（网页7 KDE + 动态阈值）
        StatisticalThresholdCalculator calculator = new StatisticalThresholdCalculator(amounts);
        PureJavaKDEAnomalyDetector kde = new PureJavaKDEAnomalyDetector(amounts);
        double baseThreshold = calculator.calculateDynamicThreshold();

        // 时间敏感调整
        TimeSensitiveAdjuster adjuster = new TimeSensitiveAdjuster();
        adjuster.loadHolidaysFromList(holidaylist);

        return dailyData.entrySet().stream()
                .filter(entry -> isAnomaly(entry.getValue(), entry.getKey(), baseThreshold, kde, adjuster))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    // 异常判断逻辑（网页5自定义异常处理的精简版）
    static boolean isAnomaly(double amount, String date, double baseThreshold,
                             PureJavaKDEAnomalyDetector kde, TimeSensitiveAdjuster adjuster) {
        LocalDateTime timestamp = LocalDate.parse(date).atStartOfDay();
        double dynamicThreshold = adjuster.adjustThreshold(
                baseThreshold,
                timestamp
        );
        double hybirdThreshold = 0.4 * dynamicThreshold + 0.6 * kde.estimateDensity(amount);
        //System.out.println(hybirdThreshold + ": " + amount +"\n");
        return amount > hybirdThreshold;
    }
}
