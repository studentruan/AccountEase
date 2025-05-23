package com.myapp.controller;

import AIUtilities.classification.TransactionClassifier;
import AIUtilities.prediction.ARIMAModel;

import Backend.*;

import DataProcessor.DailyTransactionProcessor;
import DataProcessor.TransactionAnalyzer;
import ai.djl.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import javafx.scene.layout.GridPane;


import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static Backend.FinanceData.THIRD_DIR;
import static detectorTools.OutlierDetector.detectAnomalies;

public class LedgerController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label ledgerTypeLabel;

    // 左侧导航栏
    @FXML
    private Button BackButton;
    @FXML
    private ImageView image_Back_to_main;
    @FXML
    private Label image_Back_to_main_text;

    // 主要财务信息
    @FXML
    private Label expenseLabel;
    @FXML
    private Label incomeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label budgetLabel;
    @FXML
    private Label remainLabel;
    @FXML
    private Label spentLabel;
    @FXML
    private Label pendingLabel;
    @FXML
    private Label claimedLabel;
    @FXML
    private Label reimburLabel;
    @FXML
    private Label netAssetsLabel;
    @FXML
    private Label debtsLabel;
    @FXML
    private Label totalsLabel;

    // 图表
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private PieChart expenseCategoriesChart;

    // 按钮
    @FXML
    private Button expenseButton;
    @FXML
    private Button incomeButton;
    @FXML
    private Button balanceButton;
    @FXML
    private Button expenseCategoryButton;
    @FXML
    private Button incomeCategoryButton;

    // 建议和日历
    @FXML
    private VBox adviceBox;
    @FXML
    private VBox calendarWidget;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Label monthTitle;
    private Ledger ledger;
    private FinanceData financeData;

    @FXML
    private GridPane calendarGrid;

    private YearMonth currentMonth = YearMonth.now();
    private Map<LocalDate, String> dateMarkers = new HashMap<>();

    private List<String> allAnniversaries = new ArrayList<>();
    private LocalDate selectedDate = null;
    private boolean isDailyMode = false;



    @FXML
    private Label selectedDateLabel;

    @FXML
    private VBox expenseItemsContainer;

    List<String> adviceList = new ArrayList<>();
    private Map<LocalDate, List<Transactions>> dateTransactions = new HashMap<>();



    @FXML
    private VBox budgetVBox;







