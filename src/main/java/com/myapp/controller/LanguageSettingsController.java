package com.myapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class LanguageSettingsController {
    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private void initialize() {
        // 可以在这里设置默认选择的语言
        // 例如根据系统当前设置选择
        languageComboBox.getSelectionModel().select(0); // 默认选择第一个
    }

    @FXML
    private void saveLanguageSettings(ActionEvent event) {
        String selectedLanguage = languageComboBox.getValue();

        // 这里添加实际保存语言设置的逻辑
        System.out.println("Selected language: " + selectedLanguage);

        // 显示保存成功的提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings Saved");
        alert.setHeaderText(null);
        alert.setContentText("Language preference saved: " + selectedLanguage);
        alert.showAndWait();
    }
}