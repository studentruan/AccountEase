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


    public AdvancedAnomalyDetector(List<Transaction> transactions) {
        // 数据预处理（网页8特征工程）
        List<Double> amounts = DataPreprocessor.extractValues(transactions);
        this.kdeDetector = new PureJavaKDEAnomalyDetector(amounts);
        this.statisticalThreshold = new StatisticalThresholdCalculator(amounts);
        this.thresholdAdjuster = new TimeSensitiveAdjuster();
    }
}