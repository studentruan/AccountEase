package com.myapp.controller;

import com.myapp.model.FinanceData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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
    @FXML private Label searchLabel;
    @FXML private DatePicker datePicker;
    @FXML private Label calendarLabel;
    @FXML private Label dailyExpenseLabel;
    @FXML private Label dailyIncomeLabel;

    @FXML
    private FinanceData financeData = new FinanceData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化数据
        updateDashboard();

        // 设置日历监听器
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDashboard();
        });

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






//    private void loadPage(String fxmlPath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//            Parent page = loader.load();
//            mainPane.setCenter(page);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void clearIconEffects() {
//        ledgerImage.setEffect(null);
//        deepAccountImage.setEffect(null);
//        settingsImage.setEffect(null);
//    }
//
//    private void applyBlueEffect(ImageView imageView) {
//        ColorAdjust colorAdjust = new ColorAdjust();
//        colorAdjust.setHue(-0.5); // 调整 hue 值使图标变蓝
//        imageView.setEffect(colorAdjust);
//    }
//
//    private void setActiveIcon(ImageView activeIcon) {
//        // 先清除所有图标的激活效果
//        clearIconEffects();
//
//        // 方式1：通过 CSS 类（需要在 style.css 中定义 .active-icon）
//        activeIcon.getStyleClass().add("active-icon");
//        applyBlueEffect(activeIcon);
//    }



}


