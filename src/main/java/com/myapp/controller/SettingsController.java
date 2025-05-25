package com.myapp.controller;

import com.google.common.eventbus.Subscribe;
import com.myapp.util.I18nUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML private StackPane settingsContentPane;
    @FXML private Button backButton;
    @FXML private ImageView backImageView;
    @FXML private Label backToMainLabel;
    @FXML private Label ledgerTypeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 注册语言变更监听
        I18nUtil.register(this);
    }

    // 所有设置页面加载方法（示例）
    @FXML
    private void handleLanguageSettings() {
        loadSettingsPage("/fxml/languageSettings.fxml");
    }

    @FXML
    private void handleGeneralSettings() {
        loadSettingsPage("/fxml/generalSettings.fxml");
    }


    @FXML
    private void handleAppearanceSettings() {
        loadSettingsPage("/fxml/appearanceSettings.fxml");
    }

    @FXML
    private void handleFunctionSettings() {
        loadSettingsPage("/fxml/functionSettings.fxml");
    }

    @FXML
    private void handleInputSettings() {
        loadSettingsPage("/fxml/inputSettings.fxml");
    }

    @FXML
    private void handleExportSettings() {
        loadSettingsPage("/fxml/exportSettings.fxml");
    }

    @FXML
    private void handleCurrencySettings() {
        loadSettingsPage("/fxml/currencySettings.fxml");
    }

    @FXML
    private void handleBackupSettings() {
        loadSettingsPage("/fxml/backupSettings.fxml");
    }

    // 其他handleXXXSettings方法...

    private void loadSettingsPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath),
                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
            );

            Node content = loader.load();
            ObservableList<Node> children = settingsContentPane.getChildren();
            children.clear();
            children.add(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



//    private void loadSettingsPage(String fxmlPath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource(fxmlPath),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            settingsContentPane.getChildren().setAll(loader.load());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // 语言变更时的回调
    @Subscribe
    public void onLocaleChanged(I18nUtil.LocaleChangeEvent event) {
        // 无需额外操作，FXMLLoader会自动处理%开头的文本
    }

    @FXML
    private void back_to_main() {
        // 返回主页面逻辑
    }
}

//public class SettingsController {
//    @FXML
//    private StackPane settingsContentPane;
//
//    @FXML
//    private void handleGeneralSettings(ActionEvent event) {
//        try {
//            // 加载通用设置界面
//            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/generalSettings.fxml"));
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/generalSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent content = loader.load();
//            settingsContentPane.getChildren().setAll(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleLanguageSettings(ActionEvent event) {
//
//        String fxmlPath = "/fxml/languageSettings.fxml";
//        System.out.println("加载路径: " + fxmlPath);
//
//        try {
//            // 关键修复：加载时传入资源包
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/languageSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//
//            Parent content = loader.load();
//            settingsContentPane.getChildren().setAll(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleAppearanceSettings(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/appearanceSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent content = loader.load();
//            settingsContentPane.getChildren().setAll(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @FXML
//    private void handleFunctionSettings(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/functionSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent content = loader.load();
//            settingsContentPane.getChildren().setAll(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleInputSettings() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/inputSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent inputSettings = loader.load();
//            settingsContentPane.getChildren().setAll(inputSettings);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleExportSettings() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/exportSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent exportSettings = loader.load();
//            settingsContentPane.getChildren().setAll(exportSettings);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleCurrencySettings() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/currencySettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent currencySettings = loader.load();
//            settingsContentPane.getChildren().setAll(currencySettings);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleBackupSettings() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/backupSettings.fxml"),
//                    ResourceBundle.getBundle("lang/messages", I18nUtil.getCurrentLocale())
//            );
//            Parent backupSettings = loader.load();
//            settingsContentPane.getChildren().setAll(backupSettings);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 其他分类的处理方法...
//    private void loadSettingsContent(String fxmlPath) {
//        try {
//            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
//            settingsContentPane.getChildren().setAll(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void back_to_main() {
//        System.out.println("切换页面");
//        // 在这里执行页面切换逻辑，例如切换 Scene 或者加载新的 FXML
//    }
//}