package com.myapp.controller;

import com.myapp.util.I18nUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

//public class LanguageSettingsController {
//    @FXML
//    private ComboBox<String> languageComboBox;
//
//    @FXML
//    private void initialize() {
//        // 可以在这里设置默认选择的语言
//        // 例如根据系统当前设置选择
//        languageComboBox.getSelectionModel().select(0); // 默认选择第一个
//    }
//
//    @FXML
//    private void saveLanguageSettings(ActionEvent event) {
//        String selectedLanguage = languageComboBox.getValue();
//
//        // 这里添加实际保存语言设置的逻辑
//        System.out.println("Selected language: " + selectedLanguage);
//
//        // 显示保存成功的提示
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Settings Saved");
//        alert.setHeaderText(null);
//        alert.setContentText("Language preference saved: " + selectedLanguage);
//        alert.showAndWait();
//    }
//
//}

public class LanguageSettingsController extends BaseController {
    @FXML private ComboBox<String> languageComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        //languageComboBox.getItems().addAll("English", "简体中文");
        languageComboBox.setValue(getCurrentLanguageName());

    }

    @FXML
    private void saveLanguageSettings() {
        String selected = languageComboBox.getValue();
        Locale newLocale = selected.equals("简体中文") ?
                Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;

        I18nUtil.setLocale(newLocale);
        showSaveSuccess();
    }

    private String getCurrentLanguageName() {
        return I18nUtil.getCurrentLocale().equals(Locale.ENGLISH) ?
                "English" : "简体中文";
    }

    private void showSaveSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(I18nUtil.get("alert.success"));
        alert.setHeaderText(I18nUtil.get("settings.saved"));
        alert.show();
    }

    @Override
    protected void updateLocalizedText() {
        // 更新界面动态文本
        languageComboBox.setPromptText(I18nUtil.get("language.select"));
    }
}