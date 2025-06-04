package view.javafx;

import controller.SetupControllerFX;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Game;
import model.Player;
import model.ThrowResult;
import model.Piece;
import view.IGameView;
import view.IGameViewListener;
import view.ISetupViewListener;

import java.util.List;
import java.util.Optional;

/**
 * JavaFXGameView
 *
 * IGameView 인터페이스를 구현한 JavaFX 버전 게임 화면
 * MVC 패턴을 준수하여 View와 Controller를 분리
 */
public class JavaFXGameView implements IGameView {

    private final Stage stage;
    private final Game game;
    private GameBoardPanel gameBoardPanel;

    private Label statusLabel;
    private Button randomThrowButton;
    private Button fixedThrowButton;
    private ComboBox<ThrowResult> fixedThrowCombo;

    private IGameViewListener listener;
    private boolean pieceSelectable = false;

    /**
     * 생성자: 외부에서 Stage를 전달받아 사용할 때
     */
    public JavaFXGameView(Stage primaryStage, Game game) {
        this.stage = primaryStage;
        this.game = game;
        this.gameBoardPanel = new GameBoardPanel(game.getBoard(), game);
        initUI();
    }

    /**
     * 생성자: Stage를 직접 새로 생성하여 사용할 때
     */
    public JavaFXGameView(Game game) {
        this(new Stage(), game);
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        // 최상위 레이아웃
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));

        // 중앙: GameBoardPanel
        StackPane centerContainer = new StackPane(gameBoardPanel);
        centerContainer.setPadding(new Insets(10));
        centerContainer.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        root.setCenter(centerContainer);

        // 하단: 컨트롤 패널
        VBox southContainer = createControlPanel();
        root.setBottom(southContainer);

        // Scene 설정
        Scene scene = new Scene(root, 600, 700);
        stage.setTitle("윷놀이 게임 (JavaFX)");
        stage.setScene(scene);

        // 창 닫기 이벤트 처리
        stage.setOnCloseRequest(e -> {
            Platform.exit();
        });

        setupEventHandlers();
    }

    /**
     * 하단 컨트롤 패널 생성
     */
    private VBox createControlPanel() {
        VBox southContainer = new VBox(5);
        southContainer.setPadding(new Insets(10));
        southContainer.setAlignment(Pos.CENTER);

        // 버튼 패널
        HBox buttonPane = new HBox(10);
        buttonPane.setAlignment(Pos.CENTER);

        // 랜덤 윷 던지기 버튼
        randomThrowButton = new Button("랜덤 윷 던지기");
        randomThrowButton.setDisable(false);

        // 지정 윷 던지기 콤보박스와 버튼
        fixedThrowCombo = new ComboBox<>(FXCollections.observableArrayList(ThrowResult.values()));
        fixedThrowCombo.setValue(ThrowResult.DO);

        fixedThrowButton = new Button("지정 윷 던지기");
        fixedThrowButton.setDisable(false);

        buttonPane.getChildren().addAll(
                randomThrowButton,
                new Label("지정 결과:"),
                fixedThrowCombo,
                fixedThrowButton
        );

        // 상태 레이블
        statusLabel = new Label("게임 준비 중...");
        statusLabel.setMinHeight(30);
        HBox statusPane = new HBox(statusLabel);
        statusPane.setAlignment(Pos.CENTER);

        southContainer.getChildren().addAll(buttonPane, statusPane);
        return southContainer;
    }

    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // GameBoardPanel의 말 클릭 처리
        gameBoardPanel.setGameViewListener(new IGameViewListener() {
            @Override
            public void onRandomThrowClicked() {
                // GameBoardPanel에서는 사용하지 않음
            }

            @Override
            public void onFixedThrowClicked(ThrowResult tr) {
                // GameBoardPanel에서는 사용하지 않음
            }

            @Override
            public void onPieceClicked(Piece piece) {
                if (!pieceSelectable || listener == null) return;
                listener.onPieceClicked(piece);
            }
        });

        // 랜덤 윷 던지기 버튼
        randomThrowButton.setOnAction(e -> {
            if (listener != null) {
                listener.onRandomThrowClicked();
            }
        });

        // 지정 윷 던지기 버튼
        fixedThrowButton.setOnAction(e -> {
            ThrowResult selected = fixedThrowCombo.getValue();
            if (selected != null && listener != null) {
                listener.onFixedThrowClicked(selected);
            }
        });
    }

    // IGameView 인터페이스 구현
    @Override
    public void setGameViewListener(IGameViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void showWindow() {
        Platform.runLater(() -> {
            if (!stage.isShowing()) {
                stage.show();
            }
        });
    }

    @Override
    public void updateBoard() {
        if (gameBoardPanel != null) {
            gameBoardPanel.updateBoard();
        }
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
    public void delayNextTurn(Runnable action, int delayMillis) {
        // JavaFX에서 일정 시간 후 작업을 실행하려면 PauseTransition을 사용합니다.
        PauseTransition pause = new PauseTransition(Duration.millis(delayMillis));
        pause.setOnFinished(e -> action.run());
        pause.play();
    }


    @Override
    public void setPieceSelectable(boolean enabled) {
        this.pieceSelectable = enabled;
    }

    @Override
    public void setThrowEnabled(boolean enabled) {
        Platform.runLater(() -> {
            if (randomThrowButton != null) {
                randomThrowButton.setDisable(!enabled);
            }
            if (fixedThrowButton != null) {
                fixedThrowButton.setDisable(!enabled);
            }
            if (fixedThrowCombo != null) {
                fixedThrowCombo.setDisable(!enabled);
            }
        });
    }

    @Override
    public void showRankingDialog(List<Player> ranking) {
        Platform.runLater(() -> {
            // 1) 순위 문자열 생성
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ranking.size(); i++) {
                Player p = ranking.get(i);
                sb.append((i + 1)).append("등: ").append(p.getName());
                if (i < ranking.size() - 1) sb.append("\n");
            }

            // 2) Alert 창 생성
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.initOwner(stage); // 기존 게임 창의 Stage
            alert.setTitle("최종 등수");
            alert.setHeaderText(sb.toString());
            alert.setContentText("게임을 다시 시작하시겠습니까?");

            // 3) 버튼 추가
            ButtonType restartBtn = new ButtonType("다시 시작");
            ButtonType exitBtn    = new ButtonType("종료");
            alert.getButtonTypes().setAll(restartBtn, exitBtn);

            // 4) 사용자 선택에 따라 동작
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == restartBtn) {
                    // 다시 시작 버튼 클릭 시 - 게임 설정 창부터 다시 시작
                    stage.close(); // 현재 게임 창 닫기

                    // 새로운 게임 설정 창 띄우기 (YutNoriApplication의 start 메소드 호출)
                    try {
                        new SetupControllerFX().start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (result.get() == exitBtn) {
                    // 종료 버튼 클릭 시 - 게임 창 닫기
                    stage.close();
                }
            }
        });
    }
}