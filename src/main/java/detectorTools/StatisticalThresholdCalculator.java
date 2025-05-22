/**
 * Dynamic threshold calculator using statistical methods.
 */
package detectorTools;

import java.util.List;
import java.util.stream.Collectors;

import static detectorTools.DataPreprocessor.calculateQuantile;

public class StatisticalThresholdCalculator {
    private final List<Double> amounts;

    public StatisticalThresholdCalculator(List<Double> amounts) {
        this.amounts = amounts.stream()
                .sorted()
                .collect(Collectors.toList());
    }
    /**
     * Calculates upper outlier boundary using IQR method.
     * <p>
     * Formula: {@code Q3 + 1.5 * IQR}
     *
     * @return dynamic threshold value
     */
    // 动态阈值计算
    public double calculateDynamicThreshold() {
        double q1 = calculateQuantile(amounts,0.25);
        double q3 = calculateQuantile(amounts,0.75);
        double iqr = q3 - q1;

        // 异常边界
        return q3 + 1.5 * iqr;
    }


}