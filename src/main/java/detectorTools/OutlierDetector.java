package detectorTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import TransectionAnalyzer.TransactionAnalyzer;


public class OutlierDetector {
    private static Map<String, Double> convertToTargetMap(Map<LocalDate, BigDecimal> originalMap) {
        return originalMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)  // 过滤空值（参考网页5空值处理）
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(DateTimeFormatter.ISO_DATE),  // LocalDate→String（网页4日期格式化）
                        entry -> entry.getValue().doubleValue()                       // BigDecimal→Double（网页10数值转换）
                ));
    }
    // 静态方法直接处理外部数据（网页1静态类集成方案）
    public static Map<String, Double> detectAnomalies(TransactionAnalyzer analyzer) {
        Map<String, Double> dailyData = convertToTargetMap(analyzer.getDailySummary());
        List<Double> amounts = new ArrayList<>(dailyData.values());

        // 核心检测逻辑（网页7 KDE + 动态阈值）
        StatisticalThresholdCalculator calculator = new StatisticalThresholdCalculator(amounts);
        PureJavaKDEAnomalyDetector kde = new PureJavaKDEAnomalyDetector(amounts);
        double baseThreshold = calculator.calculateDynamicThreshold();

        // 时间敏感调整（网页9时间API整合）
        TimeSensitiveAdjuster adjuster = new TimeSensitiveAdjuster();

        return dailyData.entrySet().stream()
                .filter(entry -> isAnomaly(entry.getValue(), entry.getKey(), baseThreshold, kde, adjuster))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 异常判断逻辑（网页5自定义异常处理的精简版）
    private static boolean isAnomaly(double amount, String date, double baseThreshold,
                                     PureJavaKDEAnomalyDetector kde, TimeSensitiveAdjuster adjuster) {
        LocalDateTime timestamp = LocalDate.parse(date).atStartOfDay();
        double dynamicThreshold = adjuster.adjustThreshold(
                Math.min(baseThreshold, kde.estimateDensity(amount)),
                timestamp
        );
        return amount > dynamicThreshold;
    }
}
