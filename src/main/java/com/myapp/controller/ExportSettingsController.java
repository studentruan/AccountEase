package com.myapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.prefs.Preferences;

public class ExportSettingsController {

    @FXML private TextField exportPathField;
    @FXML private ComboBox<String> exportFormatComboBox;

    private static final String[] EXPORT_FORMATS = {"CSV", "Excel", "JSON"};

    @FXML
    public void initialize() {
        // 初始化导出格式选项
        exportFormatComboBox.getItems().addAll(EXPORT_FORMATS);
        exportFormatComboBox.getSelectionModel().selectFirst();

        // 加载历史记录
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        exportPathField.setText(prefs.get("EXPORT_PATH", ""));
        exportFormatComboBox.setValue(prefs.get("EXPORT_FORMAT", "CSV"));
    }

    @FXML
    private void handleBrowseExportPath() {
        // 文件保存对话框
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择导出位置");

        // 根据选择的格式设置默认文件名
        String format = exportFormatComboBox.getValue();
        fileChooser.setInitialFileName("bill_export." + format.toLowerCase());

        // 设置文件过滤器
        switch (format) {
            case "CSV":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("CSV文件 (*.csv)", "*.csv"));
                break;
            case "Excel":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Excel文件 (*.xlsx)", "*.xlsx"));
                break;
            case "JSON":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON文件 (*.json)", "*.json"));
                break;
        }

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            exportPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleExportData() {
        String path = exportPathField.getText();
        String format = exportFormatComboBox.getValue();

        if (path.isEmpty()) {
            showAlert("错误", "请先选择导出路径");
            return;
        }

        // 保存配置
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.put("EXPORT_PATH", path);
        prefs.put("EXPORT_FORMAT", format);

        // 实际导出逻辑（示例）
        System.out.println("导出数据到: " + path + " 格式: " + format);
        showAlert("成功", "数据已导出到:\n" + path);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}