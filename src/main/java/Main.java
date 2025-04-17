import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;

public class Main {
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/transactions.xml");
        //每日净收支
        //analyzer.getDailySummary().forEach((date, amount) ->
          //      System.out.println(date + ": " + amount));

        //每月净收支
        //analyzer.getMonthlySummary().forEach((month, amount) ->
                //System.out.println(month + ": " + amount));

        //每年净收支
        //analyzer.getYearlySummary().forEach((year, amount) ->
                //System.out.println(year + ": " + amount));
        analyzer.getExpenseDailySummary().forEach((date, amount) ->
                System.out.println(date + ": " + amount));
        Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);
        System.out.println("异常高消费日期：");
        anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));

    }
}
