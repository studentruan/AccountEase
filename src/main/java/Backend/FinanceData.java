package Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

public class FinanceData {

    // 基础属性
    private String ledgerId;
    private LocalDate creationTime;

    // 每日数据存储结构
    public static class DailyData {
        public double income;
        public double expense;
        public double budget;
        public double spentBudget;
        public double remainingBudget;
        public List<Map.Entry<String, Double>> topCategories; // 修改为包含金额的数据结构

        public DailyData(double income, double expense, double budget, double spentBudget,
                         List<Map.Entry<String, Double>> topCategories) {
            this.income = income;
            this.expense = expense;
            this.budget = budget;
            this.spentBudget = spentBudget;
            this.remainingBudget = budget - spentBudget;
            this.topCategories = topCategories;
        }
    }

    // 每月数据存储结构
    public static class MonthlyData {
        public double income;
        public double expense;
        public double budget;
        public double spentBudget;
        public double remainingBudget;
        public List<Map.Entry<String, Double>> topCategories; // 包含分类和金额

        public MonthlyData(double income, double expense, double budget,
                           double spentBudget, List<Map.Entry<String, Double>> topCategories) {
            this.income = income;
            this.expense = expense;
            this.budget = budget;
            this.spentBudget = spentBudget;
            this.remainingBudget = budget - spentBudget;
            this.topCategories = topCategories;
        }
    }

    // 数据存储
    private final Map<LocalDate, DailyData> dailyDataMap = new HashMap<>();
    private MonthlyData monthlyData;
    private final List<String> anniversaryDates = new ArrayList<>();

    // 元数据
    private LocalDate generatedDate;
    private int transactionCount;

    // 日期解析器
    private static final DateTimeFormatter FLEXIBLE_DATE_PARSER = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral('T')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .optionalEnd()
            .toFormatter();

    // 路径配置
    public static final String PRIMARY_DIR = "src/main/resources/firstlevel_json/";
    public static final String SECONDARY_DIR = "src/main/resources/secondlevel_json/";
    public static final String THIRD_DIR = "src/main/resources/thirdlevel_json/";

    public void loadFinanceData(String id) throws IOException {
        this.ledgerId = id;
        File file = new File(THIRD_DIR, id + ".json");

        if (!file.exists()) {
            throw new FileNotFoundException("财务数据文件不存在: " + file.getAbsolutePath());
        }

        String content = new String(Files.readAllBytes(file.toPath()));
        JSONObject json = new JSONObject(content);

        // 解析基础信息
        parseBasicInfo(json);
        parseDailyData(json);
        parseMonthlyData(json);
        parseAnniversaries(json);
        parseMetadata(json);
    }

    private void parseBasicInfo(JSONObject json) {
        this.ledgerId = json.getString("账本ID");

        try {
            File metaFile = new File(SECONDARY_DIR, ledgerId + ".json");
            JSONObject metaJson = new JSONObject(new String(Files.readAllBytes(metaFile.toPath())));
            this.creationTime = LocalDate.parse(metaJson.getString("创建时间"), FLEXIBLE_DATE_PARSER);
        } catch (Exception e) {
            throw new RuntimeException("无法解析创建时间", e);
        }
    }

    private void parseDailyData(JSONObject json) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        json.keySet().stream()
                .filter(key -> key.matches("\\d{4}-\\d{2}-\\d{2}"))
                .forEach(dateStr -> {
                    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                    JSONObject dailyJson = json.getJSONObject(dateStr);

                    // 解析分类数据
                    List<Map.Entry<String, Double>> categories = parseCategoryEntries(
                            dailyJson.optJSONArray("前四个支出类别")
                    );

                    DailyData data = new DailyData(
                            dailyJson.optDouble("日收入", 0),
                            dailyJson.optDouble("日支出", 0),
                            dailyJson.optDouble("日预算", 0),
                            dailyJson.optDouble("日已花预算", 0),
                            categories
                    );

                    dailyDataMap.put(date, data);
                });
    }

    private void parseMonthlyData(JSONObject json) {
        String monthKey = json.keySet().stream()
                .filter(key -> key.matches("\\d{4}-\\d{2}"))
                .findFirst()
                .orElseThrow();

        JSONObject monthlyJson = json.getJSONObject(monthKey);

        // 解析分类数据
        List<Map.Entry<String, Double>> categories = parseCategoryEntries(
                monthlyJson.optJSONArray("前四个支出类别")
        );

        this.monthlyData = new MonthlyData(
                monthlyJson.optDouble("月收入", 0),
                monthlyJson.optDouble("月支出", 0),
                monthlyJson.optDouble("月预算", 0),
                monthlyJson.optDouble("月已花预算", 0),
                categories
        );
    }

    private List<Map.Entry<String, Double>> parseCategoryEntries(JSONArray categories) {
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        if (categories != null) {
            for (int i = 0; i < categories.length(); i++) {
                JSONObject item = categories.getJSONObject(i);
                result.add(new SimpleEntry<>(
                        item.getString("category"),
                        item.getDouble("amount")
                ));
            }
        }
        return result;
    }

    private void parseAnniversaries(JSONObject json) {
        JSONArray anniversaries = json.optJSONArray("所有的纪念日");
        if (anniversaries != null) {
            anniversaryDates.addAll(anniversaries.toList().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
        }
    }

    private void parseMetadata(JSONObject json) {
        JSONObject meta = json.optJSONObject("_metadata");
        if (meta != null) {
            this.generatedDate = LocalDate.parse(meta.getString("生成时间"));
            this.transactionCount = meta.getInt("交易总数");
        }
    }

    // 数据访问方法
    public DailyData getDailyData(LocalDate date) {
        return dailyDataMap.get(date);
    }

    public MonthlyData getMonthlyData() {
        return monthlyData;
    }

    public List<String> getAnniversaryDates() {
        return Collections.unmodifiableList(anniversaryDates);
    }

    // 其他getter方法
    public String getLedgerId() { return ledgerId; }
    public LocalDate getCreationTime() { return creationTime; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public int getTransactionCount() { return transactionCount; }

}