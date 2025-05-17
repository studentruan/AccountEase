package com.myapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import org.controlsfx.control.ToggleSwitch;

public class FunctionSettingsController {

    @FXML private ToggleSwitch expenseSwitch;
    @FXML private ToggleSwitch budgetSwitch;
    @FXML private ToggleSwitch pendingSwitch;
    @FXML private ToggleSwitch assertsSwitch;
    @FXML private ToggleSwitch bargraphSwitch;
    @FXML private ToggleSwitch linegraphSwitch;
    @FXML private ToggleSwitch piegraphSwitch;
    // 其他开关定义...

    @FXML
    public void initialize() {
        setupSwitchListeners();
    }

    private void setupSwitchListeners() {
        expenseSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("expenseSwitch: " + newVal);
        });

        budgetSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("budgetSwitch: " + newVal);
        });

        pendingSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("pendingSwitch: " + newVal);
        });

        assertsSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("assertsSwitch: " + newVal);
        });

        bargraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("bargraphSwitch: " + newVal);
        });

        linegraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("linegraphSwitch: " + newVal);
        });

        piegraphSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("piegraphSwitch: " + newVal);
        });
        // 其他开关监听...
    }

}