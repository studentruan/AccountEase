package com.myapp.controller;

import com.myapp.util.I18nUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

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