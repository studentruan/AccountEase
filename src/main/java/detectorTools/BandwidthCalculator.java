/**
 * Provides bandwidth calculation utilities using Silverman's rule for kernel density estimation.
 */

package detectorTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static detectorTools.DataPreprocessor.calculateQuantile;

public class BandwidthCalculator {
    /**
     * Calculates optimal bandwidth using Silverman's rule for kernel density estimation.
     * <p>
     * Implementation steps:
     * 1. Compute standard deviation of the dataset
     * 2. Calculate interquartile range (IQR)
     * 3. Apply Silverman's formula: {@code 0.9 * min(σ, IQR/1.34) * n^(-0.2)}
     *
     * @param data input dataset containing numerical values
     * @return calculated bandwidth value
     * @throws IllegalArgumentException if data contains less than 2 elements
     */
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

        // 3. 带宽参数计算
        double a = Math.min(std, iqr / 1.34);
        int n = data.size();
        return 0.9 * a * Math.pow(n, -0.2);
    }

    /**
     * Computes arithmetic mean of numerical values.
     *
     * @param data collection of numerical values
     * @return mean value, returns 0 for empty collections
     */
    private static double calculateMean(List<Double> data) {
        return data.stream().mapToDouble(d -> d).average().orElse(0);
    }
    /**
     * Computes population variance.
     *
     * @param data collection of numerical values
     * @param mean precomputed mean value
     * @return variance value, returns 0 for empty collections
     */
    private static double calculateVariance(List<Double> data, double mean) {
        return data.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0);
    }


}
