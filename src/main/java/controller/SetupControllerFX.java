package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Game;
import view.javafx.JavaFXGameView;
import view.javafx.SetupFrame;

public class SetupControllerFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // (1) 설정 화면 SetupFrame 생성
        SetupFrame setupView = new SetupFrame();

        // (2) SetupListener 등록: 사용자가 "게임 시작" 눌렀을 때 처리
        setupView.setSetupListener((playerNames, piecesPerPlayer, boardShape) -> {
            // Game 모델 생성
            Game game = new Game(playerNames.length, playerNames, piecesPerPlayer, boardShape);

            // 게임 뷰 생성
            JavaFXGameView gameView = new JavaFXGameView(game);

            // GameController 생성 및 연결
            GameController gameController = new GameController(game, gameView);

            // 설정 창 닫기
            setupView.close();
        });

        // (3) 설정 창 보여주기
        setupView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}