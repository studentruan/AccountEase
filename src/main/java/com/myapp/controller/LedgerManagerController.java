package com.myapp.controller;

import Backend.GlobalContext;
import Backend.Ledger;
import Backend.LedgerManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class LedgerManagerController {

    @FXML
    private GridPane todayGrid;      // 当日创建账本网格
    @FXML
    private GridPane historyGrid;    // 历史账本网格

    private final LedgerManager ledgerManager = new LedgerManager();

    // 初始化界面
    @FXML
    public void initialize() {
        refreshLedgerDisplay();
    }

    // 刷新账本显示
    private void refreshLedgerDisplay() {
        clearGrids();
        ledgerManager.loadLedgerIdsFromFile();
        List<String> ledgerIds = ledgerManager.getAllLedgerIds();

        ledgerIds.forEach(id -> {
            try {
                Ledger ledger = new Ledger();
                ledger.load(id);

                addLedgerButton(ledger);
            } catch (IOException e) {
                showErrorAlert("账本加载失败", "无法加载账本ID: " + id);
            }
        });
    }

    // 清空网格内容
    private void clearGrids() {
        todayGrid.getChildren().clear();
        historyGrid.getChildren().clear();
        todayGrid.getRowConstraints().clear();
        historyGrid.getRowConstraints().clear();
    }

    // 添加账本按钮到对应网格
    private void addLedgerButton(Ledger ledger) {
        Button button = createLedgerButton(ledger);
        GridPane targetGrid = isToday(ledger.getCreationTime().toLocalDate()) ? todayGrid : historyGrid;
        targetGrid.add(button, 0, targetGrid.getRowCount());
    }

    // 创建带封面的账本按钮
    private Button createLedgerButton(Ledger ledger) {
        Button button = new Button(ledger.getName());
        button.getStyleClass().add("ledger-button");

        // 设置封面图片
        File coverImage = ledger.getCoverImage();
        if (coverImage != null && coverImage.exists()) {
            ImageView imageView = new ImageView(new Image(coverImage.toURI().toString()));
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            button.setGraphic(imageView);
        }

        // 点击事件处理
        button.setOnAction(event -> openLedgerDetail(ledger));
        return button;
    }

    // 打开新建账本对话框
    @FXML
    private void handleNewLedger() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLedgerDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("新建账本");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            NewLedgerDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                refreshLedgerDisplay(); // 刷新显示
            }

        } catch (IOException e) {
            showErrorAlert("界面加载失败", e.getMessage());
        }
    }

    // 按创建时间排序
    @FXML
    private void handleSortByTime() {
        ledgerManager.getAllLedgerIds().sort(Comparator.reverseOrder());
        refreshLedgerDisplay();
    }

    // 按分类排序（需实现分类逻辑）
    @FXML
    private void handleSortByCategory() {
        // 需要扩展Ledger类添加分类属性
        showInfoAlert("功能提示", "分类排序功能正在开发中");
    }

    // 打开账本详情界面
    public void openLedgerDetail(Ledger ledger) {
        try {

            GlobalContext.getInstance().setCurrentLedger(ledger);
            // 加载 MainController 和主界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            // 获取 MainController
            MainController mainController = loader.getController();

            // 加载 Ledger 页面
            mainController.loadPage("Ledger.fxml");

            // 获取 LedgerController 并传递账本
            Object controller = mainController.getCurrentController();
            if (controller instanceof LedgerController ledgerController) {
                ledgerController.loadLedger(ledger);
            }

            Stage stage = new Stage();
            stage.setTitle(ledger.getName());  // 设置账本名称为窗口标题

            // 为新窗口应用相同的样式
            Scene scene = new Scene(root, 1490, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();  // 显示新窗口



        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("打开账本失败", e.getMessage());
        }
    }

    // 判断是否为当天创建
    private boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    // 错误提示对话框
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 信息提示对话框
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}




