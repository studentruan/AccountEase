package com.myapp.controller;

import TransactionAnalyzer.TransactionAnalyzer;
import com.myapp.model.FinanceData;
import com.myapp.model.Ledger;
import com.myapp.model.TransactionLoader;
import com.myapp.model.Transactions;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.scene.layout.GridPane;


import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

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
    private Map<LocalDate, List<Transactions>> dateTransactions = new HashMap<>();

    private LocalDate selectedDate = null;


    @FXML
    private Label selectedDateLabel;

    @FXML
    private VBox expenseItemsContainer;

    List<String> adviceList = new ArrayList<>();


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
        TransactionLoader loader = new TransactionLoader();

        // 加载 XML 文件中的数据
        loader.loadTransactionsFromXml("src/main/output/classified_transactions1.xml");

        // 获取数据
        Map<String, Transactions> data = loader.getTransactionData();


        for (Transactions t : data.values()) {
            LocalDate date = t.getDate();
            dateTransactions.computeIfAbsent(date, k -> new ArrayList<>()).add(t);
        }
        // 不要在这里调用 updateDashboard()，因为 ledger 还未传入
    }

    public void loadLedger(Ledger ledger) {
        this.ledger = ledger;

        if (ledger != null) {
            this.financeData = new FinanceData(ledger);  // 正确初始化
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

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expense Statistics");

        // 将所有数据初始化为0
        series.getData().add(new XYChart.Data<>("Jan", 0));
        series.getData().add(new XYChart.Data<>("Feb", 0));
        series.getData().add(new XYChart.Data<>("Mar", 0));
        series.getData().add(new XYChart.Data<>("Apr", 0));
        series.getData().add(new XYChart.Data<>("May", 0));
        series.getData().add(new XYChart.Data<>("Jun", 0));
        series.getData().add(new XYChart.Data<>("Jul", 0));
        series.getData().add(new XYChart.Data<>("Aug", 0));
        series.getData().add(new XYChart.Data<>("Sep", 0));
        series.getData().add(new XYChart.Data<>("Oct", 0));
        series.getData().add(new XYChart.Data<>("Nov", 0));
        series.getData().add(new XYChart.Data<>("Dec", 0));

        lineChart.getData().add(series);

        // 初始化饼图，所有比例都设置为0
        PieChart.Data slice1 = new PieChart.Data("Shop", 71);
        PieChart.Data slice2 = new PieChart.Data("Dress", 10);
        PieChart.Data slice3 = new PieChart.Data("Car", 14);
        PieChart.Data slice4 = new PieChart.Data("Pet", 5);

        expenseCategoriesChart.getData().addAll(slice1, slice2, slice3, slice4);
    }


    public void updateDashboard() {

        // 更新支出
        expenseLabel.setText("Expense $" + financeData.getExpense());

        // 更新收入
        incomeLabel.setText("Income $" + financeData.getIncome());

        // 更新余额
        balanceLabel.setText("Balance $" + financeData.getBalance());

        // 更新预算
        budgetLabel.setText("Budget $" + financeData.getBudget());
        remainLabel.setText("Remain $" + (financeData.getBudget() - financeData.getExpense()));
        spentLabel.setText("Spent $" + financeData.getExpense());

        // 更新待处理
        pendingLabel.setText("Pending $" + financeData.getPending());
        claimedLabel.setText("Claimed $" + financeData.getClaimed());
        reimburLabel.setText("Reimbur $" + financeData.getReimbursement());

        // 更新净资产
        netAssetsLabel.setText("Net $" + financeData.getnNetAssets());
        totalsLabel.setText("Total $" + financeData.getTotals());
        debtsLabel.setText("Debts $" + financeData.getDebts());

        ObservableList<XYChart.Series<String, Number>> chartData = lineChart.getData();
        loadAiAdvice(adviceList);

// 确保 chartData 不为空并且包含至少一个 Series
//        if (chartData != null && !chartData.isEmpty()) {
//            XYChart.Series<String, Number> series = chartData.get(0);
//
//            // 只在 monthlyExpenses 不为空时清除并更新数据
//            Map<String, Double> monthlyExpenses = financeData.getMonthlyExpenses();  // 例如：{"Jan": 1000, "Feb": 1200, ...}
//            if (monthlyExpenses != null && !monthlyExpenses.isEmpty()) {
//                series.getData().clear();  // 清除现有数据
//                for (Map.Entry<String, Double> entry : monthlyExpenses.entrySet()) {
//                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
//                }
//            }
//
//            // 只在 expenseCategories 不为空时清除并更新数据
//            ObservableList<PieChart.Data> pieChartData = expenseCategoriesChart.getData();
//            Map<String, Double> expenseCategories = financeData.getExpenseCategories();  // 例如：{"Shop": 500, "Dress": 200, ...}
//            if (expenseCategories != null && !expenseCategories.isEmpty()) {
//                pieChartData.clear();  // 清除现有数据
//                for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
//                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
//                }
//            }
//        } else {
//            System.out.println("Line chart data is empty or not properly initialized.");
//        }


        // 刷新UI

        // 可以在这里更新其他UI元素，例如建议框


    }

    @FXML
    private void back_to_main() {
        System.out.println("切换页面");
        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
    }

    @FXML
    public void loadAiAdvice(List<String> adviceList) {
        try {
            // 检查文件是否存在
            File file = new File("src/main/resources/xml/test.xml");
            if (!file.exists()) {
                // 文件不存在时，不打印异常
                System.err.println("文件不存在: " + file.getAbsolutePath());
                return;  // 如果文件不存在，则退出方法
            }

            // 文件存在时，执行后续操作
            // 使用 TransactionAnalyzer 来分析数据
            TransactionAnalyzer analyzer = new TransactionAnalyzer("src/main/resources/xml/transactions1.xml");
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
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
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
}


