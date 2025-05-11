import java.time.LocalDate;
import java.util.Map;

public class TransactionMainForExample {
    public static void main(String[] args) {
        // === 1. 加载 XML ===
        TransactionDataLoader loader = new TransactionDataLoader();
        loader.loadFromXML("C:\\Users\\ASUS\\Desktop\\Merged\\merged_transactions.xml");

        // 获取已加载的数据
        Map<String, Transaction> dataMap = loader.getTransactionData();

        // === 2. 调取交易（按 ID） ===
        String id = "250404001"; // 示例 ID
        Transaction t1 = dataMap.get(id);
        if (t1 != null) {
            // 你可以访问 t1.getAmount(), t1.getDate(), t1.getType() 等属性
            double amount = t1.getAmount();
            String product = t1.getProduct();
            // ... 此处为调取示例，不打印
        }

        // === 3. 比较交易 ===
        // 自定义创建一个交易对象
        Transaction t2 = new Transaction("250404001", LocalDate.of(2025, 4, 4), "Amazon", "Book", "Expense", 239.90);

        // 比较是否相等（基于 ID）
        boolean isSame = t1.equals(t2); // true，因为 ID 相同

        // === 4. 导出为新的 XML ===
        loader.saveToXML("output_transactions.xml");
    }
}
