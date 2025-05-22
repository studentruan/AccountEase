package com.myapp.controller;

import com.myapp.util.I18nUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class GeneralSettingsController {
    @FXML
    private TextField billNameField;

    @FXML
    private TextArea billDescriptionArea;

    @FXML
    private void initialize() {
        // 设置账单名称输入限制（最多50字符）
        billNameField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 50 ? change : null
        ));

        // 设置账单描述输入限制（最多500字符）
        billDescriptionArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 500 ? change : null
        ));

        // 可选：从配置加载已有值
        // loadSavedSettings();
    }

    @FXML
    private void saveGeneralSettings() {
        String billName = billNameField.getText().trim();
        String billDescription = billDescriptionArea.getText().trim();

        // 验证输入
        if (billName.isEmpty()) {
            showAlert(
                    I18nUtil.get("alert.error"),
                    I18nUtil.get("bill.name.empty")
            );
            billNameField.requestFocus();
            return;
        }

        if (billDescription.length() > 500) {
            showAlert(
                    I18nUtil.get("alert.error"),
                    I18nUtil.get("bill.description.long")
            );
            billDescriptionArea.requestFocus();
            return;
        }

        // 这里添加实际保存逻辑
        System.out.println("保存的账单设置:");
        System.out.println("名称: " + billName);
        System.out.println("描述: " + billDescription);

        showAlert(
                I18nUtil.get("alert.success"),
                I18nUtil.get("bill.save.success")
        );
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}