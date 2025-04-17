package com.myapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.*;
import java.util.prefs.Preferences;

public class CurrencySettingsController {

    @FXML private ComboBox<String> defaultCurrencyCombo;
    @FXML private VBox exchangeRateContainer;

    private final Map<String, Map<String, Double>> exchangeRates = new HashMap<>();

    @FXML
    public void initialize() {
        // 初始化货币数据
        initializeExchangeRates();

        // 设置货币选择下拉框
        defaultCurrencyCombo.getItems().addAll("美元", "人民币", "英镑", "欧元");

        // 加载保存的默认货币
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        String savedCurrency = prefs.get("DEFAULT_CURRENCY", "美元");
        defaultCurrencyCombo.getSelectionModel().select(savedCurrency);

        // 监听货币选择变化
        defaultCurrencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateExchangeRateDisplay(newVal);
        });

        // 初始显示
        updateExchangeRateDisplay(savedCurrency);
    }

    private void initializeExchangeRates() {
        // 美元基准汇率
        Map<String, Double> usdRates = new HashMap<>();
        usdRates.put("美元", 1.0);
        usdRates.put("人民币", 7.23);
        usdRates.put("英镑", 0.79);
        usdRates.put("欧元", 0.92);
        exchangeRates.put("美元", usdRates);

        // 人民币汇率
        Map<String, Double> cnyRates = new HashMap<>();
        cnyRates.put("美元", 0.14);
        cnyRates.put("人民币", 1.0);
        cnyRates.put("英镑", 0.11);
        cnyRates.put("欧元", 0.13);
        exchangeRates.put("人民币", cnyRates);

        // 英镑汇率
        Map<String, Double> gbpRates = new HashMap<>();
        gbpRates.put("美元", 1.27);
        gbpRates.put("人民币", 9.18);
        gbpRates.put("英镑", 1.0);
        gbpRates.put("欧元", 1.17);
        exchangeRates.put("英镑", gbpRates);

        // 欧元汇率
        Map<String, Double> eurRates = new HashMap<>();
        eurRates.put("美元", 1.08);
        eurRates.put("人民币", 7.81);
        eurRates.put("英镑", 0.85);
        eurRates.put("欧元", 1.0);
        exchangeRates.put("欧元", eurRates);
    }

    private void updateExchangeRateDisplay(String baseCurrency) {
        exchangeRateContainer.getChildren().clear();

        Map<String, Double> rates = exchangeRates.get(baseCurrency);
        if (rates != null) {
            rates.forEach((currency, rate) -> {
                HBox rateRow = new HBox(10);
                Label currencyLabel = new Label(currency + ":");
                currencyLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 60;");

                Label rateLabel = new Label(String.format("%.2f", rate));
                rateLabel.setStyle("-fx-font-family: monospace;");

                rateRow.getChildren().addAll(currencyLabel, rateLabel);
                exchangeRateContainer.getChildren().add(rateRow);
            });
        }
    }

    // 新增保存方法
    @FXML
    private void saveCurrencySettings() {
        try {
            // 获取当前选择的默认货币
            String selectedCurrency = defaultCurrencyCombo.getValue();

            // 这里实际应该保存到配置文件或数据库
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            prefs.put("DEFAULT_CURRENCY", selectedCurrency);

            // 保存所有汇率数据（实际项目可能需要更复杂的存储方式）
            exchangeRates.forEach((baseCurrency, rates) -> {
                rates.forEach((targetCurrency, rate) -> {
                    String key = baseCurrency + "_TO_" + targetCurrency;
                    prefs.putDouble(key, rate);
                });
            });

            // 显示保存成功提示
            showAlert("保存成功", "货币设置已保存");

        } catch (Exception e) {
            showAlert("保存失败", "无法保存设置: " + e.getMessage());
        }
    }

    // 辅助方法：显示提示框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}