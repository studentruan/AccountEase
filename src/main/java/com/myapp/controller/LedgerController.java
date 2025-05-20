package com.myapp.controller;

import AIUtilities.classification.TransactionClassifier;
import AIUtilities.prediction.ARIMAModel;

import Backend.*;

import DataProcessor.DailyTransactionProcessor;
import DataProcessor.TransactionAnalyzer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import javafx.scene.layout.GridPane;


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

import static detectorTools.OutlierDetector.detectAnomalies;
import static detectorTools.OutlierDetector.outputOutliers;

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


    private LocalDate selectedDate = null;
    private boolean isDailyMode = false;



    @FXML
    private Label selectedDateLabel;

    @FXML
    private VBox expenseItemsContainer;

    List<String> adviceList = new ArrayList<>();
    private Map<LocalDate, List<Transactions>> dateTransactions = new HashMap<>();


//    @FXML
//    private FinanceData financeData = new FinanceData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置界面基础视图（不依赖账本数据）
        updateCalendar();

        LocalDate today = LocalDate.now();
        showDateDetails(today);

        initializeCharts();
        initializePictures();
        loadAiAdvice(adviceList);



        // 不要在这里调用 updateDashboard()，因为 ledger 还未传入
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
        isDailyMode = selectedDate != null;
        // 获取当前显示模式


        // 更新核心财务指标
        updateFinancialMetrics(isDailyMode);

        // 更新图表数据
        refreshCharts(isDailyMode);

        // 加载智能建议
        loadAiAdvice(adviceList);

        TransactionLoader loader = new TransactionLoader();
        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();

        // 步骤2：验证ID有效性
        if (ledgerId == null || ledgerId.trim().isEmpty()) {
            throw new IllegalStateException("未找到有效的账本ID，请先选择账本");
        }



        // 加载 XML 文件中的数据
        String resourcePath = "fourthlevel_xml/" + ledgerId;
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);

        if (resourceUrl == null) {
            System.err.println("目录不存在：" + resourcePath);
            return;
        }

        File folder = new File(resourceUrl.getFile());
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (files == null || files.length == 0) {
            System.out.println("未找到XML文件");
            return;
        }

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
}

    private void updateFinancialMetrics(boolean isDailyMode) {
        // 获取对应数据
        FinanceData.DailyData dailyData = isDailyMode ?
                financeData.getDailyData(selectedDate) : null;
        FinanceData.MonthlyData monthlyData = financeData.getMonthlyData();

        // 更新基础指标
        expenseLabel.setText(formatAmount(isDailyMode ? dailyData.expense : monthlyData.expense, isDailyMode));
        incomeLabel.setText(formatAmount(isDailyMode ? dailyData.income : monthlyData.income, isDailyMode));
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
        } else {
            // 月模式图表
            refreshMonthlyCharts();
        }
    }

    private void refreshDailyCharts(LocalDate date) {
        // 当天消费分类
        List<List<String>> categories = financeData.getDailyTopCategories();
        if (!categories.isEmpty()) {
            categories.get(0).forEach(category ->
                    expenseCategoriesChart.getData().add(
                            new PieChart.Data(category, 25) // 示例比例，需根据实际数据调整
                    )
            );
        }

        // 当天消费趋势（示例数据）
        initializeCharts();
    }

    private void refreshMonthlyCharts() {
        // 月度消费分类
        List<List<String>> categories = financeData.getMonthlyTopCategories();
        if (!categories.isEmpty()) {
            categories.get(0).forEach(category ->
                    expenseCategoriesChart.getData().add(
                            new PieChart.Data(category, 25) // 示例比例，需根据实际数据调整
                    )
            );
        }

        // 月度消费趋势（示例数据）
        initializeCharts();
    }

    @FXML
    private void back_to_main() {
        System.out.println("切换页面");
        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
    }

    @FXML
    public void loadAiAdvice(List<String> adviceList) {
        try {
            // 加载 XML 文件中的数据
            String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
//
            String resourcePath = "Transactions_Record_XML/" + ledgerId;
            URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
            System.out.println("资源路径: " + resourceUrl);

            if (resourceUrl == null) {
                System.err.println("目录不存在：" + resourcePath);
                return;
            }

            File folder = new File(resourceUrl.getFile());
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

            if (files == null || files.length == 0) {
                System.out.println("未找到XML文件");
                return;
            }

            for (File file : files) {
                String filePath = file.getAbsolutePath();
                System.out.println(filePath);
                TransactionAnalyzer analyzer = new TransactionAnalyzer(filePath);
                Map<String, Double> anomalies = outputOutliers(detectAnomalies(analyzer), 1.5);
                // 可选：输出异常的日期和金额
                System.out.println("Abnormally high spending dates:");
                anomalies.forEach((date, amount) -> System.out.printf(date + ": %.2f%n", amount));

                // 清空原始建议列表，避免重复
                adviceList.clear();

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
            String category = classifyTransaction(data.product(), data.counterparty());

            // 生成XML
            Document doc = createTransactionXml(id, date, data, category);

            // 保存文件
            saveTransactionFile(doc, id);

            // 合并文件
            mergeTransactionFiles();

            // 刷新UI
            refreshUI();

        } catch (Exception e) {
            showErrorAlert("交易处理失败", e.getMessage());
        }
    }

    private String generateTransactionId() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
    }

    private String classifyTransaction(String product, String counterparty) throws Exception {
        Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
        String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
        TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath);
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

    private void mergeTransactionFiles() {
        String inputDir = "src/main/resources/fourthlevel_xml/" + ledgerId + "/";
        String outputFile = inputDir + "merged_transactions.xml";

        if (TransactionXmlMerger.mergeTransactionXmlFiles(inputDir, outputFile) != null) {
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


