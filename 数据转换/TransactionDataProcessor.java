import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionDataProcessor {
    // 属性：存储交易数据的Map
    private final Map<String, Map<String, String>> transactionData = new HashMap<>();

    // 有效的键名
    private static final List<String> VALID_KEYS = Arrays.asList(
            "id", "日期", "名称", "金额", "种类", "描述", "图标颜色"
    );

    // 方法1：读取txt文件
    public void loadFromTxt(String filePath) throws IOException {
        validateFilePath(filePath); // 验证文件路径
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            processLine(line);
        }
    }

    // 方法2：读取csv文件
    public void loadFromCsv(String filePath) throws IOException {
        validateFilePath(filePath); // 验证文件路径
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            processLine(line);
        }
    }

    // 方法3：读取json文件
    public void loadFromJson(String filePath) throws IOException {
        validateFilePath(filePath); // 验证文件路径
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> dataList = mapper.readValue(new File(filePath), List.class);
        for (Map<String, String> data : dataList) {
            addTransaction(data);
        }
    }

    // 方法4：读取xml文件
    public void loadFromXml(String filePath) throws Exception {
        validateFilePath(filePath); // 验证文件路径
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        NodeList transactionNodes = document.getElementsByTagName("transaction");
        for (int i = 0; i < transactionNodes.getLength(); i++) {
            Map<String, String> data = new HashMap<>();
            NodeList childNodes = transactionNodes.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                String nodeName = childNodes.item(j).getNodeName();
                String content = childNodes.item(j).getTextContent();
                if (VALID_KEYS.contains(nodeName)) {
                    data.put(nodeName, content);
                }
            }
            addTransaction(data);
        }
    }

    // 方法5：根据ID获取数据
    public Map<String, String> getTransactionById(String id) {
        return transactionData.get(id);
    }

    // 方法6：获取全部数据
    public Collection<Map<String, String>> getAllTransactions() {
        return transactionData.values();
    }

    // 方法7：修改特定数据的特定字段
    public void updateTransactionFieldById(String id, String field, String newValue) {
        if (transactionData.containsKey(id)) {
            Map<String, String> data = transactionData.get(id);
            if (VALID_KEYS.contains(field)) { // 确保字段是有效的
                data.put(field, newValue);
            }
        }
    }

    // 方法8：替换整个数据
    public void replaceTransactionById(String id, Map<String, String> newData) {
        if (newData != null && newData.keySet().containsAll(VALID_KEYS)) { // 确保新数据包含所有有效字段
            transactionData.put(id, newData);
        }
    }

    // 辅助方法：处理单行数据
    private void processLine(String line) {
        String[] parts = line.split(",");
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            if (i < VALID_KEYS.size()) {
                data.put(VALID_KEYS.get(i), parts[i]);
            }
        }
        addTransaction(data);
    }

    // 辅助方法：添加交易数据
    private void addTransaction(Map<String, String> data) {
        String id = data.get("id");
        if (id != null && !transactionData.containsKey(id)) {
            for (String key : VALID_KEYS) {
                data.putIfAbsent(key, ""); // 缺失的字段补充为空字符串
            }
            transactionData.put(id, data);
        }
    }

    // 辅助方法：验证文件路径
    private void validateFilePath(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件路径无效或文件不存在: " + filePath);
        }
    }

    // 主方法：测试调用
    public static void main(String[] args) {
        TransactionDataProcessor processor = new TransactionDataProcessor();

        try {
            // 加载txt文件
            processor.loadFromTxt("data.txt");
            System.out.println("从TXT加载完成，数据如下：");
            System.out.println(processor.getAllTransactions());

            // 加载csv文件
            processor.loadFromCsv("data.csv");
            System.out.println("从CSV加载完成，数据如下：");
            System.out.println(processor.getAllTransactions());

            // 加载json文件
            processor.loadFromJson("data.json");
            System.out.println("从JSON加载完成，数据如下：");
            System.out.println(processor.getAllTransactions());

            // 加载xml文件
            processor.loadFromXml("data.xml");
            System.out.println("从XML加载完成，数据如下：");
            System.out.println(processor.getAllTransactions());

            // 根据ID获取数据
            System.out.println("根据ID获取数据：");
            System.out.println(processor.getTransactionById("1"));

            // 修改特定字段
            processor.updateTransactionFieldById("1", "名称", "新名称");
            System.out.println("修改ID为1的名称字段后：");
            System.out.println(processor.getTransactionById("1"));

            // 替换整个数据
            Map<String, String> newData = new HashMap<>();
            newData.put("id", "1");
            newData.put("日期", "2025-04-18");
            newData.put("名称", "替换后的名称");
            newData.put("金额", "1000");
            newData.put("种类", "消费");
            newData.put("描述", "这是替换后的描述");
            newData.put("图标颜色", "蓝色");
            processor.replaceTransactionById("1", newData);
            System.out.println("替换ID为1的整个数据后：");
            System.out.println(processor.getTransactionById("1"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}