package com.myapp;

import com.myapp.model.TransactionLoader;
import com.myapp.model.Transactions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class Main extends Application {

    private static Main instance;


    private BorderPane mainPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        //VBox root = loader.load();
        mainPane = loader.load();

        Scene scene = new Scene(mainPane, 1490, 900);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("AccountEase");


        primaryStage.show();



    }








    public static void main(String[] args) {
        launch(args);
    }
}
