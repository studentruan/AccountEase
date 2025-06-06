package com.myapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static Main instance;

    private BorderPane mainPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ledgermanager.fxml"));
        VBox root = loader.load();
//        mainPane = loader.load();

        Scene scene = new Scene(root, 1490, 900);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("AccountEase");


        primaryStage.show();



    }








    public static void main(String[] args) {
        launch(args);
    }
}
