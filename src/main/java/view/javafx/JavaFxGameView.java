package view.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXGameView extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX 기본 창");
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
    }

    public static void launchUI() {
        launch();  // Application.launch()를 호출해서 start()가 실행되게 함
    }
}
