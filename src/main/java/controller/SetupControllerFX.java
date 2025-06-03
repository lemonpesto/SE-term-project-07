package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Game;
//import view.javafx.JavaFXGameView;
import view.javafx.SetupFrame;

public class SetupControllerFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // (1) 설정 화면 SetupViewFX 생성
        SetupFrame setupView = new SetupFrame();

        // (2) SetupListener 등록: 사용자가 “게임 시작” 눌렀을 때 처리
        setupView.setSetupListener((playerNames, piecesPerPlayer, boardShape) -> {
            // Game 모델 생성
            Game game = new Game(playerNames.length, playerNames, piecesPerPlayer, boardShape);
            game.startGame(); // 필요 시 초기화 로직

            // 새 게임 화면 표시
            //JavaFXGameView gameView = new JavaFXGameView(game);
            //gameView.show(new Stage());

            // 설정 창 닫기
            setupView.close();
        });

        // (3) 설정 창(primaryStage) 보여주기
        setupView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
