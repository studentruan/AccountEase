package Backend;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceDataProcessor {
    private final String ledgerId;
    private final Map<String, Transaction_FZ> transactions;
    private final JSONObject financeJson = new JSONObject();

    // 日期格式工具
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");



    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter ANNIVERSARY_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd");

    public FinanceDataProcessor(String ledgerId, Map<String, Transaction_FZ> transactions) {
        this.ledgerId = ledgerId;
        this.transactions = transactions;
    }

    public void process() {
        // 初始化基础结构
        initBaseStructure();
        // 处理每日数据
        processDailyMetrics();
        // 处理月度聚合
        processMonthlyMetrics();
        // 设置系统元数据
        setSystemMetadata();
    }

    private void initBaseStructure() {
        financeJson.put("账本ID", ledgerId);
//        financeJson.put("创建时间", LocalDate.now().format(DATE_FORMATTER));

        // 初始化所有数值字段
        Arrays.asList("日收入", "日支出", "日预算", "日已花预算", "日剩余预算",
                        "日报销", "日已报销", "日未报销", "日总资产", "日剩余资产", "日总负债",
                        "月收入", "月支出", "月预算", "月已花预算", "月剩余预算",
                        "月报销", "月已报销", "月未报销", "月总资产", "月剩余资产", "月总负债")
                .forEach(field -> financeJson.put(field, 0.0));
    }

    private void processDailyMetrics() {
        Map<LocalDate, List<Transaction_FZ>> dailyMap = transactions.values().stream()
                .collect(Collectors.groupingBy(Transaction_FZ::getDate));

        dailyMap.forEach((date, transactions) -> {
            JSONObject dailyData = new JSONObject();

            // 基础计算
            double income = sumByType(transactions, "INCOME");
            double expense = sumByType(transactions, "EXPENSE");

            // 预算计算（示例逻辑，需根据实际业务调整）
            double budget = 1000.0; // 应从配置获取
            double spent = expense;
            double remaining = budget - spent;

            // 填充每日数据
            dailyData.put("日收入", income);
            dailyData.put("日支出", expense);
            dailyData.put("日预算", budget);
            dailyData.put("日已花预算", spent);
            dailyData.put("日剩余预算", remaining);

            // 分类统计
            List<String> topCategories = getTopCategories(transactions);
            financeJson.append("当天前四个支出最多的种类", new JSONArray(topCategories));

            // 合并到主结构
            financeJson.put(date.format(DATE_FORMATTER), dailyData);
        });
    }

    private void processMonthlyMetrics() {
        Map<String, List<Transaction_FZ>> monthlyMap = transactions.values().stream()
                .collect(Collectors.groupingBy(t -> t.getDate().format(MONTH_FORMATTER)));

        monthlyMap.forEach((month, transactions) -> {
            JSONObject monthlyData = new JSONObject();

            double income = sumByType(transactions, "INCOME");
            double expense = sumByType(transactions, "EXPENSE");

            // 月度预算计算（示例值）
            double budget = 30000.0;
            double spent = expense;
            double remaining = budget - spent;

            monthlyData.put("月收入", income);
            monthlyData.put("月支出", expense);
            monthlyData.put("月预算", budget);
            monthlyData.put("月已花预算", spent);
            monthlyData.put("月剩余预算", remaining);

            // 合并分类统计
            List<String> topCategories = getTopCategories(transactions);
            financeJson.append("当月前四个支出最多的种类", new JSONArray(topCategories));

            financeJson.put(month, monthlyData);
        });
    }

    private List<String> getTopCategories(List<Transaction_FZ> transactions) {
        return transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction_FZ::getProduct,
                        Collectors.summingDouble(Transaction_FZ::getAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void setSystemMetadata() {
        JSONObject meta = new JSONObject();
        meta.put("生成时间", LocalDate.now().format(DATE_FORMATTER));
        meta.put("交易总数", transactions.size());
        financeJson.put("_metadata", meta);

        // 预设纪念日
        JSONArray anniversaries = new JSONArray()
                .put("01-01")
                .put("10-01");
        financeJson.put("所有的纪念日", anniversaries);
    }

    private double sumByType(List<Transaction_FZ> transactions, String type) {
        return transactions.stream()
                .filter(t -> type.equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction_FZ::getAmount)
                .sum();
    }

    public void saveToThirdLevel(String directory) throws IOException {
        Path path = Paths.get(directory, ledgerId + ".json");

        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // 格式化输出
        String jsonString = financeJson.toString(2)
                .replace(",\"", ",\n  \"")  // 增加可读性
                .replace(":{", ": {\n  ");

        Files.writeString(path, jsonString);
    }
}