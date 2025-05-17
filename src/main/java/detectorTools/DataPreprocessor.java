package detectorTools;

import java.util.List;
import java.util.stream.Collectors;

public class DataPreprocessor {
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

    // 统一分位数计算方法（网页3+网页6方法）
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
