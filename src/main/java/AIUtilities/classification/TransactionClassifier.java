package AIUtilities.classification;

/**
 * A classifier for transaction categories using Bert-ONNX model.
 * Providing two core methods: 1. Get a input transaction description and return its category
 * 2. Get a input XML file containing a series of transactions, and return a Map of all their categories
 *
 * @version 2.0
 * @date 26 April
 * @author studentruan
 */

import ai.onnxruntime.*;
import java.util.*;
import java.nio.LongBuffer;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import java.nio.file.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.NotNull;

public class TransactionClassifier {

    /**
     * Mapping of category IDs to their corresponding labels
     */
    private static final Map<Integer, String> CATEGORIES = new HashMap<Integer, String>() {{
        put(0, "Utilities");
        put(1, "Health");
        put(2, "Dining");
        put(3, "Travel");
        put(4, "Education");
        put(5, "Subscription");
        put(6, "Family");
        put(7, "Food");
        put(8, "Festivals");
        put(9, "Culture");
        put(10, "Apparel");
        put(11, "Transportation");
        put(12, "Investment");
        put(13, "Shopping");
        put(14, "Groceries");
        put(15, "Documents");
        put(16, "Grooming");
        put(17, "Entertainment");
        put(18, "Social Life");
        put(19, "Beauty");
        put(20, "Rent");
        put(21, "Money transfer");
        put(22, "Salary");
        put(23, "Tourism");
        put(24, "Household");
    }};

    private final HuggingFaceTokenizer tokenizer;
    private final OrtEnvironment env;
    private final OrtSession session;
    private final Map<String, String> merchantMap;

    /**
     * Initializes the TransactionClassifier with tokenizer， ONNX model and merchant description
     *
     * @param tokenizerDir Path to the tokenizer directory
     * @param modelPath Path to the ONNX model file
     * @param merchantMapPath Path to the merchant description
     * @throws Exception If initialization fails
     */
    public TransactionClassifier(Path tokenizerDir, String modelPath, Path merchantMapPath) throws Exception {
        this.tokenizer = HuggingFaceTokenizer.newInstance(tokenizerDir);
        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        this.merchantMap = loadMerchantMap(merchantMapPath);
    }

    /**
     * Classifies the input text into one of the predefined categories
     *
     * @param text The transaction description to classify
     * @return The input text to the model -- The predicted category name
     * @throws OrtException If classification fails
     */
    public Map<String, String> classify(String text) throws OrtException {
        // Prepare input tensors
        String enrichedText = enrichTextWithMerchantInfo(text);
        Map<String, OnnxTensor> inputs = prepareInputs(enrichedText);
        Map<String, String> result = new LinkedHashMap<>();
        // Run inference
        try (OrtSession.Result results = session.run(inputs)) {
            int predictedClassId = extractPredictedClass(results);
            result.put(enrichedText, CATEGORIES.getOrDefault(predictedClassId, "Unknown"));
            return result;
        } finally {
            // Clean up resources
            inputs.values().forEach(OnnxTensor::close);
        }
    }

    /**
     * Prepares input tensors for the ONNX model from the input text
     *
     * @param text The input text to tokenize and convert to tensors
     * @return A map of input tensors (input_ids and attention_mask)
     * @throws OrtException If tensor creation fails
     */
    private Map<String, OnnxTensor> prepareInputs(String text) throws OrtException {
        long[] inputIds = tokenizer.encode(text).getIds();
        long[] attentionMask = tokenizer.encode(text).getAttentionMask();
        long[] inputShape = {1, inputIds.length};

        OnnxTensor inputIdsTensor = OnnxTensor.createTensor(
                env,
                LongBuffer.wrap(inputIds),
                inputShape
        );

        OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(
                env,
                LongBuffer.wrap(attentionMask),
                inputShape
        );

        return Map.of(
                "input_ids", inputIdsTensor,
                "attention_mask", attentionMaskTensor
        );
    }

    /**
     * Extracts the predicted class ID from the model output
     *
     * @param results The output from the ONNX model
     * @return The index of the predicted class
     * @throws OrtException If result processing fails
     */
    private int extractPredictedClass(OrtSession.Result results) throws OrtException {
        try (OnnxTensor logits = (OnnxTensor) results.get(0)) {
            float[][] logitsArray = (float[][]) logits.getValue();
            int maxIndex = 0;
            for (int i = 1; i < logitsArray[0].length; i++) {
                if (logitsArray[0][i] > logitsArray[0][maxIndex]) {
                    maxIndex = i;
                }
            }
            return maxIndex;
        }
    }

    /**
     * Classifies a batch of transactions
     *
     * @param transactions List of transactions to classify
     * @return A map of transactions to their predicted results(input text -- category)
     * @throws OrtException If classification fails
     */
    public Map<Transaction, Map<String,String>> classifyBatch(List<Transaction> transactions) throws OrtException {
        Map<Transaction, Map<String,String>> results = new LinkedHashMap<>();
        for (Transaction tx : transactions) {
            // Combine counterparty and product as input text
            String inputText = tx.toString();
            results.put(tx, classify(inputText));
        }
        return results;
    }

    /**
     * Closes the classifier and releases resources
     *
     * @throws Exception If closing fails
     */
    public void close() throws Exception {
        session.close();
    }

    /**
     * Load the json file which contains the merchant description
     * @param path
     * @return
     * @throws Exception
     */
    private Map<String, String> loadMerchantMap(@NotNull Path path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), new TypeReference<Map<String, String>>() {});
    }

    /**
     *  Since it's hard for model to understand the merchant name like
     *  KFC, Amazon, StarBacks ..., a additional description will be added
     *  when a specific merchant name is detected
     * @param text original transaction description
     * @return enriched text with additional description
     */
    private String enrichTextWithMerchantInfo(String text) {
        List<String> matchedDescriptions = new ArrayList<>();

        for (Map.Entry<String, String> entry : merchantMap.entrySet()) {
            if (text.toLowerCase().contains(entry.getKey().toLowerCase())) {
                matchedDescriptions.add(entry.getValue());
            }
        }

        if (!matchedDescriptions.isEmpty()) {
            String merchantInfo = String.join(" ", matchedDescriptions);
            return "Description: " + merchantInfo + text;
        } else {
            return text;
        }
    }
}