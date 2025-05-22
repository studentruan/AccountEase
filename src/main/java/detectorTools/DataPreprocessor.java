/**
 * Provides data preprocessing utilities for anomaly detection systems.
 */
package detectorTools;

import java.util.List;
import java.util.stream.Collectors;

public class DataPreprocessor {
    /**
     * Extracts numerical values from heterogeneous collections.
     * <p>
     * Supported types:
     * <ul>
     *   <li>{@link Transaction} objects (extracts amount field)</li>
     *   <li>{@link Double} primitive values</li>
     * </ul>
     *
     * @param data input list containing supported types
     * @return list of extracted numerical values
     * @throws IllegalArgumentException for unsupported data types
     */
    // 通用数据提取方法（支持Transaction和原始数值）
    public static List<Double> extractValues(List<?> data) {
        return data.stream()
                .map(item -> {
                    if (item instanceof Transaction) {
                        return ((Transaction) item).getAmount();
                    } else if (item instanceof Double) {
                        return (Double) item;
                    }
                    throw new IllegalArgumentException("Unsupported data type");
                })
                .collect(Collectors.toList());
    }
    /**
     * Computes quantile value using linear interpolation between nearest ranks.
     * <p>
     * Uses R-7 algorithm implementation for quantile estimation.
     *
     * @param data sorted list of numerical values
     * @param percentile target quantile position (0.0 ≤ percentile ≤ 1.0)
     * @return interpolated quantile value
     * @throws IllegalArgumentException for empty data or invalid percentiles
     */
    // 统一分位数计算方法
    public static double calculateQuantile(List<Double> data, double percentile) {
        List<Double> sorted = data.stream().sorted().collect(Collectors.toList());
        int n = sorted.size();
        double pos = (n - 1) * percentile;
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);
        double fraction = pos - lower;
        return sorted.get(lower) * (1 - fraction) + sorted.get(upper) * fraction;
    }
}
