import org.classification.ClassificationXmlWriter;
import org.classification.Transaction;
import org.classification.TransactionClassifier;
import org.classification.TransactionXmlParser;

import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. 初始化分类器
            Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath);

            // 2. 解析 XML 文件
            String xmlFilePath = "src/main/resources/transactions.xml";
            List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

            // 3. 批量分类
            Map<Transaction, String> categorized = classifier.classifyBatch(transactions);

            // 4. 打印结果
            categorized.forEach((tx, category) -> {
                System.out.println("--- Transaction ---");
                System.out.println(tx);
                System.out.println("Predicted Category: " + category + "\n");
            });

            // 5. 将分类结果写回XML文件
            ClassificationXmlWriter.writeClassifications(xmlFilePath, categorized);

            // 6. 关闭分类器
            classifier.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}