package BasicInformationStorageandRetrieval;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceData {

    // 类属性，使用清晰且明确的英文命名
    private String ledgerId; // 账本ID
    private double dailyIncome; // 日收入
    private double dailyExpense; // 日支出
    private double dailyBudget; // 日预算
    private double dailySpentBudget; // 日已花预算
    private double dailyRemainingBudget; // 日剩余预算
    private double dailyReimbursement; // 日报销
    private double dailyReimbursed; // 日已报销
    private double dailyUnreimbursed; // 日未报销
    private double dailyTotalAssets; // 日总资产
    private double dailyRemainingAssets; // 日剩余资产
    private double dailyTotalLiabilities; // 日总负债

    // 月相关属性
    private double monthlyIncome; // 月收入
    private double monthlyExpense; // 月支出
    private double monthlyBudget; // 月预算
    private double monthlySpentBudget; // 月已花预算
    private double monthlyRemainingBudget; // 月剩余预算
    private double monthlyReimbursement; // 月报销
    private double monthlyReimbursed; // 月已报销
    private double monthlyUnreimbursed; // 月未报销
    private double monthlyTotalAssets; // 月总资产
    private double monthlyRemainingAssets; // 月剩余资产
    private double monthlyTotalLiabilities; // 月总负债

    // 前四个支出最多的种类
    private List<String> top4DailyExpenseCategories = new ArrayList<>(); // 当天前四个支出最多的种类
    private List<String> top4MonthlyExpenseCategories = new ArrayList<>(); // 当月前四个支出最多的种类

    // 纪念日信息
    private List<String> anniversaryDates = new ArrayList<>(); // 所有的纪念日

    // 创建时间
    private LocalDate creationTime; // 账本创建时间

    // 存储路径定义
    private static final String DIRECTORY_A = "./a"; // 存储目录a
    private static final String DIRECTORY_B = "./b"; // 存储目录b

    /**
     * 方法：根据账本ID加载对应的财务数据，并进行月度数据的计算
     *
     * @param id 账本ID
     * @throws IOException 文件读取或写入异常
     */
    public void loadFinanceData(String id) throws IOException {
        this.ledgerId = id;

        // Step 1: 从存储路径b中读取该ID对应的JSON文件
        File fileB = new File(DIRECTORY_B, id + ".json");
        if (!fileB.exists()) {
            throw new FileNotFoundException("JSON 文件不存在: " + fileB.getAbsolutePath());
        }

        String contentB = new String(Files.readAllBytes(Paths.get(fileB.getAbsolutePath())));
        JSONObject jsonObjectB = new JSONObject(contentB);

        // 将JSON文件中的键录入到类的属性中
        this.dailyIncome = jsonObjectB.optDouble("日收入", 0.0);
        this.dailyExpense = jsonObjectB.optDouble("日支出", 0.0);
        this.dailyBudget = jsonObjectB.optDouble("日预算", 0.0);
        this.dailySpentBudget = jsonObjectB.optDouble("日已花预算", 0.0);
        this.dailyRemainingBudget = jsonObjectB.optDouble("日剩余预算", 0.0);
        this.dailyReimbursement = jsonObjectB.optDouble("日报销", 0.0);
        this.dailyReimbursed = jsonObjectB.optDouble("日已报销", 0.0);
        this.dailyUnreimbursed = jsonObjectB.optDouble("日未报销", 0.0);
        this.dailyTotalAssets = jsonObjectB.optDouble("日总资产", 0.0);
        this.dailyRemainingAssets = jsonObjectB.optDouble("日剩余资产", 0.0);
        this.dailyTotalLiabilities = jsonObjectB.optDouble("日总负债", 0.0);

        // 纪念日处理
        if (jsonObjectB.has("所有的纪念日")) {
            jsonObjectB.getJSONArray("所有的纪念日").forEach(entry -> anniversaryDates.add(entry.toString()));
        }

        // Step 2: 从存储路径a中获取创建时间
        File fileA = new File(DIRECTORY_A, id + ".json");
        if (!fileA.exists()) {
            throw new FileNotFoundException("JSON 文件不存在: " + fileA.getAbsolutePath());
        }

        String contentA = new String(Files.readAllBytes(Paths.get(fileA.getAbsolutePath())));
        JSONObject jsonObjectA = new JSONObject(contentA);
        this.creationTime = LocalDate.parse(jsonObjectA.getString("创建时间"));

        // Step 3: 遍历存储路径a中所有含该月信息的ID
        String creationMonth = creationTime.getMonth().toString(); // 提取创建时间的月份
        List<String> relatedIds = new ArrayList<>();

        File directoryA = new File(DIRECTORY_A);
        for (File file : directoryA.listFiles()) {
            if (file.getName().endsWith(".json")) {
                String fileContent = new String(Files.readAllBytes(file.toPath()));
                JSONObject json = new JSONObject(fileContent);
                LocalDate fileCreationTime = LocalDate.parse(json.getString("创建时间"));
                if (fileCreationTime.getMonth().toString().equals(creationMonth)) {
                    relatedIds.add(json.getString("账本ID"));
                }
            }
        }

        // Step 4: 加和存储路径b中相关ID对应的JSON文件中的数据
        this.monthlyIncome = 0.0;
        this.monthlyExpense = 0.0;
        this.monthlyBudget = 0.0;
        this.monthlySpentBudget = 0.0;
        this.monthlyRemainingBudget = 0.0;
        this.monthlyReimbursement = 0.0;
        this.monthlyReimbursed = 0.0;
        this.monthlyUnreimbursed = 0.0;
        this.monthlyTotalAssets = 0.0;
        this.monthlyRemainingAssets = 0.0;
        this.monthlyTotalLiabilities = 0.0;

        for (String relatedId : relatedIds) {
            File relatedFileB = new File(DIRECTORY_B, relatedId + ".json");
            if (relatedFileB.exists()) {
                String relatedContentB = new String(Files.readAllBytes(Paths.get(relatedFileB.getAbsolutePath())));
                JSONObject relatedJsonB = new JSONObject(relatedContentB);

                this.monthlyIncome += relatedJsonB.optDouble("日收入", 0.0);
                this.monthlyExpense += relatedJsonB.optDouble("日支出", 0.0);
                this.monthlyBudget += relatedJsonB.optDouble("日预算", 0.0);
                this.monthlySpentBudget += relatedJsonB.optDouble("日已花预算", 0.0);
                this.monthlyRemainingBudget += relatedJsonB.optDouble("日剩余预算", 0.0);
                this.monthlyReimbursement += relatedJsonB.optDouble("日报销", 0.0);
                this.monthlyReimbursed += relatedJsonB.optDouble("日已报销", 0.0);
                this.monthlyUnreimbursed += relatedJsonB.optDouble("日未报销", 0.0);
                this.monthlyTotalAssets += relatedJsonB.optDouble("日总资产", 0.0);
                this.monthlyRemainingAssets += relatedJsonB.optDouble("日剩余资产", 0.0);
                this.monthlyTotalLiabilities += relatedJsonB.optDouble("日总负债", 0.0);
            }
        }
    }
}