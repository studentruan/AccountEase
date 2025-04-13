package org.classification;

public class DeepSeekONNXInference {
    /**
    private HuggingFaceTokenizer tokenizer;
    private OrtEnvironment env;
    private OrtSession session;
    private LongBuffer inputIdsBuffer;
    private LongBuffer attentionMaskBuffer;

    public void init(String modelDir) throws Exception {
        Path dirPath = Paths.get(modelDir).toAbsolutePath();

        // 加载tokenizer（保持不变）
        Path tokenizerPath = dirPath.resolve("tokenizer.json");
        this.tokenizer = HuggingFaceTokenizer.newInstance(tokenizerPath);

        // 1. 准备模型和权重文件路径
        Path modelFile = dirPath.resolve("model.onnx");
        Path weightFile = dirPath.resolve("model.onnx.data");

        if (!Files.exists(modelFile) || !Files.exists(weightFile)) {
            throw new Exception("Missing model files in " + dirPath);
        }

        // 2. 正确使用外部初始化器
        this.env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();

        // 创建权重张量映射
        Map<String, OnnxTensorLike> externalData = new HashMap<>();

        long fileSize = Files.size(weightFile);
        float[] weightData = new float[(int) (fileSize / 4)];  // 假设浮点型，每个4字节

        try (FileChannel channel = FileChannel.open(weightFile, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect((int) fileSize);
            channel.read(buffer);
            buffer.flip();

            // 将字节缓冲区转换为浮点数组
            int index = 0;
            while (buffer.hasRemaining()) {
                weightData[index++] = buffer.getFloat();
            }
            // 使用ByteBuffer创建OnnxTensor
            OnnxTensor weightTensor = OnnxTensor.createTensor(env, weightData);
            externalData.put("model.onnx.data", weightTensor);
            opts.addExternalInitializers(externalData);
            // 3. 加载模型
            this.session = env.createSession(weightFile.toString(), opts);
        }

    }

    public void generate_coding(String inputText) throws Exception {
        Encoding encoding = tokenizer.encode(inputText);
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();
        System.out.println(inputIds);
        System.out.println(attentionMask);
    }

    private long argmax(float[] logits) {
        int maxIndex = 0;
        for (int i = 1; i < logits.length; i++) {
            if (logits[i] > logits[maxIndex]) maxIndex = i;
        }
        return maxIndex;
    }

    public static void main(String[] args) throws Exception {
        DeepSeekONNXInference model = new DeepSeekONNXInference();
        model.init("src/main/resources/cpu-int4-rtn-block-32-acc-level-4");
        model.generate_coding("hello deepseek");
    }
     */
}