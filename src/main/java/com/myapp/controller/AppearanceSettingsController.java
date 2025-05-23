package com.myapp.controller;

import com.myapp.util.ConfigUtil;
import com.myapp.util.I18nUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AppearanceSettingsController extends BaseController{
    @FXML private ComboBox<String> themeModeCombo;
    @FXML private ComboBox<String> numberFontCombo;
    @FXML private ComboBox<String> languageFontCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initComboBoxes();
        loadSavedSettings();
    }

    private void initComboBoxes() {
        // 初始化主题下拉框（使用国际化文本）
        themeModeCombo.getItems().setAll(
                I18nUtil.get("theme.light"),
                I18nUtil.get("theme.dark")
        );

        // 初始化数字字体下拉框
        numberFontCombo.getItems().setAll(
                I18nUtil.get("font.default"),
                I18nUtil.get("font.style1"),
                I18nUtil.get("font.style2")
        );

        // 初始化语言字体下拉框
        languageFontCombo.getItems().setAll(
                I18nUtil.get("font.default"),
                I18nUtil.get("font.arial"),
                I18nUtil.get("font.yahei"),
                I18nUtil.get("font.simsun")
        );
    }

    @Override
    protected void updateLocalizedText() {
        // 保存当前选中的值
        String selectedTheme = themeModeCombo.getValue();
        String selectedNumberFont = numberFontCombo.getValue();
        String selectedLanguageFont = languageFontCombo.getValue();

        // 重新初始化下拉框内容
        initComboBoxes();

        // 恢复选中的值
        if (selectedTheme != null) themeModeCombo.setValue(selectedTheme);
        if (selectedNumberFont != null) numberFontCombo.setValue(selectedNumberFont);
        if (selectedLanguageFont != null) languageFontCombo.setValue(selectedLanguageFont);
    }

    @FXML
    private void saveAppearanceSettings() {
        // 获取实际值（非显示文本）
        String themeMode = getThemeValue(themeModeCombo.getValue());
        String numberFont = getNumberFontValue(numberFontCombo.getValue());
        String languageFont = getLanguageFontValue(languageFontCombo.getValue());

        // 保存设置
        ConfigUtil.saveThemePreference(themeMode);
        ConfigUtil.saveNumberFontPreference(numberFont);
        ConfigUtil.saveLanguageFontPreference(languageFont);

        // 应用设置
        applyAppearanceSettings(themeMode, numberFont, languageFont);

        // 显示国际化提示
        showAlert(
                I18nUtil.get("alert.success"),
                I18nUtil.get("settings.saved")
        );
    }

    private void loadSavedSettings() {
        // 加载保存的设置并选中
        String savedTheme = ConfigUtil.getThemePreference();
        themeModeCombo.setValue(
                savedTheme.equals("dark") ?
                        I18nUtil.get("theme.dark") :
                        I18nUtil.get("theme.light")
        );

        String savedNumberFont = ConfigUtil.getNumberFontPreference();
        // 类似逻辑处理其他下拉框...
    }

    // 以下方法用于将显示文本转换为实际值
    private String getThemeValue(String displayText) {
        return displayText.equals(I18nUtil.get("theme.dark")) ? "dark" : "light";
    }

    private String getNumberFontValue(String displayText) {
        if (displayText.equals(I18nUtil.get("font.style1"))) return "1";
        if (displayText.equals(I18nUtil.get("font.style2"))) return "2";
        return "default";
    }

    private String getLanguageFontValue(String displayText) {
        if (displayText.equals(I18nUtil.get("font.arial"))) return "Arial";
        if (displayText.equals(I18nUtil.get("font.yahei"))) return "Microsoft YaHei";
        if (displayText.equals(I18nUtil.get("font.simsun"))) return "SimSun";
        return "default";
    }

    private void applyAppearanceSettings(String themeMode, String numberFont, String languageFont) {
        // 实现实际的应用逻辑
        // 例如：加载CSS、设置字体等
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

//    @FXML private ComboBox<String> themeModeCombo;
//    @FXML private ComboBox<String> numberFontCombo;
//    @FXML private ComboBox<String> languageFontCombo;
//
//    @FXML
//    private void initialize() {
//        // 加载已保存的设置
//        loadSavedSettings();
//
//        // 设置下拉框默认选项
//        themeModeCombo.getSelectionModel().selectFirst();
//        numberFontCombo.getSelectionModel().selectFirst();
//        languageFontCombo.getSelectionModel().selectFirst();
//    }
//
//    @FXML
//    private void saveAppearanceSettings() {
//        // 获取选中的设置
//        String themeMode = themeModeCombo.getValue();
//        String numberFont = numberFontCombo.getValue();
//        String languageFont = languageFontCombo.getValue();
//
//        // 这里添加实际保存逻辑
//        System.out.println("保存的外观设置:");
//        System.out.println("Theme mode: " + themeMode);
//        System.out.println("Number Font: " + numberFont);
//        System.out.println("Language font: " + languageFont);
//
//        // 应用设置（示例）
//        applyAppearanceSettings(themeMode, numberFont, languageFont);
//
//        showAlert("Success", "Appearance settings applied");
//    }
//
//    private void loadSavedSettings() {
//        // 示例：从配置加载已有设置
//        /*
//        themeModeCombo.getSelectionModel().select(ConfigManager.getThemeMode());
//        numberFontCombo.getSelectionModel().select(ConfigManager.getNumberFont());
//        languageFontCombo.getSelectionModel().select(ConfigManager.getLanguageFont());
//        */
//    }
//
//    private void applyAppearanceSettings(String themeMode, String numberFont, String languageFont) {
//        // 这里实现实际的应用设置逻辑
//        // 例如：更新全局样式表、字体设置等
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
}