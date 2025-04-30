package AIUtilities.classification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TransactionClassifierTest {
    private TransactionClassifier classifier;

    @BeforeEach
    public void setUp() throws Exception {
        Path tokenizerPath = Paths.get("src/main/resources/Tokenizer");
        String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
        Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
        this.classifier = new TransactionClassifier(tokenizerPath, modelPath, descriptionPath);
    }

    @Test
    public void testBatchClassificationAndXmlWrite() throws Exception {
        // 1. 解析 XML 原始交易记录
        String xmlFilePath = "src/main/resources/transactions.xml";
        List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

        assertFalse(transactions.isEmpty(), "Parsed transactions should not be empty");

        // 2. 分类
        Map<Transaction, Map<String,String>> results = classifier.classifyBatch(transactions);
        assertEquals(transactions.size(), results.size(), "All transactions should be classified");

        // 3. 检查分类结果合法性
        Map<String, String> idToCategory = new HashMap<>();
        results.forEach((tx, cateMap) -> {
            assertNotNull(cateMap, "Classification map should not be null");
            assertFalse(cateMap.isEmpty(), "Classification map should not be empty");

            String enrichedText = cateMap.keySet().iterator().next();
            String predictedCategory = cateMap.get(enrichedText);
            assertNotNull(predictedCategory, "Predicted category should not be null");
            assertFalse(predictedCategory.isBlank(), "Predicted category should not be blank");

            // 假设 Transaction 类有 getId() 方法
            idToCategory.put(tx.getId(), predictedCategory);
        });

        // 4. 写入分类结果到新 XML 文件
        String outputXmlPath = "src/main/output/classified_transactions.xml";
        ClassificationXmlWriter.writeClassifications(xmlFilePath, outputXmlPath, results);

        // 5. 读取写出的 XML 并验证内容
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(outputXmlPath));
        NodeList txNodes = doc.getElementsByTagName("transaction");

        assertEquals(transactions.size(), txNodes.getLength(), "Transaction count should match");

        for (int i = 0; i < txNodes.getLength(); i++) {
            Element tx = (Element) txNodes.item(i);
            String id = tx.getElementsByTagName("id").item(0).getTextContent();;

            NodeList categoryNodes = tx.getElementsByTagName("class");
            assertEquals(1, categoryNodes.getLength(), "<class> should be present");

            String actualCategory = categoryNodes.item(0).getTextContent();
            String expectedCategory = idToCategory.get(id);

            assertNotNull(expectedCategory, "Expected category should not be null");
            assertEquals(expectedCategory, actualCategory, "Category should match classification result");
        }
    }

    @Test
    public void testBatchClassificationWithEmptyInput() throws Exception {
        List<Transaction> emptyList = new ArrayList<>();
        Map<Transaction, Map<String,String>> result = classifier.classifyBatch(emptyList);
        assertTrue(result.isEmpty(), "Classification result should be empty for empty input");
    }

    @Test
    public void testBatchClassificationWithCorruptedTransaction() throws Exception {
        String transaction = "";

        Map<String, String> result = classifier.classify(transaction);
        assertEquals(1, result.size());
        String category = result.get("");
        assertNotNull(category);
    }
}
