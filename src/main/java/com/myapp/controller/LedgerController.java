package com.myapp.controller;

import com.myapp.model.FinanceData;
import javafx.application.Platform;
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

import java.net.URL;
import java.util.*;

import javafx.scene.layout.GridPane;


import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class LedgerController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label ledgerTypeLabel;

    // 左侧导航栏
    @FXML private Button BackButton;
    @FXML private ImageView image_Back_to_main;
    @FXML private Label image_Back_to_main_text;

    // 主要财务信息
    @FXML private Label expenseLabel;
    @FXML private Label incomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label budgetLabel;
    @FXML private Label remainLabel;
    @FXML private Label spentLabel;
    @FXML private Label pendingLabel;
    @FXML private Label claimedLabel;
    @FXML private Label reimburLabel;
    @FXML private Label netAssetsLabel;
    @FXML private Label debtsLabel;
    @FXML private Label totalsLabel;

    // 图表
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private PieChart expenseCategoriesChart;

    // 按钮
    @FXML private Button expenseButton;
    @FXML private Button incomeButton;
    @FXML private Button balanceButton;
    @FXML private Button expenseCategoryButton;
    @FXML private Button incomeCategoryButton;

    // 建议和日历
    @FXML private VBox adviceBox;
    @FXML
    private VBox calendarWidget;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Label monthTitle;


    @FXML
    private GridPane calendarGrid;

    private YearMonth currentMonth = YearMonth.now();
    private Map<LocalDate, String> dateMarkers = new HashMap<>();
    private Map<LocalDate, List<Transaction>> dateTransactions = new HashMap<>();
    private LocalDate selectedDate = null;


    @FXML
    private Label selectedDateLabel;

    @FXML
    private VBox expenseItemsContainer;



    @FXML
    private FinanceData financeData = new FinanceData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化数据
        updateDashboard();



        List<Transaction> march6Transactions = new ArrayList<>();
        march6Transactions.add(new Transaction("Travel", -200.0));
        march6Transactions.add(new Transaction("Wage", 160.0));

        List<Transaction> march8Transactions = new ArrayList<>();
        march8Transactions.add(new Transaction("Other", -2.0));

        dateTransactions.put(LocalDate.of(2025, 3, 6), march6Transactions);
        dateTransactions.put(LocalDate.of(2025, 3, 8), march8Transactions);

        dateMarkers.put(LocalDate.of(2025, 3, 6), "-200\n+160");
        dateMarkers.put(LocalDate.of(2025, 3, 8), "+0");
        updateCalendar();
        LocalDate today = LocalDate.now();
        showDateDetails(today);

        // 初始化图表
        initializeCharts();
        initializePictures();

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
        series.getData().add(new XYChart.Data<>("Jan", 200));
        series.getData().add(new XYChart.Data<>("Feb", 150));
        series.getData().add(new XYChart.Data<>("Mar", 300));
        series.getData().add(new XYChart.Data<>("Apr", 250));
        series.getData().add(new XYChart.Data<>("May", 350));
        series.getData().add(new XYChart.Data<>("Jun", 280));
        series.getData().add(new XYChart.Data<>("Jul", 320));
        series.getData().add(new XYChart.Data<>("Aug", 220));
        series.getData().add(new XYChart.Data<>("Sep", 290));
        series.getData().add(new XYChart.Data<>("Oct", 310));
        series.getData().add(new XYChart.Data<>("Nov", 270));
        series.getData().add(new XYChart.Data<>("Dec", 330));

        lineChart.getData().add(series);

        // 初始化饼图
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

        loadAiAdvice(advice);
    }

    @FXML
    private void back_to_main() {
        System.out.println("切换页面");
        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
    }

    @FXML
    public void loadAiAdvice(List<String> adviceList) {
        Platform.runLater(() -> {
            adviceBox.getChildren().remove(1, adviceBox.getChildren().size());
            int i = 1;
            for (String advice : adviceList) {
                Label label = new Label(i + ". " + advice);
                label.getStyleClass().remove("label");  // 移除默认的 label 样式类
                label.getStyleClass().add("text-advice");  // 添加自定义的样式类
                adviceBox.getChildren().add(label);

                // 打印控件的相关信息
                System.out.println("Label added: " + label.getText());
                System.out.println("Label style class: " + label.getStyleClass());
                i++;
            }
        });
    }

    List<String> advice = Arrays.asList(
            "Try to reduce dining out expenses.",
            "Consider allocating more to savings.",
            "Set a weekly budget goal."
    );



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
                for (Transaction transaction : dateTransactions.get(date)) {
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
            for (Transaction transaction : dateTransactions.get(date)) {
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
                imageView.setImage(new Image(transaction.getAmount() > 0 ? "/images/income.png" : "/images/expense.png"));

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

    // 交易记录类
    private static class Transaction {
        private String description;
        private double amount;

        public Transaction(String description, double amount) {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public double getAmount() {
            return amount;
        }
    }


}


