package detectorTools;

import DataProcessor.TransactionAnalyzer;

import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;

public class DetectorTest {
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/transactions.xml");
        analyzer.getExpenseDailySummary().forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
        Map<String, Double> anomalies = detectAnomalies(analyzer);
        anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
    }
}
