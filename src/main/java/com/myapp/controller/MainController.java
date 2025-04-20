package com.myapp.controller;

import com.myapp.model.Ledger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static MainController instance;
    public static MainController getInstance() {
        return instance;
    }

    @FXML private BorderPane mainPane;
    @FXML private AnchorPane contentArea;
    private Object currentController; // 加上这一行，全局保存控制器

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        loadPage("ledger.fxml"); // 默认页面


    }

    public Object getCurrentController() {
        return currentController;
    }

    public void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            BorderPane page = loader.load();

            mainPane.setCenter(page);
            currentController = loader.getController(); // ⬅️ 保存当前页面控制器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//public void loadPage(String fxmlFile) {
//    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
//        Parent page = loader.load();
//
//        // 锚点绑定，页面铺满 contentArea
//        AnchorPane.setTopAnchor(page, 0.0);
//        AnchorPane.setBottomAnchor(page, 0.0);
//        AnchorPane.setLeftAnchor(page, 0.0);
//        AnchorPane.setRightAnchor(page, 0.0);
//
//        contentArea.getChildren().setAll(page);
//
//        currentController = loader.getController();
//
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
}
