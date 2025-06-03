package view.javafx;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Game;
import model.Piece;
import view.IGameView;
import view.IGameViewListener;

/**
 * JavaFXGameView
 *
 * – IGameView 인터페이스를 구현한 JavaFX 버전 게임 화면입니다.
 * – showWindow() 호출 시, 새로운 Stage를 생성하여
 *   • 중앙에는 GameBoardPanel(보드판)을,
 *   • 하단에는 상태 레이블 + “윷 던지기” 버튼을 배치합니다.
 * – GameController는 외부에서 생성해 setGameViewListener(...)로 전달합니다.
 */
public class JavaFXGameView implements IGameView {
    private final Game game;
    private IGameViewListener listener;
    private Label statusLabel;
    private Button btnThrow;
    private Stage stage;

    // 보드판 전용 패널 (분리된 컴포넌트)
    private GameBoardPanel boardPanel;

    public JavaFXGameView(Game game) {
        this.game = game;
    }

    @Override
    public void showWindow() {
        // JavaFX Application Thread가 아닐 경우, 다시 스케줄링
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::showWindow);
            return;
        }

        stage = new Stage();
        stage.setTitle("JavaFX 윷놀이 게임");

        // (1) GameBoardPanel 생성 및 리스너 연결
        boardPanel = new GameBoardPanel(game);
        boardPanel.setGameViewListener(new IGameViewListener() {
            @Override
            public void onThrowButtonClicked() {
                // 보드판 클릭 이벤트가 아닌 “윷 던지기” 버튼 클릭용 (사용 안 함)
            }

            @Override
            public void onPieceClicked(Piece piece) {
                if (listener != null) {
                    listener.onPieceClicked(piece);
                }
            }
        });

        // (2) 상태 레이블
        statusLabel = new Label(game.getCurrentPlayer().getName() + "님, 윷을 던지세요!");
        statusLabel.setMinHeight(30);

        // (3) “윷 던지기” 버튼
        btnThrow = new Button("윷 던지기");
        btnThrow.setMinHeight(30);
        btnThrow.setOnAction(e -> {
            if (listener != null) {
                listener.onThrowButtonClicked();
            }
        });

        // (4) 하단 HBox: 상태 라벨 + 버튼
        HBox controlBox = new HBox(10, statusLabel, btnThrow);
        controlBox.setStyle("-fx-padding: 10;");
        controlBox.setMinHeight(40);

        // (5) 전체 레이아웃: BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(boardPanel);
        root.setBottom(controlBox);

        Scene scene = new Scene(root, 600, 700);
        stage.setScene(scene);
        stage.show();

        // (6) 처음 보드판과 상태 초기화
        updateBoard();
    }

    @Override
    public void setGameViewListener(IGameViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateBoard() {
        // GameBoardPanel.updateBoard()가 JavaFX 스레드인지 검사하여 drawBoard 호출
        boardPanel.updateBoard();
    }

    @Override
    public void updateStatus(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }

    @Override
    public void setPieceSelectable(boolean enabled) {
        // 시각적 변화가 필요하다면, 여기서 스타일을 바꾸거나
        // boardPanel 내부에서 클릭 허용/비허용 로직을 추가로 구현할 수 있습니다.
    }

    @Override
    public void showWinnerDialog(String winnerName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("게임 종료");
            alert.setHeaderText(null);
            alert.setContentText(winnerName + "님이 승리하셨습니다!");
            alert.showAndWait();
        });
    }
}
