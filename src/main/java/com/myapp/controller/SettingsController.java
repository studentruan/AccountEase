package com.myapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class SettingsController {
    @FXML
    private StackPane settingsContentPane;

    @FXML
    private void handleGeneralSettings(ActionEvent event) {
        try {
            // 加载通用设置界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/generalSettings.fxml"));
            Parent content = loader.load();
            settingsContentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLanguageSettings(ActionEvent event) {

        String fxmlPath = "/fxml/languageSettings.fxml";
        System.out.println("加载路径: " + fxmlPath);
        try {
            // 加载语言设置界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            settingsContentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAppearanceSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appearanceSettings.fxml"));
            Parent content = loader.load();
            settingsContentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFunctionSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/functionSettings.fxml"));
            Parent content = loader.load();
            settingsContentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInputSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inputSettings.fxml"));
            Parent inputSettings = loader.load();
            settingsContentPane.getChildren().setAll(inputSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/exportSettings.fxml"));
            Parent exportSettings = loader.load();
            settingsContentPane.getChildren().setAll(exportSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCurrencySettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/currencySettings.fxml"));
            Parent currencySettings = loader.load();
            settingsContentPane.getChildren().setAll(currencySettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackupSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/backupSettings.fxml"));
            Parent backupSettings = loader.load();
            settingsContentPane.getChildren().setAll(backupSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 其他分类的处理方法...
    private void loadSettingsContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            settingsContentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back_to_main() {
        System.out.println("切换页面");
        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
    }
}

