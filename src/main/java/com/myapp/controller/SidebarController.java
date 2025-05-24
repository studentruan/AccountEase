package com.myapp.controller;

import Backend.GlobalContext;
import Backend.Ledger;
import com.myapp.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

    private Ledger ledger;

    @FXML
    private void handleLedgerClick() {
        ledger = GlobalContext.getInstance().getCurrentLedger();
        MainController.getInstance().loadPage("ledger.fxml");
        Object controller = MainController.getInstance().getCurrentController();



        if (controller instanceof LedgerController ledgerController) {
            ledgerController.loadLedger(GlobalContext.getInstance().getCurrentLedger());
        }


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
        // 创建 Alert 弹窗（类型为 INFORMATION）
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        // 设置弹窗标题
        alert.setTitle("Information");

        // 设置弹窗头部文本（可选，设为 null 则不显示）
        alert.setHeaderText(null);

        // 设置弹窗内容文本（显示英文提示）
        alert.setContentText("If there's a problem, there's nothing we can do.");

        // 显示弹窗并等待用户关闭
        alert.showAndWait();
    }
}
