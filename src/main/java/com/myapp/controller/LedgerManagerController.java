package com.myapp.controller;

import com.myapp.model.Ledger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LedgerManagerController {

    @FXML
    private GridPane todayGrid;

    @FXML
    private GridPane julyGrid;

    private List<Ledger> ledgers;

    @FXML
    public void initialize() {
        // 初始化账本数据
        loadLedgers();
        // 渲染账本列表
        renderLedgerItems();
    }

    private void loadLedgers() {
        // 从数据源加载账本列表
        // 这里可以实现从文件或数据库加载
        ledgers = Ledger.loadAllLedgers();
    }

    private void renderLedgerItems() {

        // 清空现有网格
        todayGrid.getChildren().clear();
        julyGrid.getChildren().clear();

        // 根据日期分组渲染账本
        ledgers.forEach(ledger -> {
            Button ledgerButton = new Button(ledger.getName());
            System.out.println(ledger.getName());
            System.out.println(ledger.getCoverImage());
            String imagePath = ledger.getCoverImage();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                String imageUri = imageFile.toURI().toString();
                Image image = new Image(imageUri);

                // 创建ImageView并设置固定大小
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(175);  // 设置固定宽度
                imageView.setFitHeight(250); // 设置固定高度
//                imageView.setPreserveRatio(true); // 保持图片比例

                // 将ImageView设置为按钮的图标
                ledgerButton.setGraphic(imageView);
            } else {
                System.out.println("图像文件未找到！");
            }
//            ledgerButton.setGraphic(new ImageView(ledger.getCoverImage()));
            ledgerButton.setOnAction(event -> openLedger(ledger));

            // 根据日期决定放入哪个网格
            if (isToday(ledger.getCreationDate())) {
                todayGrid.add(ledgerButton, 0, todayGrid.getRowCount());
            } else {
                julyGrid.add(ledgerButton, 0, julyGrid.getRowCount());
            }
        });
    }

    @FXML
    private void handleNewLedger() {
        try {
//             加载新建账本对话框
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLedgerDialog.fxml"));

            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("新建账本");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            NewLedgerDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            // 如果用户确认创建
            if (controller.isConfirmed()) {
                System.out.println("22222");
                Ledger newLedger = controller.getLedger();
                ledgers.add(newLedger);
                renderLedgerItems();
                System.out.println("d11d1d");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("创建账本失败", e.getMessage());
        }
    }

    @FXML
    private void handleSortByTime() {
        // 按时间排序
        ledgers.sort((l1, l2) -> l2.getCreationDate().compareTo(l1.getCreationDate()));
        renderLedgerItems();
    }

    @FXML
    private void handleSortByCategory() {
        // 按类别排序
        ledgers.sort((l1, l2) -> l1.getCategory().compareTo(l2.getCategory()));
        renderLedgerItems();
    }

    private void openLedger(Ledger ledger) {
        // 打开选中的账本
        // 这里可以实现打开账本的逻辑
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isToday(java.time.LocalDate date) {
        return date.equals(java.time.LocalDate.now());
    }
}