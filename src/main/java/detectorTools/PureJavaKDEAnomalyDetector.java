package detectorTools;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class PureJavaKDEAnomalyDetector {
    private final List<Transaction> transactions;
    private final List<Double> data;
    private final double bandwidth;

    public PureJavaKDEAnomalyDetector(List<Transaction> transactions, List<Double> data) {
        this.transactions = transactions;
        this.data = data;
        this.bandwidth = BandwidthCalculator.calculateBandwidth(data);
    }

    public PureJavaKDEAnomalyDetector(List<Transaction> transactions, List<Double> data, double bandwidth) {
        this.transactions = transactions;  // 确保传入数据不为null
        this.data = data;
        this.bandwidth = bandwidth;
    }


    // KDE密度估计（网页3算法Java重写）
    public double estimateDensity(double x) {
        double sum = 0.0;
        for (Double point : data) {
            sum += KernelUtils.scaledKernel((x - point), bandwidth);
        }
        return sum / data.size();
    }

    // 动态异常检测（网页1、网页5方法融合）
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

    // 四分位数计算（纯Java实现）
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

class Transaction {
    private LocalDateTime timestamp;
    private double amount;

    public Transaction(LocalDateTime timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
}
