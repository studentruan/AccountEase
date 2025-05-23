package com.myapp.controller;

import com.google.common.eventbus.Subscribe;
import com.myapp.util.ConfigUtil;
import com.myapp.util.I18nUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

//public class FunctionSettingsController {
//
//    @FXML private ToggleSwitch expenseSwitch;
//    @FXML private ToggleSwitch budgetSwitch;
//    @FXML private ToggleSwitch pendingSwitch;
//    @FXML private ToggleSwitch assertsSwitch;
//    @FXML private ToggleSwitch bargraphSwitch;
//    @FXML private ToggleSwitch linegraphSwitch;
//    @FXML private ToggleSwitch piegraphSwitch;
//    // 其他开关定义...
//
//    @FXML
//    public void initialize() {
//        setupSwitchListeners();
//    }
//
//    private void setupSwitchListeners() {
//        expenseSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("expenseSwitch: " + newVal);
//        });
//
//        budgetSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("budgetSwitch: " + newVal);
//        });
//
//        pendingSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("pendingSwitch: " + newVal);
//        });
//
//        assertsSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("assertsSwitch: " + newVal);
//        });
//
//        bargraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("bargraphSwitch: " + newVal);
//        });
//
//        linegraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("linegraphSwitch: " + newVal);
//        });
//
//        piegraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("piegraphSwitch: " + newVal);
//        });
//        // 其他开关监听...
//    }
//}

public class FunctionSettingsController extends BaseController {

    // 开关控件
    @FXML private ToggleSwitch expenseSwitch;
    @FXML private ToggleSwitch budgetSwitch;
    @FXML private ToggleSwitch pendingSwitch;
    @FXML private ToggleSwitch assertsSwitch;
    @FXML private ToggleSwitch bargraphSwitch;
    @FXML private ToggleSwitch linegraphSwitch;
    @FXML private ToggleSwitch piegraphSwitch;

    // 存储修改状态
    private final Map<String, Boolean> pendingChanges = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        loadInitialStates();
        setupSwitchListeners();
    }

    private void loadInitialStates() {
        // 从配置加载初始状态（使用默认值true）
        expenseSwitch.setSelected(ConfigUtil.getBoolean("expense", true));
        budgetSwitch.setSelected(ConfigUtil.getBoolean("budget", true));
        pendingSwitch.setSelected(ConfigUtil.getBoolean("pending", true));
        assertsSwitch.setSelected(ConfigUtil.getBoolean("asserts", true));
        bargraphSwitch.setSelected(ConfigUtil.getBoolean("bargraph", true));
        linegraphSwitch.setSelected(ConfigUtil.getBoolean("linegraph", true));
        piegraphSwitch.setSelected(ConfigUtil.getBoolean("piegraph", true));
    }

    private void setupSwitchListeners() {
        // 为每个开关添加监听器
        addSwitchListener(expenseSwitch, "expense");
        addSwitchListener(budgetSwitch, "budget");
        addSwitchListener(pendingSwitch, "pending");
        addSwitchListener(assertsSwitch, "asserts");
        addSwitchListener(bargraphSwitch, "bargraph");
        addSwitchListener(linegraphSwitch, "linegraph");
        addSwitchListener(piegraphSwitch, "piegraph");
    }

    private void addSwitchListener(ToggleSwitch toggleSwitch, String configKey) {
        toggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            pendingChanges.put(configKey, newVal);
        });
    }

    @Override
    protected void updateLocalizedText() {
        // 可以添加其他需要国际化的控件文本
    }

    @FXML
    private void saveFunctionSettings() {
        try {
            // 1. 保存所有修改
            pendingChanges.forEach(ConfigUtil::setBoolean);

            // 2. 应用设置
            applySettings();

            // 3. 显示成功提示
            showAlert(
                    I18nUtil.get("alert.success"),
                    I18nUtil.get("settings.saved")
            );

        } catch (Exception e) {
            showAlert(
                    I18nUtil.get("alert.error"),
                    I18nUtil.get("settings.saveError") + ": " + e.getMessage()
            );
        }
    }

    private void applySettings() {
        // 应用每个修改的设置
        if (pendingChanges.containsKey("bargraph")) {
            boolean show = pendingChanges.get("bargraph");
            // MainController.getInstance().setChartVisibility("bar", show);
        }
        // 其他设置应用...
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}