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

    public PureJavaKDEAnomalyDetector(List<?> inputData) {
        if (inputData.isEmpty()) throw new IllegalArgumentException("Input data is empty");

        if (inputData.get(0) instanceof Transaction) {
            this.transactions = (List<Transaction>) inputData;
            this.data = transactions.stream()
                    .map(Transaction::getAmount)
                    .collect(Collectors.toList());
        } else {
            this.data = (List<Double>) inputData;
            this.transactions = Collections.emptyList();
        }
        this.bandwidth = BandwidthCalculator.calculateBandwidth(data);
    }



    // KDE密度估计
    public double estimateDensity(double x) {
        double sum = 0.0;
        for (Double point : data) {
            sum += KernelUtils.scaledKernel((x - point), bandwidth);
        }
        return sum / data.size();
    }

    // 动态异常检测
    public List<Double> detectHighSpendingAnomalies(List<Transaction> transactions) {
        List<Double> anomalies = new ArrayList<>();
        double q3 = calculateQ3(data);
        double iqrThreshold = q3 + 1.5 * (q3 - calculateQ1(data));

        transactions.forEach(tx -> {
            double kdeDensity = estimateDensity(tx.getAmount());
            double dynamicThreshold = TimeSensitiveAdjuster.adjustThreshold(
                    Math.min(iqrThreshold, kdeDensity),
                    tx.getTimestamp()
            );

            if (tx.getAmount() > dynamicThreshold) {
                anomalies.add(tx.getAmount());
            }
        });
        return anomalies;
    }

    // 四分位数计算
    private double calculateQ1(List<Double> data) {
        Collections.sort(data);
        int index = (int) Math.ceil(0.25 * data.size()) - 1;
        return data.get(index);
    }

    private double calculateQ3(List<Double> data) {
        Collections.sort(data);
        int index = (int) Math.ceil(0.75 * data.size()) - 1;
        return data.get(index);
    }
}
// 后续开发中
class TimeSensitiveAdjuster {
    // 未来用于处理节假日以及周末
    // TimeSensitiveAdjuster类中
    public static double adjustThreshold(double threshold, LocalDateTime timestamp) {
        return threshold;  // 时间参数无实际作用，仅保持方法签名兼容
    }
}