//    @FXML
//    private FinanceData financeData = new FinanceData();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        budgetVBox.setOnMouseClicked(event -> {
            showBudgetSettingDialog();
        });


        // 设置界面基础视图（不依赖账本数据）



        Ledger ledger = this.ledger;

        if (ledger != null) {


            this.financeData = new FinanceData();  // 正确初始化
            try {
                financeData.loadFinanceData(ledger.getId());


                if (isDailyMode) {
                    // 日模式图表
                    refreshDailyCharts(selectedDate);
                } else {
                    // 月模式图表
                    refreshMonthlyCharts();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            updateDashboard();  // 显示图表/数据
        } else {
            System.err.println("loadLedger received a null ledger.");
        }

        updateCalendar();

        LocalDate today = LocalDate.now();
        showDateDetails(today);

        initializeCharts();
        initializePictures();


        loadAiAdvice(adviceList);
        expenseCategoriesChart.getData().clear();



//        updateDashboard();



        // 不要在这里调用 updateDashboard()，因为 ledger 还未传入
    }


    @FXML
    private Button addMemorialDayButton;

    @FXML
    private Button MemorialDayButton;

    @FXML
    private void handleShowAllAnniversaries() {
        try {
            List<String> anniversaries = getAllAnniversaries();

            if (anniversaries.isEmpty()) {
                showAlert("提示", "暂无纪念日数据");
                return;
            }

            // 将列表转换为换行显示的字符串
            String content = String.join("\n", anniversaries);

            // 创建弹窗展示
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("所有纪念日");
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();

        } catch (IOException e) {
            showAlert("错误", "加载纪念日失败: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddMemorialDay() {
        // 创建输入对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加纪念日");
        dialog.setHeaderText("请输入纪念日(MM-dd格式)");
        dialog.setContentText("日期:");

        // 显示对话框并获取结果
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(dateStr -> {
            try {
                // 验证日期格式
                if (!dateStr.matches("(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])")) {
                    throw new IllegalArgumentException("日期格式不正确，请使用MM-dd格式");
                }

                // 读取JSON文件
                String jsonFilePath = "src/main/resources/thirdlevel_json/" + ledgerId + ".json";
                JSONObject rootObject;
                try (FileReader reader = new FileReader(jsonFilePath)) {
                    rootObject = new JSONObject(new JSONTokener(reader));
                }

                // 获取或创建纪念日数组
                JSONArray memorialDays = rootObject.optJSONArray("所有的纪念日");
                if (memorialDays == null) {
                    memorialDays = new JSONArray();
                    rootObject.put("所有的纪念日", memorialDays);
                }

                // 检查是否已存在
                if (memorialDays.toList().contains(dateStr)) {
                    throw new IllegalArgumentException("该纪念日已存在");
                }

                // 添加新纪念日
                memorialDays.put(dateStr);

                // 写回文件
                try (FileWriter writer = new FileWriter(jsonFilePath)) {
                    writer.write(rootObject.toString(4));
                }

                // 显示成功消息
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("成功");
                    alert.setHeaderText(null);
                    alert.setContentText("纪念日添加成功: " + dateStr);
                    alert.showAndWait();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("错误");
                    alert.setHeaderText("添加纪念日失败");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }




    private void showBudgetSettingDialog() {
        // 创建对话框
        Dialog<Pair<Double, Double>> dialog = new Dialog<>();
        dialog.setTitle("设置预算");
        dialog.setHeaderText("请输入预算信息");

        // 设置按钮类型
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField budgetField = new TextField();
        budgetField.setPromptText("日预算");
        TextField spentField = new TextField();
        spentField.setPromptText("已花预算");

        grid.add(new Label("日预算:"), 0, 0);
        grid.add(budgetField, 1, 0);
        grid.add(new Label("已花预算:"), 0, 1);
        grid.add(spentField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double budget = Double.parseDouble(budgetField.getText());
                    double spent = Double.parseDouble(spentField.getText());
                    return new Pair<>(budget, spent);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        // 显示对话框并处理结果
        Optional<Pair<Double, Double>> result = dialog.showAndWait();

        result.ifPresent(budgetData -> {
            double budget = budgetData.getKey();
            double spent = budgetData.getValue();
            double remain = budget - spent;

            // 更新UI
            budgetLabel.setText(String.format("Budget $%.2f", budget));
            remainLabel.setText(String.format("Remain $%.2f", remain));
            spentLabel.setText(String.format("Spent $%.2f", spent));

            // 更新JSON数据
            updateJsonData(budget, spent, remain);
        });
    }




    private void updateJsonData(double budget, double spent, double remain) {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        try {
            String jsonFilePath = "src/main/resources/thirdlevel_json/" + ledgerId + ".json";

            // 读取JSON文件
            JSONObject rootObject;
            try (FileReader reader = new FileReader(jsonFilePath)) {
                rootObject = new JSONObject(new JSONTokener(reader));
            }

            String dateKey = selectedDate.toString();

            // 获取或创建日数据对象
            JSONObject dayData = rootObject.optJSONObject(dateKey);
            if (dayData == null) {
                dayData = new JSONObject();
                dayData.put("日支出", 0);
                dayData.put("日收入", 0);
            }

            // 保存旧值用于计算差值
            double oldBudget = dayData.optDouble("日预算", 0);
            double oldSpent = dayData.optDouble("日已花预算", 0);

            // 更新日数据
            dayData.put("日预算", budget);
            dayData.put("日已花预算", spent);
            dayData.put("日剩余预算", remain);
            rootObject.put(dateKey, dayData);

            // 更新月数据
            String monthKey = selectedDate.getYear() + "-" + selectedDate.getMonthValue();
            JSONObject monthData = rootObject.optJSONObject(monthKey);
            if (monthData == null) {
                monthData = new JSONObject();
                monthData.put("月收入", 0);
                monthData.put("月支出", 0);
            }

            double monthBudget = monthData.optDouble("月预算", 0);
            double monthSpent = monthData.optDouble("月已花预算", 0);

            // 计算差值并更新
            monthData.put("月预算", monthBudget + (budget - oldBudget));
            monthData.put("月已花预算", monthSpent + (spent - oldSpent));
            monthData.put("月剩余预算",
                    monthData.getDouble("月预算") - monthData.getDouble("月已花预算"));

            rootObject.put(monthKey, monthData);

            // 写回文件
            try (FileWriter writer = new FileWriter(jsonFilePath)) {
                writer.write(rootObject.toString(4)); // 使用4空格缩进
            }

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("保存错误");
                alert.setHeaderText("无法保存预算数据");
                alert.setContentText("错误: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }






    public void loadLedger(Ledger ledger) {
        this.ledger = ledger;

        if (ledger != null) {
            this.financeData = new FinanceData();  // 正确初始化
            try {
                financeData.loadFinanceData(ledger.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            updateDashboard();  // 显示图表/数据
        } else {
            System.err.println("loadLedger received a null ledger.");
        }



        // 遍历输出
//        for (Map.Entry<String, Transactions> entry : data.entrySet()) {
//            System.out.println("ID: " + entry.getKey());
//            System.out.println("交易信息: " + entry.getValue());
//        }
    }

    private void initializePictures() {
        Image image_Back = new Image(getClass().getResource("/images/home.png").toExternalForm());
        image_Back_to_main.setImage(image_Back);
        // 将 ImageView 设置为按钮的图标
        BackButton.setGraphic(image_Back_to_main);
        BackButton.setOnAction(event -> back_to_main());

    }

    private void initializeCharts() {
        // 初始化折线图
        xAxis.setLabel("Time");
        yAxis.setLabel("Value");

        //获取一段时间的每日交易数据
        DailyTransactionProcessor processor = new DailyTransactionProcessor();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Expense Statistics");
        LocalDate startDate = LocalDate.of(2025, 3, 23);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        double[] expense_history = new double[31-23+1];
        int index = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM/dd"));
            series.getData().add(new XYChart.Data<>(dateStr, processor.getTotalExpenses("2025/" + dateStr)));
            expense_history[index] = processor.getTotalExpenses("2025/" + dateStr);
            index++;
        }


        System.out.println(expense_history.length);

        lineChart.getData().add(series);

        ARIMAModel model = new ARIMAModel(expense_history, 1, 4, 5);

        // 3. 预测未来3天
        int steps = 3;
        int[] forecasts = model.predict(steps);

        for(int i = 0;i<steps;i++){
            System.out.println(forecasts[i]);
        }

        XYChart.Series<String, Number> forecastSeries = new XYChart.Series<>();
        forecastSeries.setName("ARIMA Forecast (Next 3 Days)");

        // 生成预测日期的标签
        LocalDate lastHistoryDate = endDate;
        for (int i = 0; i < steps; i++) {
            lastHistoryDate = lastHistoryDate.plusDays(1);
            String forecastDate = lastHistoryDate.format(DateTimeFormatter.ofPattern("MM/dd"));
            forecastSeries.getData().add(new XYChart.Data<>(forecastDate, Math.max(forecasts[i],0)));
        }

        lineChart.getData().add(forecastSeries);

        // 4. 设置预测系列的样式（红色虚线）
        forecastSeries.getNode().setStyle(
                "-fx-stroke: #ff0000; " +          // 红色线条
                        "-fx-stroke-dash-array: 5 5; " +   // 虚线样式
                        "-fx-stroke-width: 2px;"
        );

//        // 初始化饼图，所有比例都设置为0
//        PieChart.Data slice1 = new PieChart.Data("Shop", 71);
//        PieChart.Data slice2 = new PieChart.Data("Dress", 10);
//        PieChart.Data slice3 = new PieChart.Data("Car", 14);
//        PieChart.Data slice4 = new PieChart.Data("Pet", 5);
//
//        expenseCategoriesChart.getData().addAll(slice1, slice2, slice3, slice4);
    }


//    public void updateDashboard() {
//
//        // 更新支出
//        expenseLabel.setText("Expense $" + financeData.getExpense());
//
//        // 更新收入
//        incomeLabel.setText("Income $" + financeData.getIncome());
//
//        // 更新余额
//        balanceLabel.setText("Balance $" + financeData.getBalance());
//
//        // 更新预算
//        budgetLabel.setText("Budget $" + financeData.getBudget());
//        remainLabel.setText("Remain $" + (financeData.getBudget() - financeData.getExpense()));
//        spentLabel.setText("Spent $" + financeData.getExpense());
//
//        // 更新待处理
//        pendingLabel.setText("Pending $" + financeData.getPending());
//        claimedLabel.setText("Claimed $" + financeData.getClaimed());
//        reimburLabel.setText("Reimbur $" + financeData.getReimbursement());
//
//        // 更新净资产
//        netAssetsLabel.setText("Net $" + financeData.getnNetAssets());
//        totalsLabel.setText("Total $" + financeData.getTotals());
//        debtsLabel.setText("Debts $" + financeData.getDebts());
//
//        ObservableList<XYChart.Series<String, Number>> chartData = lineChart.getData();
//        loadAiAdvice(adviceList);
//
//// 确保 chartData 不为空并且包含至少一个 Series
////        if (chartData != null && !chartData.isEmpty()) {
////            XYChart.Series<String, Number> series = chartData.get(0);
////
////            // 只在 monthlyExpenses 不为空时清除并更新数据
////            Map<String, Double> monthlyExpenses = financeData.getMonthlyExpenses();  // 例如：{"Jan": 1000, "Feb": 1200, ...}
////            if (monthlyExpenses != null && !monthlyExpenses.isEmpty()) {
////                series.getData().clear();  // 清除现有数据
////                for (Map.Entry<String, Double> entry : monthlyExpenses.entrySet()) {
////                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
////                }
////            }
////
////            // 只在 expenseCategories 不为空时清除并更新数据
////            ObservableList<PieChart.Data> pieChartData = expenseCategoriesChart.getData();
////            Map<String, Double> expenseCategories = financeData.getExpenseCategories();  // 例如：{"Shop": 500, "Dress": 200, ...}
////            if (expenseCategories != null && !expenseCategories.isEmpty()) {
////                pieChartData.clear();  // 清除现有数据
////                for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
////                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
////                }
////            }
////        } else {
////            System.out.println("Line chart data is empty or not properly initialized.");
////        }
//
//
//        // 刷新UI
//
//        // 可以在这里更新其他UI元素，例如建议框
//
//
//    }
    public void updateDashboard() {
        TransactionLoader loader = new TransactionLoader();


        isDailyMode = selectedDate != null;
        // 获取当前显示模式
//        updateCalendar();

        refreshCharts(isDailyMode);

        updateFinancialMetrics(isDailyMode);
        LocalDate today = LocalDate.now();
        showDateDetails(today);
        // 更新核心财务指标

        // 更新图表数据
        refreshCharts(isDailyMode);

        // 加载智能建议
        loadAiAdvice(adviceList);


        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();

        // 步骤2：验证ID有效性
        if (ledgerId == null || ledgerId.trim().isEmpty()) {
            throw new IllegalStateException("未找到有效的账本ID，请先选择账本");
        }

//        // 加载 XML 文件中的数据
//        String resourcePath = "fourthlevel_xml/" + ledgerId;
//        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
//
//        if (resourceUrl == null) {
//            System.err.println("目录不存在：" + resourcePath);
//            return;
//        }
//
//        File folder = new File(resourceUrl.getFile());
//        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
//
//        if (files == null || files.length == 0) {
//            System.out.println("未找到XML文件");
//            return;
//        }
//
//        for (File file : files) {
//            String filePath = file.getAbsolutePath();
//            loader.loadTransactionsFromXml(filePath);
//        }
        // 定义XML文件的根目录（根据实际情况调整或从配置获取）
        String baseDir = "src/main/resources/";
        String resourcePath = baseDir + "fourthlevel_xml/" + ledgerId;
        File folder = new File(resourcePath);

// 检查目录是否存在且为文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("目录不存在：" + resourcePath);
            return;
        }

// 获取所有XML文件
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (files == null || files.length == 0) {
            System.out.println("未找到XML文件");
            return;
        }

// 处理每个文件
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            loader.loadTransactionsFromXml(filePath);
        }

        // 获取数据
        Map<String, Transactions> data = loader.getTransactionData();

        for (Transactions t : data.values()) {
            LocalDate date = t.getDate();
            dateTransactions.computeIfAbsent(date, k -> new ArrayList<>()).add(t);
        }

        showDateDetails(today);


}

    private void updateFinancialMetrics(boolean isDailyMode) {
        // 获取对应数据
        FinanceData.DailyData dailyData = isDailyMode ?
                financeData.getDailyData(selectedDate) : null;
        FinanceData.MonthlyData monthlyData = financeData.getMonthlyData();

        // 更新基础指标
        if(dailyData!=null) {
            expenseLabel.setText(formatAmount(isDailyMode ? dailyData.expense : monthlyData.expense, isDailyMode));
            incomeLabel.setText(formatAmount(isDailyMode ? dailyData.income : monthlyData.income, isDailyMode));
        }

        if(monthlyData != null) {
            balanceLabel.setText(formatAmount(monthlyData.budget - monthlyData.spentBudget, false));

            // 预算相关（仅月模式）
            budgetLabel.setText(isDailyMode ? "N/A" : formatAmount(monthlyData.budget, false));
            remainLabel.setText(isDailyMode ? "N/A" : formatAmount(monthlyData.remainingBudget, false));
            spentLabel.setText(isDailyMode ? "N/A" : formatAmount(monthlyData.spentBudget, false));

            // 资产概况（始终使用月数据）
            netAssetsLabel.setText(formatAmount(monthlyData.income - monthlyData.expense, false));
            totalsLabel.setText(formatAmount(monthlyData.income, false));
            debtsLabel.setText(formatAmount(monthlyData.expense, false));

        }

    }

    private String formatAmount(double value, boolean showDecimal) {
        return showDecimal ?
                String.format("$%.2f", value) :
                String.format("$%,.0f", value);
    }

    private void refreshCharts(boolean isDailyMode) {
        lineChart.getData().clear();
        expenseCategoriesChart.getData().clear();

        if (isDailyMode) {
            // 日模式图表
            refreshDailyCharts(selectedDate);
            initializeCharts();
        } else {
            // 月模式图表
            refreshMonthlyCharts();
            initializeCharts();
        }
    }

    // 在图表控制类中添加
    private void refreshMonthlyCharts() {
        expenseCategoriesChart.getData().clear();

        // 隐藏饼图扇区上的文字标签
        expenseCategoriesChart.setLabelsVisible(false);

        List<Map.Entry<String, Double>> categories = financeData.getMonthlyData().topCategories;
        if (!categories.isEmpty()) {
            double total = categories.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            categories.forEach(entry -> {
                // 仅保留分类名称用于图例，隐藏扇区文字
                PieChart.Data data = new PieChart.Data(
                        entry.getKey(),  // 分类名称用于图例
                        entry.getValue()
                );

                // 可选：添加提示信息（鼠标悬停时显示详情）
                Tooltip.install(data.getNode(), new Tooltip(
                        String.format("%s\n金额：%.2f\n占比：%.1f%%",
                                entry.getKey(),
                                entry.getValue(),
                                (entry.getValue() / total) * 100)
                ));

                expenseCategoriesChart.getData().add(data);
            });
        }
    }

    private void refreshDailyCharts(LocalDate date) {
        expenseCategoriesChart.getData().clear();

        // 隐藏饼图扇区上的文字标签
        expenseCategoriesChart.setLabelsVisible(false);

        FinanceData.DailyData dailyData = financeData.getDailyData(date);
        if (dailyData != null && !dailyData.topCategories.isEmpty()) {
            double total = dailyData.topCategories.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            dailyData.topCategories.forEach(entry -> {
                // 仅保留分类名称用于图例
                PieChart.Data data = new PieChart.Data(
                        entry.getKey(),  // 分类名称用于图例
                        entry.getValue()
                );

                // 添加提示信息
                Tooltip.install(data.getNode(), new Tooltip(
                        String.format("%s\n金额：%.2f\n占比：%.1f%%",
                                entry.getKey(),
                                entry.getValue(),
                                (entry.getValue() / total) * 100)
                ));

                expenseCategoriesChart.getData().add(data);
            });
        }
    }


    @FXML
    private void back_to_main() {
        System.out.println("切换页面");
        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
    }


    /**
            * 从指定目录的JSON文件中读取所有纪念日
 * 包含所有纪念日的List<String>
 * IOException 如果目录不存在或读取文件出错
 */
    public List<String> getAllAnniversaries() throws IOException {
        // 1. 获取当前账本ID和目录路径
        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
        String jsonDir = "src/main/resources/thirdlevel_json";

        // 2. 准备结果集合
        List<String> anniversaries = new ArrayList<>();

        // 3. 检查目录是否存在
        Path dirPath = Paths.get(jsonDir);
        if (!Files.exists(dirPath)) {
            throw new IOException("纪念日目录不存在: " + jsonDir);
        }

        // 4. 遍历目录中的所有JSON文件
        Files.list(dirPath)
                .filter(path -> path.toString().endsWith(ledgerId + ".json"))
                .forEach(jsonFile -> {
                    try (FileReader reader = new FileReader(jsonFile.toFile())) {
                        // 5. 解析JSON文件
                        JSONObject json = new JSONObject(new JSONTokener(reader));

                        // 6. 检查并获取纪念日数组
                        if (json.has("所有的纪念日")) {
                            JSONArray dates = json.getJSONArray("所有的纪念日");

                            // 7. 添加到结果集合
                            for (int i = 0; i < dates.length(); i++) {
                                anniversaries.add(dates.getString(i));
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("读取文件失败: " + jsonFile, e);
                    }
                });

        // 8. 返回结果
        return anniversaries;
    }
    @FXML
    public void loadAiAdvice(List<String> adviceList) {
        try {

            adviceList.clear();
            allAnniversaries = getAllAnniversaries();
            System.out.println(allAnniversaries);
















            // 加载 XML 文件中的数据
            String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
//

            // 定义XML文件的根目录（根据实际情况调整或从配置获取）
            String baseDir = "src/main/resources/";
            String resourcePath = baseDir + "Transactions_Record_XML/" + ledgerId;
            File folder = new File(resourcePath);

// 检查目录是否存在且为文件夹
            if (!folder.exists() || !folder.isDirectory()) {
                System.err.println("目录不存在：" + resourcePath);
                return;
            }

// 获取所有XML文件
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

            if (files == null || files.length == 0) {
                System.out.println("未找到XML文件");
                return;
            }

            for (File file : files) {
                String filePath = file.getAbsolutePath();
                System.out.println(filePath);
                TransactionAnalyzer analyzer = new TransactionAnalyzer(filePath);
                Map<String, Double> anomalies = detectAnomalies(analyzer, allAnniversaries);
                // 可选：输出异常的日期和金额
                System.out.println("Abnormally high spending dates:");
                anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));

                // 清空原始建议列表，避免重复


                // 将异常信息转换为 List<String> 格式
                anomalies.forEach((date, amount) -> {
                    adviceList.add("Abnormal spending detected on " + date + " confidence coefficient: " + String.format("%.2f", amount));
                });

                // 确保 UI 更新发生在 JavaFX 应用线程上
                Platform.runLater(() -> {
                    adviceBox.getChildren().clear(); // 清除现有内容

                    // 重新添加标题标签
                    Label titleLabel = new Label("Advice From AI");
                    titleLabel.getStyleClass().add("advice-title");
                    adviceBox.getChildren().add(titleLabel);

                    // 将新的 adviceList 内容加载到 UI
                    int i = 1;
                    for (String advice : adviceList) {
                        Label label = new Label(i + ". " + advice);
                        label.setWrapText(true);           // 启用自动换行
                        label.setMaxWidth(400);            // 可选：设置最大宽度（根据需要调整）
                        label.getStyleClass().remove("label");
                        label.getStyleClass().add("text-advice");
                        adviceBox.getChildren().add(label);

                        System.out.println("Label added: " + label.getText());
                        System.out.println("Label style class: " + label.getStyleClass());
                        i++;
                    }
                });

            }




        } catch (Exception e) {
            // 捕获其他异常并打印
            e.printStackTrace();
            System.err.println("加载 AI 建议时发生错误");
        }
    }



//    List<String> advice = Arrays.asList(
//            "Try to reduce dining out expenses.",
//            "Consider allocating more to savings.",
//            "Set a weekly budget goal."
//    );


//日历


    @FXML
    private void handlePrevMonth() {
        isDailyMode = false;
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
        isDailyMode = false;
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }


    private void updateCalendar() {
        // 更新月份标题
        monthTitle.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMM, yyyy")));

        // 清空网格
        calendarGrid.getChildren().clear();

        // 获取当前月份的天数
        int daysInMonth = currentMonth.lengthOfMonth();

        // 获取当前月份第一天是星期几（0=星期日，1=星期一，...）
        int firstDayOfWeek = currentMonth.atDay(1).getDayOfWeek().getValue() % 7;

        // 填充上个月的剩余天数
        YearMonth previousMonth = currentMonth.minusMonths(1);
        int daysInPreviousMonth = previousMonth.lengthOfMonth();
        for (int i = 0; i < firstDayOfWeek; i++) {
            int day = daysInPreviousMonth - firstDayOfWeek + i + 1;
            Label dayLabel = createDayLabel(day, previousMonth, false);
            dayLabel.setStyle("-fx-text-fill: #999999;");
            calendarGrid.add(dayLabel, i, 0);
        }

        // 填充当前月份的天数
        int row = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            int col = (day + firstDayOfWeek - 1) % 7;
            Label dayLabel = createDayLabel(day, currentMonth, true);

            // 添加标记
            LocalDate date = currentMonth.atDay(day);
            if (dateMarkers.containsKey(date)) {
                StringBuilder markerText = new StringBuilder();
                for (Transactions transaction : dateTransactions.get(date)) {
                    markerText.append(transaction.getAmount() > 0 ? "+" : "")
                            .append(transaction.getAmount())
                            .append("$\n");
                }
                dayLabel.setText(day + "\n" + markerText.toString().trim());
                dayLabel.setStyle("-fx-text-fill: #FF5722;"); // 标记文字颜色
            }

            // 添加点击事件
            dayLabel.setOnMouseClicked(event -> {
                // 移除之前选中日期的特效
                if (selectedDate != null) {
                    isDailyMode = !isDailyMode;
                    updateFinancialMetrics(isDailyMode);



                    Label previousLabel = findDayLabel(selectedDate);
                    if (previousLabel != null) {
                        previousLabel.getStyleClass().remove("selected-day");
                    }
                }

                // 应用选中日期的特效
                dayLabel.getStyleClass().add("selected-day");
                selectedDate = date;

                // 显示选中日期的详细信息
                showDateDetails(date);
            });

            calendarGrid.add(dayLabel, col, row);
            if (col == 6) row++; // 换行
        }

        // 填充下个月的前几天
        YearMonth nextMonth = currentMonth.plusMonths(1);
        int totalCells = 42; // 6行7列
        int filledCells = firstDayOfWeek + daysInMonth;
        for (int i = filledCells; i < totalCells; i++) {
            int col = i % 7;
            int day = i - filledCells + 1;
            Label dayLabel = createDayLabel(day, nextMonth, false);
            dayLabel.setStyle("-fx-text-fill: #999999;");
            calendarGrid.add(dayLabel, col, row);
            if (col == 6) row++;
        }
    }

//    private Label createDayLabel(int day, YearMonth month) {
//        Label label = new Label(String.valueOf(day));
//        label.setAlignment(Pos.CENTER);
//        label.setPrefSize(40, 40);
//        label.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
//        return label;
//    }

    private Label createDayLabel(int day, YearMonth month, boolean isCurrentMonth) {
        Label label = new Label(String.valueOf(day));
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(40, 40);
        label.getStyleClass().add("day-cell");

        if (!isCurrentMonth) {
            label.getStyleClass().add("other-month");
        }

        // 添加标记
        LocalDate date = month.atDay(day);
        if (dateMarkers.containsKey(date)) {
            String marker = dateMarkers.get(date);
            label.setText(day + "\n" + marker);
            label.getStyleClass().add("has-marker");
        }

        return label;
    }


    private Label findDayLabel(LocalDate date) {
        for (Node node : calendarGrid.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                String text = label.getText();
                if (text != null && !text.isEmpty()) {
                    try {
                        int day = Integer.parseInt(text.split("\n")[0]);
                        YearMonth month = currentMonth;
                        if (date.getMonthValue() != month.getMonthValue() || date.getYear() != month.getYear()) {
                            continue;
                        }
                        if (day == date.getDayOfMonth()) {
                            return label;
                        }
                    } catch (NumberFormatException e) {
                        // 忽略非数字的文本
                    }
                }
            }
        }
        return null;
    }

    private void showDateDetails(LocalDate date) {
        // 更新选中日期的标签
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));

        // 清空之前的收支项目
        expenseItemsContainer.getChildren().clear();

        // 检查是否有标记
        if (dateTransactions.containsKey(date)) {
            // 添加收支项目
            for (Transactions transaction : dateTransactions.get(date)) {
                HBox itemBox = new HBox(10);
                itemBox.setPadding(new Insets(10));
                itemBox.setStyle(transaction.getAmount() > 0 ? "-fx-background-color: #e8f5e9;" : "-fx-background-color: #f5f5f5;");
                itemBox.setPrefWidth(350);
                itemBox.setPrefHeight(50);
                itemBox.setStyle(transaction.getAmount() > 0
                        ? "-fx-background-color: #e8f5e9; -fx-background-radius: 8;"
                        : "-fx-background-color: #f5f5f5; -fx-background-radius: 8;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(24);
                imageView.setFitHeight(24);
                // 使用 getClass().getResource() 确保图像资源被正确加载
                String imagePath = transaction.getAmount() > 0 ? "/images/expense.png" : "/images/expense.png";
                imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));

                Label itemName = new Label(transaction.getDescription());
                itemName.setStyle(transaction.getAmount() > 0 ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FF5722;");
                itemName.setPrefWidth(200);

                Label itemAmount = new Label(transaction.getAmount() > 0 ? "+" + transaction.getAmount() + "$" : "" + transaction.getAmount() + "$");
                itemAmount.setStyle(transaction.getAmount() > 0 ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FF5722;");
                HBox.setHgrow(itemAmount, Priority.ALWAYS);

                itemBox.getChildren().addAll(imageView, itemName, itemAmount);
                expenseItemsContainer.getChildren().add(itemBox);
            }
        } else {
            // 没有标记时显示提示信息
            Label noDataLabel = new Label("No records for this date");
            noDataLabel.setStyle("-fx-text-fill: #999999;");
            expenseItemsContainer.getChildren().add(noDataLabel);
        }
    }

    String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
    // 添加成员变量


    @FXML
    private void handleAddTransaction() {
        showTransactionDialog();
    }

    private void showTransactionDialog() {
        Dialog<TransactionData> dialog = new Dialog<>();
        dialog.setTitle("新增交易");

        // 创建表单组件
        TextField counterpartyField = new TextField();
        TextField productField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Income", "Expense", "Transfer"
        ));
        TextField amountField = new TextField();

        // 金额输入验证
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldVal);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("交易方:"), counterpartyField);
        grid.addRow(1, new Label("产品:"), productField);
        grid.addRow(2, new Label("类型:"), typeCombo);
        grid.addRow(3, new Label("金额:"), amountField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 转换结果
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new TransactionData(
                        counterpartyField.getText(),
                        productField.getText(),
                        typeCombo.getValue(),
                        amountField.getText()
                );
            }
            return null;
        });

        Optional<TransactionData> result = dialog.showAndWait();
        result.ifPresent(this::processTransaction);
    }

    private void processTransaction(TransactionData data) {
        try {
            // 生成交易ID
            String id = generateTransactionId();

            // 自动生成日期
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            // AI分类
            Map<String,String> back_category = classifyTransaction(data.product(), data.counterparty());
            String category = back_category.values().iterator().next();

            // 生成XML
            Document doc = createTransactionXml(id, date, data, category);

            // 保存文件
            saveTransactionFile(doc, id);



            TransactionDataLoader dataLoader = new TransactionDataLoader();
            dataLoader.loadFromXML(doc.getDocumentURI());
            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();




            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
            processor.process();



            // 10.4 保存到三级目录

            try {
                processor.saveToThirdLevel(THIRD_DIR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("财务数据已保存至：" +
                    Paths.get(THIRD_DIR, ledgerId + ".json"));


            // 合并文件
            mergeTransactionFiles();
//
//            TransactionDataLoader dataLoader = new TransactionDataLoader();
//            dataLoader.loadFromXML(fourthLevelXmlFile.getAbsolutePath());
//            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();
//
//
//
//
//            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
//            processor.process();



            // 10.4 保存到三级目录

//            X



            // 刷新UI
            refreshUI();

        } catch (Exception e) {
            showErrorAlert("交易处理失败", e.getMessage());
        }
    }

    private String generateTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
    }

    private Map<String,String> classifyTransaction(String product, String counterparty) throws Exception {
        Path tokenizerDir = Paths.get("src/main/resources/Tokenizer"); // Path to the model's tokenizer
        String modelPath = "src/main/resources/bert_transaction_categorization.onnx"; // Path to the model
        Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json"); //Path to the merchant description
        TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

        return classifier.classify(product + " " + counterparty);

    }

    private Document createTransactionXml(String id, String date, TransactionData data, String category)
            throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().newDocument();

        Element transaction = doc.createElement("transaction");

        appendXmlElement(doc, transaction, "id", id);
        appendXmlElement(doc, transaction, "date", date);
        appendXmlElement(doc, transaction, "counterparty", data.counterparty());
        appendXmlElement(doc, transaction, "product", data.product());
        appendXmlElement(doc, transaction, "type", data.type());
        appendXmlElement(doc, transaction, "amount", data.amount());
        appendXmlElement(doc, transaction, "class", category);

        doc.appendChild(transaction);
        return doc;
    }

    private void appendXmlElement(Document doc, Element parent, String tagName, String value) {
        Element elem = doc.createElement(tagName);
        elem.setTextContent(value);
        parent.appendChild(elem);
    }

    private void saveTransactionFile(Document doc, String id) throws Exception {
        String dirPath = "src/main/resources/fourthlevel_xml/" + ledgerId + "/";
        Path outputDir = Paths.get(dirPath);

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        Path outputPath = outputDir.resolve(id + ".xml");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(outputPath.toFile()));
    }

    private void mergeTransactionFiles() throws Exception {
        String inputDir = "src/main/resources/fourthlevel_xml/" + ledgerId + "/";
        String outputFile = inputDir + "merged_transactions.xml";

        if (TransactionXmlMerger.mergeTransactionXmlFiles(inputDir, outputFile) != null) {
            String FF  = TransactionXmlMerger.mergeTransactionXmlFiles(inputDir, outputFile);

            TransactionDataLoader dataLoader = new TransactionDataLoader();
            dataLoader.loadFromXML(FF);
            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();




            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
            processor.process();



            // 10.4 保存到三级目录

            try {
                processor.saveToThirdLevel(THIRD_DIR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("财务数据已保存至：" +
                    Paths.get(THIRD_DIR, ledgerId + ".json"));

            cleanUpIndividualFiles(inputDir);
        }
    }

    private void cleanUpIndividualFiles(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles((d, name) ->
                name.endsWith(".xml") && !name.equals("merged_transactions.xml")
        );

        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    System.err.println("无法删除文件: " + file.getAbsolutePath());
                }
            }
        }
    }

    private void refreshUI() {
        // 实现UI刷新逻辑


            // 加载 MainController 和主界面


            // 获取 MainController


            // 加载 Ledger 页面


            // 获取 LedgerController 并传递账本

//            ledger = GlobalContext.getInstance().getCurrentLedger();
//            MainController.getInstance().loadPage("ledger.fxml");
//            Object controller = MainController.getInstance().getCurrentController();
//
//
//
//            if (controller instanceof LedgerController ledgerController) {
//                ledgerController.loadLedger(this.ledger);
//            }

//        try {
//            financeData.loadFinanceData(ledger.getId());
//
//
//            if (isDailyMode) {
//                // 日模式图表
//                refreshDailyCharts(selectedDate);
//            } else {
//                // 月模式图表
//                refreshMonthlyCharts();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        updateDashboard();










    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 数据记录类
    private record TransactionData(
            String counterparty,
            String product,
            String type,
            String amount
    ) {}

}


