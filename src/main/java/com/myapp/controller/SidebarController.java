package com.myapp.controller;

import com.myapp.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private ImageView Icon;
    @FXML private ImageView Profile;
    @FXML private ImageView ledgerImage;
//    @FXML private ImageView deepAccountImage;
    @FXML private ImageView settingsImage;
    @FXML private ImageView refreshImage;
    @FXML private ImageView questionImage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image_Icon = new Image(getClass().getResource("/images/Icon.png").toExternalForm());
        Icon.setImage(image_Icon);
        Rectangle clip = new Rectangle(60, 60);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        Icon.setClip(clip);

        Profile.setImage(new Image(getClass().getResource("/images/profile.jpg").toExternalForm()));
        Rectangle clip_Profile = new Rectangle(60, 60);
        clip_Profile.setArcWidth(40);
        clip_Profile.setArcHeight(40);
        Profile.setClip(clip_Profile);

        ledgerImage.setImage(new Image(getClass().getResource("/images/ledger.png").toExternalForm()));
//        deepAccountImage.setImage(new Image(getClass().getResource("/images/AI.png").toExternalForm()));
        settingsImage.setImage(new Image(getClass().getResource("/images/settings.png").toExternalForm()));
        questionImage.setImage(new Image(getClass().getResource("/images/question.png").toExternalForm()));
        refreshImage.setImage(new Image(getClass().getResource("/images/refresh.png").toExternalForm()));
    }



    @FXML
    private void handleLedgerClick() {
        MainController.getInstance().loadPage("ledger.fxml");
    }

//    @FXML
//    private void handleDeepAccountClick() {
//        MainController.getInstance().loadPage("deepAccount.fxml");
//    }

    @FXML
    private void handleSettingsClick() {
        MainController.getInstance().loadPage("settings.fxml");
    }



    @FXML
    private void handlerefresh() {
        System.out.println("点击了刷新按钮");

        Object controller = MainController.getInstance().getCurrentController();
        System.out.println(controller);
        if (controller != null) {
            System.out.println("当前控制器类型: " + controller.getClass().getSimpleName());
        } else {
            System.out.println("当前控制器为空");
        }

        if (controller instanceof LedgerController) {
            System.out.println("是 LedgerController，准备调用 updateDashboard()");
            ((LedgerController) controller).updateDashboard(); // 调用刷新方法
            System.out.println("刷新完成");
        } else {
            System.out.println("不是 LedgerController，无法刷新");
        }
    }


    @FXML
    private void handlequestion() {
        System.out.println("问题");
    }
}
