package detectorTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static detectorTools.DataPreprocessor.calculateQuantile;

public class BandwidthCalculator {
    // Silverman带宽选择规则
    public static double calculateBandwidth(List<Double> data) {
        // 1. 计算标准差
        double mean = calculateMean(data);
        double variance = calculateVariance(data, mean);
        double std = Math.sqrt(variance);

        // 2. 计算四分位距
        double q1 = calculateQuantile(data, 0.25);
        double q3 = calculateQuantile(data, 0.75);
        double iqr = q3 - q1;

        // 3. 带宽参数计算（网页8改进公式）
        double a = Math.min(std, iqr / 1.34);
        int n = data.size();
        return 0.9 * a * Math.pow(n, -0.2);
    }

    // 辅助方法（需全部实现）
    private static double calculateMean(List<Double> data) {
        return data.stream().mapToDouble(d -> d).average().orElse(0);
    }

    private static double calculateVariance(List<Double> data, double mean) {
        return data.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0);
    }


}
