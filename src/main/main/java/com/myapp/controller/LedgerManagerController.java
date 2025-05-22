package com.myapp.controller;

import com.myapp.model.Ledger;
import com.myapp.model.LedgerManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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


    private LedgerManager ledgerManager;

    @FXML
    public void initialize() {
        ledgerManager = new LedgerManager();
        renderLedgerItems();
    }

    private void renderLedgerItems() {
        todayGrid.getChildren().clear();
        julyGrid.getChildren().clear();

        List<Ledger> ledgers = ledgerManager.getAllLedgers();
        ledgers.forEach(ledger -> {
            Button ledgerButton = new Button(ledger.getName());
            File imageFile = ledger.getCoverImage();  // coverImage 已经是 File 类型

            if (imageFile != null && imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(175);  // 设置图片宽度
                imageView.setFitHeight(250); // 设置图片高度
                ledgerButton.setGraphic(imageView);  // 设置按钮的图片
            }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewLedgerDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("新建账本");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            NewLedgerDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                Ledger newLedger = controller.getLedger();
                ledgerManager.addLedger(newLedger);
                renderLedgerItems();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("创建账本失败", e.getMessage());
        }
    }

    @FXML
    private void handleSortByTime() {
        ledgerManager.sortByCreationDateDescending();
        renderLedgerItems();
    }

    @FXML
    private void handleSortByCategory() {
        ledgerManager.sortByCategory();
        renderLedgerItems();
    }

    private void openLedger(Ledger ledger) {
        try {
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
            showAlert("打开账本失败", e.getMessage());
        }
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
