package view.javaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class javaFXMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(javaFXMain.class.getResource("Yutnori-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("동물원 조진세 볶음면의 윷놀이");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
