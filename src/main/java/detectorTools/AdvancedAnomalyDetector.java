package detectorTools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdvancedAnomalyDetector {
    // 时间敏感阈值调整器（网页4方案增强）
    private final TimeSensitiveAdjuster thresholdAdjuster;

    // 多维度统计模型（网页6概率密度+网页7混合分布）
    private final PureJavaKDEAnomalyDetector kdeDetector;
    private final StatisticalThresholdCalculator statisticalThreshold;
    // 在AdvancedAnomalyDetector中增加：
    private List<Double> extractAmounts(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAmount)  // 提取金额字段 [1,4](@ref)
                .collect(Collectors.toList());  // 转换为 List<Double> [1](@ref)
    }

    public AdvancedAnomalyDetector(List<Transaction> transactions) {
        // 数据预处理（网页8特征工程）
        List<Double> amounts = extractAmounts(transactions);
        this.kdeDetector = new PureJavaKDEAnomalyDetector(amounts);
        this.statisticalThreshold = new StatisticalThresholdCalculator(amounts);
        this.thresholdAdjuster = new TimeSensitiveAdjuster();
    }

    // 异常检测主逻辑（网页4+网页6方法融合）
    public List<Double> detectAnomalies(List<Transaction> transactions) {
        List<Double> anomalies = new ArrayList<>();

        double baseThreshold = statisticalThreshold.calculateDynamicThreshold();
        // 基础阈值：Q3 + 1.5*IQR（网页1统计方法）

        transactions.forEach(tx -> {
            double dynamicThreshold = thresholdAdjuster.adjustThreshold(
                    Math.min(baseThreshold, kdeDetector.estimateDensity(tx.getAmount())),
                    tx.getTimestamp()
            );

            if (tx.getAmount() > dynamicThreshold) {
                anomalies.add(tx.getAmount());
            }
        });
        return anomalies;
    }
}