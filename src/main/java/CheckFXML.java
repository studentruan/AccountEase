import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class CheckFXML extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            File fxmlFile = new File("/Users/windy/IdeaProjects/SE_UI/src/main/resources/fxml/ledger.fxml");
            URL fxmlUrl = fxmlFile.toURI().toURL();
            Pane root = FXMLLoader.load(fxmlUrl);
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setTitle("FXML Check");
            primaryStage.show();
            System.out.println("✅ FXML 文件格式正确！");
        } catch (IOException e) {
            System.out.println("❌ FXML 语法错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // 这个方法会自动初始化 JavaFX 线程
    }


}

