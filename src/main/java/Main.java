import org.classification.ClassificationXmlWriter;
import org.classification.Transaction;
import org.classification.TransactionClassifier;
import org.classification.TransactionXmlParser;

import java.nio.file.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;
import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. 初始化分类器
            Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath);

            // 2. 解析 XML 文件
            String xmlFilePath = "src/main/resources/xml/transactions1.xml";
            List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

            // 3.1 批量分类
            Map<Transaction, String> categorized = classifier.classifyBatch(transactions);

            // 3.2 单个分类（比如手动输入数据）
//            String transaction = "";
//            String category = classifier.classify(transaction);

            // 4. 打印结果
            categorized.forEach((tx, cate) -> {
                System.out.println("--- Transaction ---");
                System.out.println(tx);
                System.out.println("Predicted Category: " + cate + "\n");
            });

            // 5. 将分类结果写回XML文件
            ClassificationXmlWriter.writeClassifications(xmlFilePath, "src/main/output/classified_transactions1.xml" ,categorized);

            // 6. 关闭分类器
            classifier.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/xml/transactions1.xml");
        analyzer.getExpenseDailySummary().forEach((date, amount) ->
                System.out.println(date + ": " + amount));
        Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);
        System.out.println("异常高消费日期：");
        anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));
    }
}
