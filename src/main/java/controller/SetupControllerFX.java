package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.BoardShape;
import model.Game;
import view.IGameView;
import view.ISetupViewListener;
import view.javafx.JavaFXGameView;
import view.javafx.JavaFXSetupView;

/**
 * SetupControllerFX
 *
 * JavaFX Application을 상속받아 JavaFX 애플리케이션의 진입점 역할
 * 설정 화면을 표시하고, 게임 시작 시 게임 화면으로 전환하는 컨트롤러
 * MVC 패턴을 준수하여 Model과 View를 연결
 */
public class SetupControllerFX extends Application implements ISetupViewListener {

    private JavaFXSetupView setupView;

    /**
     * JavaFX Application의 시작점
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 설정 화면 생성 및 표시
        setupView = new JavaFXSetupView();
        setupView.setSetupViewListener(this);
        setupView.showView();

        // 기본 Stage는 사용하지 않으므로 숨김
        primaryStage.hide();
    }

    /**
     * ISetupViewListener 구현 - 게임 시작 버튼 클릭 시 호출
     */
    @Override
    public void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape) {
        Platform.runLater(() -> {
            try {
                // 게임 모델 생성
                Game game = new Game(playerNames.length, playerNames, piecesPerPlayer, boardShape);

                // 게임 뷰 생성
                IGameView gameView = new JavaFXGameView(game);

                // 게임 컨트롤러 생성 및 연결
                GameController gameController = new GameController(game, gameView);

                // 설정 화면 닫기
                if (setupView != null) {
                    setupView.closeView();
                }

            } catch (Exception e) {
                e.printStackTrace();
                // 에러 발생 시 알림 표시
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setHeaderText("게임 시작 중 오류가 발생했습니다.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    /**
     * 애플리케이션 종료 시 정리 작업
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    /**
     * main 메서드 - 애플리케이션 진입점
     */
    public static void main(String[] args) {
        launch(args);
    }
}