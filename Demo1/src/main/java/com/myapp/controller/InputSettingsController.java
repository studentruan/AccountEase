package com.myapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.prefs.Preferences;

public class InputSettingsController {

    @FXML
    private TextField filePathField;

    // 文件选择逻辑
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择账单文件");

        // 设置文件过滤器
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter(
                "电子表格文件 (*.xlsx, *.csv)", "*.xlsx", "*.csv");
        fileChooser.getExtensionFilters().add(excelFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    // 保存路径逻辑
    @FXML
    private void saveFilePath() {
        String path = filePathField.getText();
        if (!path.isEmpty()) {
            // 这里添加保存到配置文件或数据库的逻辑
            System.out.println("保存路径: " + path);
            // 示例：使用Preferences存储
            Preferences.userNodeForPackage(this.getClass())
                    .put("BILL_FILE_PATH", path);
        }
    }
}