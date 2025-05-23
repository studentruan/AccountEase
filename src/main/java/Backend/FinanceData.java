package Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
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

        public DailyData(double income, double expense, double budget, double spentBudget) {
            this.income = income;
            this.expense = expense;
            this.budget = budget;
            this.spentBudget = spentBudget;
            this.remainingBudget = budget - spentBudget;
        }
    }

    // 每月数据存储结构
    public static class MonthlyData {
        public double income;
        public double expense;
        public double budget;
        public double spentBudget;
        public double remainingBudget;

        public MonthlyData(double income, double expense, double budget, double spentBudget) {
            this.income = income;
            this.expense = expense;
            this.budget = budget;
            this.spentBudget = spentBudget;
            this.remainingBudget = budget - spentBudget;
        }
    }

    // 数据存储
    private final Map<LocalDate, DailyData> dailyDataMap = new HashMap<>();
    private MonthlyData monthlyData;
    private final List<List<String>> dailyTopCategories = new ArrayList<>();
    private final List<List<String>> monthlyTopCategories = new ArrayList<>();
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
        parseCategories(json);
        parseMetadata(json);
    }

    private void parseBasicInfo(JSONObject json) {
        this.ledgerId = json.getString("账本ID");

        // 从二级目录获取创建时间
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
                .filter(key -> key.matches("\\d{4}-\\d{2}-\\d{2}"))  // 匹配日期格式
                .forEach(dateStr -> {
                    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                    JSONObject dailyJson = json.getJSONObject(dateStr);

                    DailyData data = new DailyData(
                            dailyJson.optDouble("日收入", 0),
                            dailyJson.optDouble("日支出", 0),
                            dailyJson.optDouble("日预算", 0),
                            dailyJson.optDouble("日已花预算", 0)
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
        this.monthlyData = new MonthlyData(
                monthlyJson.optDouble("月收入", 0),
                monthlyJson.optDouble("月支出", 0),
                monthlyJson.optDouble("月预算", 0),
                monthlyJson.optDouble("月已花预算", 0)
        );
    }

    private void parseCategories(JSONObject json) {
        // 当天支出分类
        JSONArray dailyCategories = json.optJSONArray("当天前四个支出最多的种类");
        if (dailyCategories != null) {
            for (int i = 0; i < dailyCategories.length(); i++) {
                JSONArray items = dailyCategories.getJSONArray(i);
                dailyTopCategories.add(items.toList().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
            }
        }

        // 当月支出分类
        JSONArray monthlyCategories = json.optJSONArray("当月前四个支出最多的种类");
        if (monthlyCategories != null) {
            for (int i = 0; i < monthlyCategories.length(); i++) {
                JSONArray items = monthlyCategories.getJSONArray(i);
                monthlyTopCategories.add(items.toList().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
            }
        }

        // 纪念日
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

    // 以下为数据访问方法
    public DailyData getDailyData(LocalDate date) {
        return dailyDataMap.get(date);
    }

    public MonthlyData getMonthlyData() {
        return monthlyData;
    }

    public List<List<String>> getDailyTopCategories() {
        return Collections.unmodifiableList(dailyTopCategories);
    }

    public List<List<String>> getMonthlyTopCategories() {
        return Collections.unmodifiableList(monthlyTopCategories);
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