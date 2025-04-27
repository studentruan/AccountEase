import AIUtilities.classification.ClassificationXmlWriter;
import AIUtilities.classification.Transaction;
import AIUtilities.classification.TransactionClassifier;
import AIUtilities.classification.TransactionXmlParser;

import java.nio.file.*;
import java.util.*;

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;
import DataProcessor.TransactionAnalyzer;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. 初始化分类器
            Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
            TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

            // 2. 解析 XML 文件
            String xmlFilePath = "src/main/resources/transactions.xml";
            List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

            // 3.1 批量分类
            Map<Transaction, Map<String,String>> categorized = classifier.classifyBatch(transactions);
            // 4. 将分类结果写回XML文件
            ClassificationXmlWriter.writeClassifications(xmlFilePath, "src/main/output/classified_transactions.xml" ,categorized);
            // 3.2 单个分类（比如手动输入数据）
//            String transaction = "";
//            String category = classifier.classify(transaction);

            // 4. 打印结果
            categorized.forEach((tx, cate) -> {
                System.out.println("--- Transaction ---");
                System.out.println(tx);

                String enrichedText = cate.keySet().iterator().next();   // enriched text
                String predictedCategory = cate.get(enrichedText);       // category

                System.out.println("Input text: " + enrichedText);
                System.out.println("Predicted Category: " + predictedCategory + "\n");
            });

            // 6. 关闭分类器
            classifier.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/transactions.xml");
        analyzer.getExpenseDailySummary().forEach((date, amount) ->
                System.out.println(date + ": " + amount));
        Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);
        System.out.println("异常高消费日期：");
        anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));
    }
}
