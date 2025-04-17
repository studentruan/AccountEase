package com.myapp.controller;

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
            showAlert("错误", "账单名称不能为空");
            billNameField.requestFocus();
            return;
        }

        if (billDescription.length() > 500) {
            showAlert("错误", "账单描述不能超过500字");
            billDescriptionArea.requestFocus();
            return;
        }

        // 这里添加实际保存逻辑
        System.out.println("保存的账单设置:");
        System.out.println("名称: " + billName);
        System.out.println("描述: " + billDescription);

        showAlert("成功", "账单设置已保存");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 可选：从配置文件加载已有设置
    private void loadSavedSettings() {
        // 示例代码：
        // billNameField.setText(ConfigManager.getBillName());
        // billDescriptionArea.setText(ConfigManager.getBillDescription());
    }
}