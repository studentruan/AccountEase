package detectorTools;

import DataProcessor.TransactionAnalyzer;

import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;

public class DetectorTest {
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/transactions.xml");

        Map<String, Double> anomalies = outputOutliers(detectAnomalies(analyzer), 1.5);
        anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
    }
}
