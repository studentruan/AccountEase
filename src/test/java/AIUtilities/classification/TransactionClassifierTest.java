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

/**
 * Unit tests for the {@link TransactionClassifier}, {@link ClassificationXmlWriter} and {@link TransactionXmlParser} class.
 * <p>
 * These tests verify the batch classification of transactions and writing results into XML,
 * including edge cases like empty input or corrupted transaction text.
 * @date 30 April
 * @author studentruan
 */
public class TransactionClassifierTest {
    private TransactionClassifier classifier;

    /**
     * Initializes the {@link TransactionClassifier} with tokenizer, model, and description data
     * before each test case is executed.
     *
     * @throws Exception if loading model or resources fails
     */
    @BeforeEach
    public void setUp() throws Exception {
        Path tokenizerPath = Paths.get("src/main/resources/Tokenizer");
        String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
        Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
        this.classifier = new TransactionClassifier(tokenizerPath, modelPath, descriptionPath);
    }

    /**
     * Tests the end-to-end flow: parsing XML input, classifying transactions,
     * writing results into a new XML file, and verifying correctness.
     *
     * @throws Exception if any parsing, classification, or file operation fails
     */
    @Test
    public void testBatchClassificationAndXmlWrite() throws Exception {
        // Step 1: Parse original XML transactions
        String xmlFilePath = "src/main/resources/transactions.xml";
        List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);
        assertFalse(transactions.isEmpty(), "Parsed transactions should not be empty");

        // Step 2: Perform classification
        Map<Transaction, Map<String, String>> results = classifier.classifyBatch(transactions);
        assertEquals(transactions.size(), results.size(), "All transactions should be classified");

        // Step 3: Check classification results for validity
        Map<String, String> idToCategory = new HashMap<>();
        results.forEach((tx, cateMap) -> {
            assertNotNull(cateMap, "Classification map should not be null");
            assertFalse(cateMap.isEmpty(), "Classification map should not be empty");

            String enrichedText = cateMap.keySet().iterator().next();
            String predictedCategory = cateMap.get(enrichedText);
            assertNotNull(predictedCategory, "Predicted category should not be null");
            assertFalse(predictedCategory.isBlank(), "Predicted category should not be blank");

            idToCategory.put(tx.getId(), predictedCategory);
        });

        // Step 4: Write classification results to a new XML file
        String outputXmlPath = "src/main/output/classified_transactions.xml";
        ClassificationXmlWriter.writeClassifications(xmlFilePath, outputXmlPath, results);

        // Step 5: Read the new XML and validate <class> tags
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(outputXmlPath));
        NodeList txNodes = doc.getElementsByTagName("transaction");

        assertEquals(transactions.size(), txNodes.getLength(), "Transaction count should match");

        for (int i = 0; i < txNodes.getLength(); i++) {
            Element tx = (Element) txNodes.item(i);
            String id = tx.getElementsByTagName("id").item(0).getTextContent();

            NodeList categoryNodes = tx.getElementsByTagName("class");
            assertEquals(1, categoryNodes.getLength(), "<class> should be present");

            String actualCategory = categoryNodes.item(0).getTextContent();
            String expectedCategory = idToCategory.get(id);

            assertNotNull(expectedCategory, "Expected category should not be null");
            assertEquals(expectedCategory, actualCategory, "Category should match classification result");
        }
    }

    /**
     * Tests that an empty list of transactions produces an empty classification result.
     *
     * @throws Exception if classification fails
     */
    @Test
    public void testBatchClassificationWithEmptyInput() throws Exception {
        List<Transaction> emptyList = new ArrayList<>();
        Map<Transaction, Map<String, String>> result = classifier.classifyBatch(emptyList);
        assertTrue(result.isEmpty(), "Classification result should be empty for empty input");
    }

    /**
     * Tests that a corrupted or empty transaction text can still be classified
     * and that a category is returned.
     *
     * @throws Exception if classification fails
     */
    @Test
    public void testBatchClassificationWithCorruptedTransaction() throws Exception {
        String transaction = "";

        Map<String, String> result = classifier.classify(transaction);
        assertEquals(1, result.size());
        String category = result.get("");
        assertNotNull(category);
    }
}
