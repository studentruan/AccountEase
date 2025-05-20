package com.myapp.controller;

import Backend.Ledger;
import Backend.LedgerManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class LedgerManagerDialogController {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private Label imagePathLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> categoryBox;

    private File coverImage;
    private Stage dialogStage;
    private boolean confirmed = false;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        categoryBox.getItems().addAll("生活", "工作", "旅行", "其他");
        categoryBox.setValue("生活");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        coverImage = chooser.showOpenDialog(dialogStage);
        if (coverImage != null) {
            imagePathLabel.setText(coverImage.getName());
        }
    }

    @FXML
    private void handleConfirm() {
        if (validateInput()) {
            try {
                Ledger newLedger = createLedger();
                saveLedger(newLedger);
                confirmed = true;
                dialogStage.close();
            } catch (IOException e) {
                showAlert("保存失败", e.getMessage());
            }
        }
    }

    private Ledger createLedger() {
        Ledger ledger = new Ledger();
        ledger.setId(UUID.randomUUID().toString());
        ledger.setName(nameField.getText());
        ledger.setDescription(descriptionArea.getText());
        ledger.setCoverImage(coverImage);
        return ledger;
    }

    private void saveLedger(Ledger ledger) throws IOException {
        // 保存到二级存储
        ledger.save();
        // 更新一级存储
        new LedgerManager().addLedger(ledger.getId());
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("输入错误", "账本名称不能为空");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.WARNING, content).show();
    }
}