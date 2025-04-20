package detectorTools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdvancedAnomalyDetector {
    // 时间敏感阈值调整器（网页4方案增强）
    private final TimeSensitiveAdjuster thresholdAdjuster;

    // 多维度统计模型
    private final PureJavaKDEAnomalyDetector kdeDetector;
    private final StatisticalThresholdCalculator statisticalThreshold;
    // 在AdvancedAnomalyDetector中增加：
    private List<Double> extractAmounts(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAmount)  // 提取金额字段
                .collect(Collectors.toList());  // 转换为 List<Double>
    }

    public AdvancedAnomalyDetector(List<Transaction> transactions) {
        // 数据预处理（网页8特征工程）
        List<Double> amounts = extractAmounts(transactions);
        this.kdeDetector = new PureJavaKDEAnomalyDetector(amounts);
        this.statisticalThreshold = new StatisticalThresholdCalculator(amounts);
        this.thresholdAdjuster = new TimeSensitiveAdjuster();
    }

    // 异常检测主逻辑
    public List<Double> detectAnomalies(List<Transaction> transactions) {
        List<Double> anomalies = new ArrayList<>();

        double baseThreshold = statisticalThreshold.calculateDynamicThreshold();
        // 基础阈值：Q3 + 1.5*IQR

        transactions.forEach(tx -> {
            double amount = tx.getAmount();

            // 新增：KDE密度估计与置信度计算
            double kdeDensity = kdeDetector.estimateDensity(amount);
            double kdeConfidence = kdeDensity > 0.05 ? 1.0 : 0.3; // 密度<0.05时降权

            // 新增：混合阈值计算
            double hybridThreshold = baseThreshold * 0.7 + kdeDensity * 0.3;

            // 时间敏感调整
            double dynamicThreshold = thresholdAdjuster.adjustThreshold(
                    hybridThreshold,  // 替换原min()方法
                    tx.getTimestamp()
            );

            if (amount > dynamicThreshold) {
                anomalies.add(amount);
            }
        });
        return anomalies;
    }
}