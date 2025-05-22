/**
 * Pure Java implementation of Kernel Density Estimation.
 */
package detectorTools;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class PureJavaKDEAnomalyDetector {
    private final List<Transaction> transactions;
    private final List<Double> data;
    private final double bandwidth;
    private final TimeSensitiveAdjuster timeAdjuster = new TimeSensitiveAdjuster();

    /**
     * Constructs KDE estimator from input data.
     * <p>
     * Accepts both raw numeric values and {@link Transaction} objects.
     *
     * @param inputData dataset for density estimation
     * @throws IllegalArgumentException for empty datasets
     */
    public PureJavaKDEAnomalyDetector(List<?> inputData) {
        if (inputData.isEmpty()) throw new IllegalArgumentException("Input data is empty");

        if (inputData.get(0) instanceof Transaction) {
            this.transactions = (List<Transaction>) inputData;
            this.data = DataPreprocessor.extractValues(inputData);
        } else {
            this.data = (List<Double>) inputData;
            this.transactions = Collections.emptyList();
        }
        this.bandwidth = BandwidthCalculator.calculateBandwidth(data);
    }


    /**
     * Estimates probability density at given point.
     *
     * @param x target evaluation point
     * @return estimated density value at x
     */
    // KDE密度估计（网页3算法Java重写）
    public double estimateDensity(double x) {
        double sum = 0.0;
        for (Double point : data) {
            sum += KernelUtils.scaledKernel((x - point), bandwidth);
        }
        return sum / data.size();
    }
}



