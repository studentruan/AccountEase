package com.myapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AppearanceSettingsController {
    @FXML private ComboBox<String> themeModeCombo;
    @FXML private ComboBox<String> numberFontCombo;
    @FXML private ComboBox<String> languageFontCombo;

    @FXML
    private void initialize() {
        // 加载已保存的设置
        loadSavedSettings();

        // 设置下拉框默认选项
        themeModeCombo.getSelectionModel().selectFirst();
        numberFontCombo.getSelectionModel().selectFirst();
        languageFontCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void saveAppearanceSettings() {
        // 获取选中的设置
        String themeMode = themeModeCombo.getValue();
        String numberFont = numberFontCombo.getValue();
        String languageFont = languageFontCombo.getValue();

        // 这里添加实际保存逻辑
        System.out.println("保存的外观设置:");
        System.out.println("Theme mode: " + themeMode);
        System.out.println("Number Font: " + numberFont);
        System.out.println("Language font: " + languageFont);

        // 应用设置（示例）
        applyAppearanceSettings(themeMode, numberFont, languageFont);

        showAlert("Success", "Appearance settings applied");
    }

    private void loadSavedSettings() {
        // 示例：从配置加载已有设置
        /*
        themeModeCombo.getSelectionModel().select(ConfigManager.getThemeMode());
        numberFontCombo.getSelectionModel().select(ConfigManager.getNumberFont());
        languageFontCombo.getSelectionModel().select(ConfigManager.getLanguageFont());
        */
    }

    private void applyAppearanceSettings(String themeMode, String numberFont, String languageFont) {
        // 这里实现实际的应用设置逻辑
        // 例如：更新全局样式表、字体设置等
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}