package com.myapp.controller;

import com.myapp.util.I18nUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ExportSettingsController extends BaseController {

    @FXML private TextField exportPathField;
    @FXML private ComboBox<String> exportFormatComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // 初始化导出格式选项（使用国际化文本）
        exportFormatComboBox.getItems().setAll(
                I18nUtil.get("export.format.csv"),
                I18nUtil.get("export.format.excel"),
                I18nUtil.get("export.format.json")
        );

        // 加载历史记录
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        exportPathField.setText(prefs.get("EXPORT_PATH", ""));

        // 设置默认选择的格式
        String savedFormat = prefs.get("EXPORT_FORMAT", "CSV");
        exportFormatComboBox.setValue(getLocalizedFormatName(savedFormat));
    }

    @Override
    protected void updateLocalizedText() {
        // 更新ComboBox中的选项文本
        String selectedValue = exportFormatComboBox.getValue();
        exportFormatComboBox.getItems().setAll(
                I18nUtil.get("export.format.csv"),
                I18nUtil.get("export.format.excel"),
                I18nUtil.get("export.format.json")
        );
        exportFormatComboBox.setValue(selectedValue);
    }

    @FXML
    private void handleBrowseExportPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18nUtil.get("export.filechooser.title"));

        // 获取实际格式值（非显示文本）
        String format = getFormatValue(exportFormatComboBox.getValue());

        // 设置默认文件名
        fileChooser.setInitialFileName("bill_export." + format.toLowerCase());

        // 设置文件过滤器
        FileChooser.ExtensionFilter filter = switch (format) {
            case "CSV" -> new FileChooser.ExtensionFilter(
                    I18nUtil.get("export.filter.csv"), "*.csv");
            case "Excel" -> new FileChooser.ExtensionFilter(
                    I18nUtil.get("export.filter.excel"), "*.xlsx");
            case "JSON" -> new FileChooser.ExtensionFilter(
                    I18nUtil.get("export.filter.json"), "*.json");
            default -> null;
        };

        if (filter != null) {
            fileChooser.getExtensionFilters().add(filter);
        }

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            exportPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleExportData() {
        String path = exportPathField.getText();
        String format = getFormatValue(exportFormatComboBox.getValue());

        if (path == null || path.isEmpty()) {
            showAlert(
                    I18nUtil.get("alert.error"),
                    I18nUtil.get("export.path.empty")
            );
            return;
        }

        // 保存配置
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.put("EXPORT_PATH", path);
        prefs.put("EXPORT_FORMAT", format);

        // 实际导出逻辑
        try {
            // 这里添加实际的导出代码
            System.out.println("Exporting data to: " + path + " Format: " + format);

            showAlert(
                    I18nUtil.get("alert.success"),
                    I18nUtil.get("export.success")
                    //I18nUtil.get("export.success", path)
            );
        } catch (Exception e) {
            showAlert(
                    I18nUtil.get("alert.error"),
                    I18nUtil.get("export.error") + ": " + e.getMessage()
            );
        }
    }

    private String getFormatValue(String displayText) {
        if (displayText.equals(I18nUtil.get("export.format.csv"))) return "CSV";
        if (displayText.equals(I18nUtil.get("export.format.excel"))) return "Excel";
        if (displayText.equals(I18nUtil.get("export.format.json"))) return "JSON";
        return "CSV"; // 默认值
    }

    private String getLocalizedFormatName(String format) {
        return switch (format) {
            case "CSV" -> I18nUtil.get("export.format.csv");
            case "Excel" -> I18nUtil.get("export.format.excel");
            case "JSON" -> I18nUtil.get("export.format.json");
            default -> I18nUtil.get("export.format.csv");
        };
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


//public class ExportSettingsController {
//
//    @FXML private TextField exportPathField;
//    @FXML private ComboBox<String> exportFormatComboBox;
//
//    private static final String[] EXPORT_FORMATS = {"CSV", "Excel", "JSON"};
//
//    @FXML
//    public void initialize() {
//        // 初始化导出格式选项
//        exportFormatComboBox.getItems().addAll(EXPORT_FORMATS);
//        exportFormatComboBox.getSelectionModel().selectFirst();
//
//        // 加载历史记录
//        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
//        exportPathField.setText(prefs.get("EXPORT_PATH", ""));
//        exportFormatComboBox.setValue(prefs.get("EXPORT_FORMAT", "CSV"));
//    }
//
//    @FXML
//    private void handleBrowseExportPath() {
//        // 文件保存对话框
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("选择导出位置");
//
//        // 根据选择的格式设置默认文件名
//        String format = exportFormatComboBox.getValue();
//        fileChooser.setInitialFileName("bill_export." + format.toLowerCase());
//
//        // 设置文件过滤器
//        switch (format) {
//            case "CSV":
//                fileChooser.getExtensionFilters().add(
//                        new FileChooser.ExtensionFilter("CSV文件 (*.csv)", "*.csv"));
//                break;
//            case "Excel":
//                fileChooser.getExtensionFilters().add(
//                        new FileChooser.ExtensionFilter("Excel文件 (*.xlsx)", "*.xlsx"));
//                break;
//            case "JSON":
//                fileChooser.getExtensionFilters().add(
//                        new FileChooser.ExtensionFilter("JSON文件 (*.json)", "*.json"));
//                break;
//        }
//
//        File selectedFile = fileChooser.showSaveDialog(null);
//        if (selectedFile != null) {
//            exportPathField.setText(selectedFile.getAbsolutePath());
//        }
//    }
//
//    @FXML
//    private void handleExportData() {
//        String path = exportPathField.getText();
//        String format = exportFormatComboBox.getValue();
//
//        if (path.isEmpty()) {
//            showAlert("错误", "请先选择导出路径");
//            return;
//        }
//
//        // 保存配置
//        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
//        prefs.put("EXPORT_PATH", path);
//        prefs.put("EXPORT_FORMAT", format);
//
//        // 实际导出逻辑（示例）
//        System.out.println("导出数据到: " + path + " 格式: " + format);
//        showAlert("成功", "数据已导出到:\n" + path);
//    }
//
//    private void showAlert(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}
