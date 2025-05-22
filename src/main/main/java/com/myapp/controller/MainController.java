package com.myapp.controller;

import com.google.common.eventbus.Subscribe;
import com.myapp.model.Ledger;
import com.myapp.util.I18nUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

//public class MainController implements Initializable {
//
//    private static MainController instance;
//    public static MainController getInstance() {
//        return instance;
//    }
//
//    @FXML private BorderPane mainPane;
//    @FXML private AnchorPane contentArea;
//    private Object currentController; // 加上这一行，全局保存控制器
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        instance = this;
//        loadPage("ledger.fxml"); // 默认页面
//
//
//    }
//
//    public Object getCurrentController() {
//        return currentController;
//    }
//
//    public void loadPage(String fxmlFile) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
//            BorderPane page = loader.load();
//
//            mainPane.setCenter(page);
//            currentController = loader.getController(); // ⬅️ 保存当前页面控制器
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//}

public class MainController implements Initializable {

    private static MainController instance;
    public static MainController getInstance() {
        return instance;
    }

    @FXML private BorderPane mainPane;
    @FXML private AnchorPane contentArea;
    private Object currentController;

    // NEW: 添加语言切换相关组件
    @FXML private ComboBox<String> languageComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        // NEW: 注册事件总线和初始化语言选择器
        I18nUtil.register(this);
        initLanguageSelector();
        loadPage("ledger.fxml"); // 默认页面
    }

    // NEW: 初始化语言选择器方法
    private void initLanguageSelector() {
        if (languageComboBox != null) {
            languageComboBox.getItems().addAll("English", "简体中文");
            languageComboBox.setValue(I18nUtil.getCurrentLocale().equals(Locale.ENGLISH) ? "English" : "简体中文");

            // NEW: 语言切换监听
            languageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                Locale locale = newVal.equals("简体中文") ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;
                I18nUtil.setLocale(locale);
            });
        }
    }

    // NEW: 语言切换事件处理
    @Subscribe
    public void onLocaleChanged(I18nUtil.LocaleChangeEvent event) {
        Platform.runLater(() -> {
            reloadCurrentPage();
        });
    }

    // NEW: 重新加载当前页面
    private void reloadCurrentPage() {
        if (currentController != null) {
            String currentFxml = getCurrentFxmlName();
            loadPage(currentFxml);
        }
    }

//    public void loadPage(String fxmlFile) {
//        try {
//            // 确保资源包存在（防御性编程）
//            ResourceBundle resources = I18nUtil.getBundle();
//            if (resources == null) {
//                System.err.println("警告：无法加载语言资源包，使用默认设置");
//                resources = ResourceBundle.getBundle("lang/messages", Locale.ENGLISH);
//            }
//
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/" + fxmlFile),
//                    resources
//            );
//
//            // 设置字符编码（防止中文乱码）
//            loader.setCharset(StandardCharsets.UTF_8);
//
//            Parent page = loader.load();
//            mainPane.setCenter(page);
//            currentController = loader.getController();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }

//    public void loadPage(String fxmlFile) {
//        try {
//            System.out.println("======= 开始加载FXML =======");
//            URL url = getClass().getResource("/fxml/" + fxmlFile);
//            System.out.println("物理路径: " + url.toURI());
//
//            // 测试原始加载（不通过FXMLLoader）
//            System.out.println("文件内容前100字符: " +
//                    new String(Files.readAllBytes(Paths.get(url.toURI())), StandardCharsets.UTF_8).substring(0, 100));
//
//            FXMLLoader loader = new FXMLLoader(url, I18nUtil.getBundle());
//            loader.setCharset(StandardCharsets.UTF_8);
//
//            // 添加控制器工厂日志
//            loader.setControllerFactory(clazz -> {
//                System.out.println("正在实例化控制器: " + clazz.getName());
//                try {
//                    return clazz.newInstance();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            Parent root = loader.load();
//            System.out.println("FXML加载成功，根节点: " + root.getClass().getName());
//
//            mainPane.setCenter(root);
//            currentController = loader.getController();
//            System.out.println("控制器实例: " + currentController);
//
//        } catch (Exception e) {
//            System.err.println("!!!!!!!!!! 加载失败 !!!!!!!!!!");
//            e.printStackTrace();
//        }
//        // 测试资源包是否可加载
//        System.out.println("测试资源键值: " +
//                ResourceBundle.getBundle("lang/messages", Locale.CHINESE)
//                        .getString("button.save"));
//    }

    public void loadPage(String fxmlFile) {
        try {
            // 1. 创建资源包（确保UTF-8编码）
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "lang/messages",
                    I18nUtil.getCurrentLocale(),
                    new Utf8ResourceControl()
            );

            // 2. 创建FXMLLoader并绑定资源
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/" + fxmlFile));
            loader.setResources(bundle); // 关键绑定
            loader.setCharset(StandardCharsets.UTF_8);

            // 3. 加载并显示
            Parent root = loader.load();
            mainPane.setCenter(root);

        } catch (Exception e) {
            // 错误处理
            e.printStackTrace();
        }
    }

    // UTF-8资源控制类
    private static class Utf8ResourceControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale,
                                        String format, ClassLoader loader, boolean reload) throws IOException {
            String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
            try (InputStream is = loader.getResourceAsStream(resourceName);
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            }
        }
    }


    // NEW: 获取当前FXML文件名（需要根据项目实际实现）
    private String getCurrentFxmlName() {
        // 示例实现，实际应根据currentController类型返回对应文件名
        if (currentController instanceof LedgerController) {
            return "ledger.fxml";
        } else if (currentController instanceof SettingsController) {
            return "settings.fxml";
        }
        return "ledger.fxml"; // 默认返回
    }

    public Object getCurrentController() {
        return currentController;
    }
}

