package detectorTools;
import java.util.List;

public class BandwidthCalculator {
    // Silverman带宽选择规则
    public static double calculateBandwidth(List<Double> data) {
        double std = calculateStd(data);
        int n = data.size();
        return 1.06 * std * Math.pow(n, -0.2);
    }

    private static double calculateStd(List<Double> data) {
        double mean = data.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0);

        double variance = data.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }
}
