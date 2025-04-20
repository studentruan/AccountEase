package com.myapp.controller;

import com.myapp.model.Ledger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class NewLedgerDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML private ImageView image_arrow;

    @FXML
    private ComboBox<String> storageLocationComboBox;
    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private ComboBox<String> categoryComboBox;
    private Stage dialogStage;
    private boolean confirmed = false;
    private File coverImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        if (creationDatePicker != null) {
            creationDatePicker.setValue(LocalDate.now());
        } else {
            System.out.println("creationDatePicker is null");
        }
        categoryComboBox.getItems().addAll("生活", "工作", "旅行", "学习", "其他");
        categoryComboBox.setValue("其他");


        // 初始化存储位置下拉框
        storageLocationComboBox.getItems().addAll(
                "Local Storage",
                "Cloud Storage",
                "External Drive"
        );
        storageLocationComboBox.setValue("Local Storage");

        Image image_Back = new Image(getClass().getResource("/images/arrow.png").toExternalForm());
        image_arrow.setImage(image_Back);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setResizable(false);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择账本封面");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        coverImage = fileChooser.showOpenDialog(dialogStage);

        if (coverImage != null) {
            // 这里可以更新UI显示选中的图片名称
        }
    }

    @FXML
    private void handleConfirm() {
        if (validateInput()) {
            confirmed = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Ledger getLedger() {
        return new Ledger(
                nameField.getText(),
                descriptionArea.getText(),
                coverImage,
                creationDatePicker.getValue(),           // 获取用户选择的日期
                categoryComboBox.getValue()              // 获取用户选择的类别

        );
    }


    private boolean validateInput() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("请输入账本名称");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}