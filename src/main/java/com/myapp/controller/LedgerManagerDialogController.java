package com.myapp.controller;

import com.myapp.model.Ledger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LedgerManagerDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label imagePathLabel;

    private Stage dialogStage;
    private boolean confirmed = false;
    private File coverImage;
    private Ledger currentLedger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化逻辑
    }

    public void setLedger(Ledger ledger) {
        this.currentLedger = ledger;
        nameField.setText(ledger.getName());
        descriptionArea.setText(ledger.getDescription());
        imagePathLabel.setText(ledger.getCoverImage() != null ?
                new File(ledger.getCoverImage()).getName() : "未选择图片");
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
            imagePathLabel.setText(coverImage.getName());
        }
    }

    @FXML
    private void handleConfirm() {
        if (validateInput()) {
            currentLedger.setName(nameField.getText());
            currentLedger.setDescription(descriptionArea.getText());
            if (coverImage != null) {
                currentLedger.setCoverImage(coverImage.getAbsolutePath());
            }
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