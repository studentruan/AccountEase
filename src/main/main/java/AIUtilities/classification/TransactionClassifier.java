package AIUtilities.classification;

/**
 * A classifier for transaction categories using Bert-ONNX model.
 *
 * @version 1.0
 * @date 31 March
 * @author studentruan
 */

import ai.onnxruntime.*;
import java.util.*;
import java.nio.LongBuffer;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import java.nio.file.*;

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

    /**
     * Initializes the TransactionClassifier with tokenizer and ONNX model
     *
     * @param tokenizerDir Path to the tokenizer directory
     * @param modelPath Path to the ONNX model file
     * @throws Exception If initialization fails
     */
    public TransactionClassifier(Path tokenizerDir, String modelPath) throws Exception {
        this.tokenizer = HuggingFaceTokenizer.newInstance(tokenizerDir);
        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }

    /**
     * Classifies the input text into one of the predefined categories
     *
     * @param text The transaction description to classify
     * @return The predicted category name
     * @throws OrtException If classification fails
     */
    public String classify(String text) throws OrtException {
        // Prepare input tensors
        Map<String, OnnxTensor> inputs = prepareInputs(text);

        // Run inference
        try (OrtSession.Result results = session.run(inputs)) {
            int predictedClassId = extractPredictedClass(results);
            return CATEGORIES.getOrDefault(predictedClassId, "Unknown");
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
     * @return A map of transactions to their predicted categories
     * @throws OrtException If classification fails
     */
    public Map<Transaction, String> classifyBatch(List<Transaction> transactions) throws OrtException {
        Map<Transaction, String> results = new LinkedHashMap<>();
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
}

/**
{
 属性：
    'date':
    'income': 当天的收入
    'expense': 当天的支出
    '？？？': 这是一个字典，存储每个分类的支出/收入情况

 方法：
    1。接收两个日期，返回一个数组，是这段时间每天的支出
    2. 接收一个月份，返回一个字典，包含该月的总的每个分类的支出/收入情况
    剩下的还没想好
}*/