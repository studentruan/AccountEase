import TransectionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;

public class Main {
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/transactions.xml");
        //每日净收支
        analyzer.getDailySummary().forEach((date, amount) ->
                System.out.println(date + ": " + amount));

        //每月净收支
        //analyzer.getMonthlySummary().forEach((month, amount) ->
                //System.out.println(month + ": " + amount));

        //每年净收支
        //analyzer.getYearlySummary().forEach((year, amount) ->
                //System.out.println(year + ": " + amount));

        Map<String, Double> anomalies = detectAnomalies(analyzer);
        System.out.println("异常高消费日期：");
        anomalies.forEach((date, amount) ->
                System.out.printf("%s: %.2f%n", date, amount));

    }
}
