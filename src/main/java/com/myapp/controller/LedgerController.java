package com.myapp.controller;

import AIUtilities.classification.TransactionClassifier;
import AIUtilities.prediction.ARIMAModel;
import com.myapp.util.I18nUtil;
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
import java.time.temporal.ChronoUnit;
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

    @FXML private Label noDataLabel;

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


        ledger = GlobalContext.getInstance().getCurrentLedger();


        if (ledger != null) {


            this.financeData = new FinanceData();  // 正确初始化
            try {
                financeData.loadFinanceData(ledger.getId());
                updateFinancialMetrics(isDailyMode);

//                if (isDailyMode) {
//                    // 日模式图表
//                    refreshDailyCharts(selectedDate);
//                } else {
//                    // 月模式图表
//                    refreshMonthlyCharts();
//                }
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
        refreshCharts(isDailyMode);
//        expenseCategoriesChart.getData().clear();



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
                showAlert(I18nUtil.get("alert.tip"), I18nUtil.get("anniversary.no.data"));
                return;
            }

            String content = String.join("\n", anniversaries);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(I18nUtil.get("anniversary.all.title"));
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();

        } catch (IOException e) {
            showAlert(I18nUtil.get("alert.error"),
                    I18nUtil.get("anniversary.load.fail") + ": " + e.getMessage());
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(I18nUtil.get("anniversary.add.title"));
        dialog.setHeaderText(I18nUtil.get("anniversary.add.header"));
        dialog.setContentText(I18nUtil.get("anniversary.add.content"));

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(dateStr -> {
            try {
                if (!dateStr.matches("(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])")) {
                    throw new IllegalArgumentException(I18nUtil.get("anniversary.invalid.format"));
                }

                String jsonFilePath = "src/main/resources/thirdlevel_json/" + ledgerId + ".json";
                JSONObject rootObject;
                try (FileReader reader = new FileReader(jsonFilePath)) {
                    rootObject = new JSONObject(new JSONTokener(reader));
                }

                JSONArray memorialDays = rootObject.optJSONArray("所有的纪念日");
                if (memorialDays == null) {
                    memorialDays = new JSONArray();
                    rootObject.put("anniversaries", memorialDays);
                }

                if (memorialDays.toList().contains(dateStr)) {
                    throw new IllegalArgumentException(I18nUtil.get("anniversary.exists"));
                }

                memorialDays.put(dateStr);

                try (FileWriter writer = new FileWriter(jsonFilePath)) {
                    writer.write(rootObject.toString(4));
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(I18nUtil.get("alert.success"));
                    alert.setHeaderText(null);
                    alert.setContentText(I18nUtil.get("anniversary.add.success") + ": " + dateStr);
                    alert.showAndWait();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(I18nUtil.get("alert.error"));
                    alert.setHeaderText(I18nUtil.get("anniversary.add.fail"));
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

    private void showBudgetSettingDialog() {
        Dialog<Pair<Double, Double>> dialog = new Dialog<>();
        dialog.setTitle(I18nUtil.get("budget.set.title"));
        dialog.setHeaderText(I18nUtil.get("budget.set.header"));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField budgetField = new TextField();
        budgetField.setPromptText(I18nUtil.get("budget.daily"));
        TextField spentField = new TextField();
        spentField.setPromptText(I18nUtil.get("budget.spent"));

        grid.add(new Label(I18nUtil.get("budget.daily") + ":"), 0, 0);
        grid.add(budgetField, 1, 0);
        grid.add(new Label(I18nUtil.get("budget.spent") + ":"), 0, 1);
        grid.add(spentField, 1, 1);

        dialog.getDialogPane().setContent(grid);

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

        Optional<Pair<Double, Double>> result = dialog.showAndWait();

        result.ifPresent(budgetData -> {
            double budget = budgetData.getKey();
            double spent = budgetData.getValue();
            double remain = budget - spent;

            budgetLabel.setText(String.format(I18nUtil.get("budget.format"), budget));
            remainLabel.setText(String.format(I18nUtil.get("budget.remain.format"), remain));
            spentLabel.setText(String.format(I18nUtil.get("budget.spent.format"), spent));

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
            String monthKey = selectedDate.getYear() + "-" + selectedDate.getMonthValue();
            String monthKeyShort = selectedDate.getYear() + "-" + selectedDate.getMonthValue(); // 简写格式如"2025-5"

            // 获取或创建日数据对象
            JSONObject dayData = rootObject.optJSONObject(dateKey);
            if (dayData == null) {
                dayData = new JSONObject();
                // 初始化所有日级字段
                dayData.put("日预算", 0);
                dayData.put("日已花预算", 0);
                dayData.put("日剩余预算", 0);
                dayData.put("日支出", 0);
                dayData.put("日收入", 0);
                dayData.put("日报销", 0);
                dayData.put("日已报销", 0);
                dayData.put("日未报销", 0);
                dayData.put("前四个支出类别", new JSONArray());
            }

            // 保存旧值用于计算差值
            double oldBudget = dayData.optDouble("日预算", 0);
            double oldSpent = dayData.optDouble("日已花预算", 0);

            // 更新日数据
            dayData.put("日预算", budget);
            dayData.put("日已花预算", spent);
            dayData.put("日剩余预算", remain);
            rootObject.put(dateKey, dayData);

            // 更新月数据（完整月份格式如"2025-03"）
            JSONObject monthData = rootObject.optJSONObject(monthKey);
            if (monthData == null) {
                monthData = new JSONObject();
                // 初始化所有月级字段
                monthData.put("月预算", 0);
                monthData.put("月已花预算", 0);
                monthData.put("月剩余预算", 0);
                monthData.put("月收入", 0);
                monthData.put("月支出", 0);
                monthData.put("月报销", 0);
                monthData.put("月已报销", 0);
                monthData.put("月未报销", 0);
                monthData.put("前四个支出类别", new JSONArray());
            }

            // 更新月数据（简写月份格式如"2025-5"）
            JSONObject monthDataShort = rootObject.optJSONObject(monthKeyShort);
            if (monthDataShort == null) {
                monthDataShort = new JSONObject();
                monthDataShort.put("月预算", 0);
                monthDataShort.put("月已花预算", 0);
                monthDataShort.put("月剩余预算", 0);
            }

            // 计算差值并更新完整月份数据
            monthData.put("月预算", monthData.optDouble("月预算", 0) + (budget - oldBudget));
            monthData.put("月已花预算", monthData.optDouble("月已花预算", 0) + (spent - oldSpent));
            monthData.put("月剩余预算",
                    monthData.getDouble("月预算") - monthData.getDouble("月已花预算"));
            rootObject.put(monthKey, monthData);

            // 更新简写月份数据
            monthDataShort.put("月预算", monthDataShort.optDouble("月预算", 0) + (budget - oldBudget));
            monthDataShort.put("月已花预算", monthDataShort.optDouble("月已花预算", 0) + (spent - oldSpent));
            monthDataShort.put("月剩余预算",
                    monthDataShort.getDouble("月预算") - monthDataShort.getDouble("月已花预算"));
            rootObject.put(monthKeyShort, monthDataShort);

            // 更新根级别的月汇总数据（如果存在）
            if (rootObject.has("月预算")) {
                rootObject.put("月预算", rootObject.optDouble("月预算", 0) + (budget - oldBudget));
                rootObject.put("月已花预算", rootObject.optDouble("月已花预算", 0) + (spent - oldSpent));
                rootObject.put("月剩余预算",
                        rootObject.getDouble("月预算") - rootObject.getDouble("月已花预算"));
            }

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
    public void updateDashboard() {
        dateTransactions.clear(); // 清空整个 Map
        TransactionLoader loader = new TransactionLoader();
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




        try {
            financeData.loadFinanceData(ledger.getId());


//                if (isDailyMode) {
//                    // 日模式图表
//                    refreshDailyCharts(selectedDate);
//                } else {
//                    // 月模式图表
//                    refreshMonthlyCharts();
//                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



//        isDailyMode = selectedDate != null;
        // 获取当前显示模式
//        updateCalendar();

        refreshCharts(isDailyMode);

        updateFinancialMetrics(isDailyMode);
//        LocalDate today = LocalDate.now();
//        showDateDetails(today);
        // 更新核心财务指标

        // 更新图表数据
        refreshCharts(isDailyMode);

        // 加载智能建议
        loadAiAdvice(adviceList);
        initializeCharts();

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
        LocalDate today = LocalDate.now();

        if(selectedDate != null) {showDateDetails(selectedDate);}
        else {showDateDetails(today);}



    }
    private void initializeCharts() {
        lineChart.getData().clear();
        // Initialize line chart
        xAxis.setLabel(I18nUtil.get("chart.time.label"));
        yAxis.setLabel(I18nUtil.get("chart.value.label"));

        // Get daily transaction data for a period
        DailyTransactionProcessor processor = new DailyTransactionProcessor();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(I18nUtil.get("chart.series.expense"));

        // Set date range dynamically
        LocalDate startDate;
        LocalDate endDate;

        if (selectedDate != null) {
            startDate = selectedDate;
            endDate = startDate.plusDays(7); // Default show 7 days data
        } else {
            startDate = LocalDate.of(2025, 3, 23);
            endDate = LocalDate.of(2025, 3, 31);
        }

        // Prepare historical data array
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double[] expense_history = new double[daysBetween];
        int index = 0;

        // Fill historical data (including budget)
        XYChart.Series<String, Number> historicalBudgetSeries = new XYChart.Series<>();
        historicalBudgetSeries.setName(I18nUtil.get("chart.series.historical.budget"));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM/dd", Locale.US));
            double expense = processor.getTotalExpenses("2025/" + dateStr);

            // Add expense data
            series.getData().add(new XYChart.Data<>(dateStr, expense));
            expense_history[index] = expense;

            // Add historical budget data
            FinanceData.DailyData dailyData = financeData.getDailyData(date);
            double dailyBudget = (dailyData != null) ? dailyData.budget : 0;
            historicalBudgetSeries.getData().add(new XYChart.Data<>(dateStr, dailyBudget));

            index++;
        }

        // Add historical data series to chart
        lineChart.getData().add(series);
        lineChart.getData().add(historicalBudgetSeries);

        // Create ARIMA model and make predictions
        ARIMAModel model = new ARIMAModel(expense_history, 1, 4, 5);
        int steps = 3; // Predict 3 days
        int[] forecasts = model.predict(steps);

        // Create forecast series
        XYChart.Series<String, Number> forecastSeries = new XYChart.Series<>();
        forecastSeries.setName(I18nUtil.get("chart.series.forecast"));

        // Create forecast period budget series
        XYChart.Series<String, Number> forecastBudgetSeries = new XYChart.Series<>();
        forecastBudgetSeries.setName(I18nUtil.get("chart.series.forecast.budget"));

        // Add forecast data points and budget data
        LocalDate lastHistoryDate = endDate;
        for (int i = 0; i < steps; i++) {
            lastHistoryDate = lastHistoryDate.plusDays(1);
            String forecastDate = lastHistoryDate.format(DateTimeFormatter.ofPattern("MM/dd", Locale.US));

            // Add forecast data
            forecastSeries.getData().add(new XYChart.Data<>(forecastDate, Math.max(forecasts[i], 0)));

            // Add forecast period budget data
            FinanceData.DailyData dailyData = financeData.getDailyData(lastHistoryDate);
            double dailyBudget = (dailyData != null) ? dailyData.budget : 0;
            forecastBudgetSeries.getData().add(new XYChart.Data<>(forecastDate, dailyBudget));
        }

        // Add series to chart
        lineChart.getData().add(forecastSeries);
        lineChart.getData().add(forecastBudgetSeries);

        // Set series styles
        series.getNode().setStyle("-fx-stroke: #1e90ff; -fx-stroke-width: 2px;"); // Blue solid line - historical expense
        historicalBudgetSeries.getNode().setStyle("-fx-stroke: #32cd32; -fx-stroke-width: 2px;"); // Green solid line - historical budget

        forecastSeries.getNode().setStyle(
                "-fx-stroke: #ff0000; " +          // Red dashed line - forecast expense
                        "-fx-stroke-dash-array: 5 5; " +
                        "-fx-stroke-width: 2px;"
        );

        forecastBudgetSeries.getNode().setStyle(
                "-fx-stroke: #228b22; " +         // Dark green dashed line - forecast budget
                        "-fx-stroke-dash-array: 5 5; " +
                        "-fx-stroke-width: 2px;"
        );

        // Add legend
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(true);
    }

    private void updateFinancialMetrics(boolean isDailyMode) {
        // Get corresponding data
        FinanceData.DailyData dailyData = isDailyMode ? financeData.getDailyData(selectedDate) : null;
        FinanceData.MonthlyData monthlyData = financeData.getMonthlyData();

        // Common format strings
        final String LABEL_FORMAT = I18nUtil.get("financial.label.format");
        final String NA_LABEL = I18nUtil.get("financial.na.value");

        // Update basic income/expense metrics
        if (isDailyMode) {
            // Daily mode display
            if (dailyData != null) {
                // Income/expense info
                expenseLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.expense"),
                        formatAmount(dailyData.expense, true)));
                incomeLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.income"),
                        formatAmount(dailyData.income, true)));
                double balance = dailyData.income - dailyData.expense;
                balanceLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.balance"),
                        formatAmount(balance, false)));

                // Budget info (daily mode)
                budgetLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.budget"),
                        formatAmount(dailyData.budget, true)));
                remainLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.remaining"),
                        formatAmount(dailyData.remainingBudget, true)));
                spentLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.spent"),
                        formatAmount(dailyData.spentBudget, true)));
            } else {
                expenseLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.expense"), NA_LABEL));
                incomeLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.income"), NA_LABEL));
                balanceLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.balance"), NA_LABEL));

                budgetLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.budget"), NA_LABEL));
                remainLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.remaining"), NA_LABEL));
                spentLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.daily.spent"), NA_LABEL));
            }
        } else {
            // Monthly mode display
            if (monthlyData != null) {
                // Income/expense info
                expenseLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.expense"),
                        formatAmount(monthlyData.expense, true)));
                incomeLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.income"),
                        formatAmount(monthlyData.income, true)));
                double balance = monthlyData.income - monthlyData.expense;
                balanceLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.balance"),
                        formatAmount(balance, false)));

                // Budget info (monthly mode)
                budgetLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.budget"),
                        formatAmount(monthlyData.budget, true)));
                remainLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.remaining"),
                        formatAmount(monthlyData.remainingBudget, true)));
                spentLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.spent"),
                        formatAmount(monthlyData.spentBudget, true)));
            } else {
                expenseLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.expense"), NA_LABEL));
                incomeLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.income"), NA_LABEL));
                balanceLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.balance"), NA_LABEL));

                budgetLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.budget"), NA_LABEL));
                remainLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.remaining"), NA_LABEL));
                spentLabel.setText(String.format(LABEL_FORMAT,
                        I18nUtil.get("financial.monthly.spent"), NA_LABEL));
            }
        }

        // Update asset overview (always uses monthly data)
        if (monthlyData != null) {
            netAssetsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.net.assets"),
                    formatAmount(monthlyData.income - monthlyData.expense, true)));
            totalsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.total.assets"),
                    formatAmount(monthlyData.income, true)));
            debtsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.debts"),
                    formatAmount(monthlyData.expense, true)));
        } else {
            netAssetsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.net.assets"), NA_LABEL));
            totalsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.total.assets"), NA_LABEL));
            debtsLabel.setText(String.format(LABEL_FORMAT,
                    I18nUtil.get("financial.debts"), NA_LABEL));
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
//            initializeCharts();
        } else {
            // 月模式图表
            refreshMonthlyCharts();
//            initializeCharts();
        }
    }

    // 在图表控制类中添加
    private void refreshMonthlyCharts() {
        expenseCategoriesChart.getData().clear();

        // Hide text labels on pie chart sectors
        expenseCategoriesChart.setLabelsVisible(false);

        List<Map.Entry<String, Double>> categories = financeData.getMonthlyData().topCategories;
        if (!categories.isEmpty()) {
            double total = categories.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            categories.forEach(entry -> {
                // Keep category name for legend, hide sector text
                PieChart.Data data = new PieChart.Data(
                        entry.getKey(),  // Category name for legend
                        entry.getValue()
                );

                // Optional: Add tooltip (shows details on hover)
                Tooltip.install(data.getNode(), new Tooltip(
                        String.format(I18nUtil.get("chart.tooltip.format"),
                                entry.getKey(),
                                entry.getValue(),
                                (entry.getValue() / total) * 100)
                ));

                expenseCategoriesChart.getData().add(data);
            });
        }
        else {
            PieChart.Data noData = new PieChart.Data(I18nUtil.get("chart.no.data.monthly"), 1);
            expenseCategoriesChart.getData().add(noData);

            // Set special style
            noData.getNode().setStyle("-fx-pie-color: #e0e0e0;"); // Light gray
        }
    }

    private void refreshDailyCharts(LocalDate date) {
        expenseCategoriesChart.getData().clear();

        // Hide text labels on pie chart sectors
        expenseCategoriesChart.setLabelsVisible(false);

        FinanceData.DailyData dailyData = financeData.getDailyData(date);
        if (dailyData != null && !dailyData.topCategories.isEmpty()) {
            double total = dailyData.topCategories.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            dailyData.topCategories.forEach(entry -> {
                // Keep category name for legend
                PieChart.Data data = new PieChart.Data(
                        entry.getKey(),  // Category name for legend
                        entry.getValue()
                );

                // Add tooltip
                Tooltip.install(data.getNode(), new Tooltip(
                        String.format(I18nUtil.get("chart.tooltip.format"),
                                entry.getKey(),
                                entry.getValue(),
                                (entry.getValue() / total) * 100)
                ));

                expenseCategoriesChart.getData().add(data);
            });
        }
        else {
            PieChart.Data noData = new PieChart.Data(I18nUtil.get("chart.no.data.daily"), 1);
            expenseCategoriesChart.getData().add(noData);

            // Set special style
            noData.getNode().setStyle("-fx-pie-color: #e0e0e0;"); // Light gray
        }
    }


    @FXML
    private void back_to_main() {
        // 获取当前按钮所在的 Stage
        Stage stage = (Stage) BackButton.getScene().getWindow();

        // 关闭当前窗口
        stage.close();


    }


    /**
            * 从指定目录的JSON文件中读取所有纪念日
 * 包含所有纪念日的List<String>
 * IOException 如果目录不存在或读取文件出错
 */
    public List<String> getAllAnniversaries() throws IOException {
        // 1. Get current ledger ID and directory path
        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
        String jsonDir = "src/main/resources/thirdlevel_json";

        // 2. Prepare result collection
        List<String> anniversaries = new ArrayList<>();

        // 3. Check if directory exists
        Path dirPath = Paths.get(jsonDir);
        if (!Files.exists(dirPath)) {
            throw new IOException(I18nUtil.get("anniversary.dir.missing") + ": " + jsonDir);
        }

        // 4. Iterate through all JSON files in directory
        Files.list(dirPath)
                .filter(path -> path.toString().endsWith(ledgerId + ".json"))
                .forEach(jsonFile -> {
                    try (FileReader reader = new FileReader(jsonFile.toFile())) {
                        // 5. Parse JSON file
                        JSONObject json = new JSONObject(new JSONTokener(reader));

                        // 6. Check and get anniversary array
                        if (json.has(I18nUtil.get("anniversary.json.key"))) {
                            JSONArray dates = json.getJSONArray(I18nUtil.get("anniversary.json.key"));

                            // 7. Add to result collection
                            for (int i = 0; i < dates.length(); i++) {
                                anniversaries.add(dates.getString(i));
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(I18nUtil.get("anniversary.file.read.error") + ": " + jsonFile, e);
                    }
                });

        // 8. Return results
        return anniversaries;
    }

    @FXML
    public void loadAiAdvice(List<String> adviceList) {
        try {
            adviceList.clear();
            allAnniversaries = getAllAnniversaries();
            System.out.println(allAnniversaries);

            // Load data from XML files
            String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
            String baseDir = "src/main/resources/";
            String resourcePath = baseDir + "Transactions_Record_XML/" + ledgerId;
            File folder = new File(resourcePath);

            // Check if directory exists and is a folder
            if (!folder.exists() || !folder.isDirectory()) {
                System.err.println(I18nUtil.get("xml.dir.missing") + ": " + resourcePath);
                return;
            }

            // Get all XML files
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

            if (files == null || files.length == 0) {
                System.out.println(I18nUtil.get("xml.files.not.found"));
                return;
            }

            for (File file : files) {
                String filePath = file.getAbsolutePath();
                System.out.println(filePath);
                TransactionAnalyzer analyzer = new TransactionAnalyzer(filePath);
                Map<String, Double> anomalies = detectAnomalies(analyzer, allAnniversaries);

                // Optional: Output abnormal dates and amounts
                System.out.println(I18nUtil.get("ai.abnormal.spending.dates"));
                anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));

                // Convert anomalies to List<String> format
                anomalies.forEach((date, amount) -> {
                    adviceList.add(String.format(I18nUtil.get("ai.advice.format"),
                            date,
                            String.format("%.2f", amount)));
                });

                // Ensure UI updates happen on JavaFX thread
                Platform.runLater(() -> {
                    adviceBox.getChildren().clear(); // Clear existing content

                    // Re-add title label
                    Label titleLabel = new Label(I18nUtil.get("ai.advice.title"));
                    titleLabel.getStyleClass().add("advice-title");
                    adviceBox.getChildren().add(titleLabel);

                    // Load new adviceList content to UI
                    int i = 1;
                    for (String advice : adviceList) {
                        Label label = new Label(i + ". " + advice);
                        label.setWrapText(true);           // Enable text wrapping
                        label.setMaxWidth(400);            // Optional: Set max width
                        label.getStyleClass().remove("label");
                        label.getStyleClass().add("text-advice");
                        adviceBox.getChildren().add(label);

                        System.out.println(I18nUtil.get("ai.label.added") + ": " + label.getText());
                        System.out.println(I18nUtil.get("ai.label.style") + ": " + label.getStyleClass());
                        i++;
                    }
                });
            }
        } catch (Exception e) {
            // Catch other exceptions and print
            e.printStackTrace();
            System.err.println(I18nUtil.get("ai.advice.load.error"));
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
        monthTitle.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMM, yyyy", Locale.US)));

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

                    updateFinancialMetrics(isDailyMode);



                    Label previousLabel = findDayLabel(selectedDate);
                    if (previousLabel != null) {
                        previousLabel.getStyleClass().remove("selected-day");
                    }
                }

                // 应用选中日期的特效
                dayLabel.getStyleClass().add("selected-day");
                selectedDate = date;
                isDailyMode = true;
                updateDashboard();

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

    String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();

    // 添加成员变量




    private void showTransactionDialog() {
        Dialog<TransactionData> dialog = new Dialog<>();
        dialog.setTitle(I18nUtil.get("transaction.dialog.title"));

        // Create form components
        TextField counterpartyField = new TextField();
        TextField productField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                I18nUtil.get("transaction.type.income"),
                I18nUtil.get("transaction.type.expense"),
                I18nUtil.get("transaction.type.transfer")
        ));
        TextField amountField = new TextField();

        // Amount input validation
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldVal);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label(I18nUtil.get("transaction.label.counterparty") + ":"), counterpartyField);
        grid.addRow(1, new Label(I18nUtil.get("transaction.label.product") + ":"), productField);
        grid.addRow(2, new Label(I18nUtil.get("transaction.label.type") + ":"), typeCombo);
        grid.addRow(3, new Label(I18nUtil.get("transaction.label.amount") + ":"), amountField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert result
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

    @FXML
    private void handleAddTransaction() {
        showTransactionDialog();
    }

    private void showDateDetails(LocalDate date) {
        // Update selected date label with localized format
        selectedDateLabel.setText(
                date.format(
                        DateTimeFormatter.ofPattern(I18nUtil.get("date.format.long"), Locale.getDefault())
                )
        );

        // Clear previous transaction items
        expenseItemsContainer.getChildren().clear();

        // Check if date has transactions
        if (dateTransactions.containsKey(date)) {
            // Add transaction items
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
                // Using same image for both income/expense as in original
                String imagePath = transaction.getAmount() > 0 ? "/images/expense.png" : "/images/expense.png";
                imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));

                Label itemName = new Label(transaction.getDescription());
                itemName.setStyle(transaction.getAmount() > 0 ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FF5722;");
                itemName.setPrefWidth(200);

                // Format amount with localized currency symbol if needed
                String amountText = transaction.getAmount() > 0 ?
                        "+" + transaction.getAmount() + I18nUtil.get("currency.symbol") :
                        "" + transaction.getAmount() + I18nUtil.get("currency.symbol");
                Label itemAmount = new Label(amountText);
                itemAmount.setStyle(transaction.getAmount() > 0 ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FF5722;");
                HBox.setHgrow(itemAmount, Priority.ALWAYS);

                itemBox.getChildren().addAll(imageView, itemName, itemAmount);
                expenseItemsContainer.getChildren().add(itemBox);
            }
        } else {
            // Show no data message
            Label noDataLabel = new Label(I18nUtil.get("transaction.no.data"));
            noDataLabel.setStyle("-fx-text-fill: #999999;");
            expenseItemsContainer.getChildren().add(noDataLabel);
        }
    }

    private void processTransaction(TransactionData data) {
        try {
            // Generate transaction ID
            String id = generateTransactionId();

            // Auto-generate date with localized format
            String date;
            if (selectedDate != null) {
                date = selectedDate.format(DateTimeFormatter.ofPattern(I18nUtil.get("date.format.short")));
            } else {
                date = LocalDate.now().format(DateTimeFormatter.ofPattern(I18nUtil.get("date.format.short")));
            }

            // AI classification
            Map<String,String> back_category = classifyTransaction(data.product(), data.counterparty());
            String category = back_category.values().iterator().next();

            // Generate XML
            Document doc = createTransactionXml(id, date, data, category);

            // Save file
            saveTransactionFile(doc, id);

            // Merge files
            mergeTransactionFiles();

            // Refresh UI
            refreshUI();

        } catch (Exception e) {
            showErrorAlert(I18nUtil.get("transaction.process.error"), e.getMessage());
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
        String FF  = TransactionXmlMerger.mergeTransactionXmlFiles(inputDir, outputFile);
        if (FF != null) {


            TransactionDataLoader dataLoader = new TransactionDataLoader();
            dataLoader.loadFromXML(FF);
            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();




            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
            processor.process();



//             10.4 保存到三级目录

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
//
//            if (controller instanceof LedgerController ledgerController) {
//                ledgerController.loadLedger(this.ledger);
//            }
//
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


