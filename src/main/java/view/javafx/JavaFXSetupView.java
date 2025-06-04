// src/main/java/view/javafx/JavaFXSetupView.java
package view.javafx;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.BoardShape;
import view.ISetupView;
import view.ISetupViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFXSetupView
 *
 * ISetupView 인터페이스를 구현한 JavaFX 버전 설정 화면입니다.
 * - ISetupViewListener를 통해 컨트롤러에게 “게임 시작” 이벤트를 전달합니다.
 * - 모든 final 필드를 생성자에서 초기화하여“might not have been initialized” 오류를 제거했습니다.
 */
public class JavaFXSetupView extends Stage implements ISetupView {

    // 콜백을 받을 리스너
    private ISetupViewListener listener;

    // 상수 정의
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;
    private static final int DEFAULT_PIECES = 4;

    // UI 컴포넌트 필드(모두 final)
    private final VBox nameListBox;
    private final List<TextField> nameFields;
    private final List<Button> removeButtons;
    private final Button addPlayerButton;
    private final Spinner<Integer> spinnerPieces;
    private final ComboBox<BoardShape> comboBoardShape;
    private final Button btnStart;

    public JavaFXSetupView() {
        setTitle("윷놀이 게임 설정");

        // 1) 모든 컴포넌트 초기화
        nameListBox = new VBox(5);
        nameFields = new ArrayList<>();
        removeButtons = new ArrayList<>();

        addPlayerButton = new Button("+ 플레이어 추가");
        spinnerPieces = new Spinner<>(MIN_PIECES, MAX_PIECES, DEFAULT_PIECES);
        comboBoardShape = new ComboBox<>(FXCollections.observableArrayList(BoardShape.values()));
        btnStart = new Button("게임 시작");
        //btnStart.setDisable(false);

        // 2) 레이아웃 구성
        setupLayout();

        // 3) 이벤트 핸들러 연결
        setupEventHandlers();

        // 4) “닫기” 버튼 누를 때 애플리케이션 종료되지 않도록
        setOnCloseRequest(e -> {
            e.consume();
            closeView();
        });
    }

    private void setupLayout() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // ─── 상단: 제목 ─────────────────────────────────────────
        Label lblTitle = new Label("윷놀이 설정");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(lblTitle, Pos.CENTER);
        root.setTop(lblTitle);

        // ─── 중앙: 설정 폼(GridPane) ───────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        root.setCenter(grid);

        int row = 0;

        // 1) 플레이어 이름 입력 리스트 (ScrollPane + VBox)
        nameListBox.setPadding(new Insets(5));
        nameListBox.setPrefHeight(120);
        nameListBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        ScrollPane scrollPane = new ScrollPane(nameListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(120);
        GridPane.setConstraints(scrollPane, 0, row, 2, 1);
        grid.getChildren().add(scrollPane);
        row++;

        // 2) “+ 플레이어 추가” 버튼
        addPlayerButton.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        GridPane.setConstraints(addPlayerButton, 0, row, 2, 1);
        grid.getChildren().add(addPlayerButton);
        row++;

        // 기본으로 MIN_PLAYERS명(2명)만큼 이름 입력란 생성
        addPlayerField();
        addPlayerField();

        // 3) 플레이어당 말 개수 선택 (Spinner)
        Label lblPieces = new Label("말 개수 (플레이어당):");
        GridPane.setConstraints(lblPieces, 0, row);
        grid.getChildren().add(lblPieces);

        spinnerPieces.setEditable(true);
        spinnerPieces.setPrefWidth(80);
        GridPane.setConstraints(spinnerPieces, 1, row);
        grid.getChildren().add(spinnerPieces);
        row++;

        // 4) 보드 모양 선택 (ComboBox)
        Label lblShape = new Label("보드 모양:");
        GridPane.setConstraints(lblShape, 0, row);
        grid.getChildren().add(lblShape);

        comboBoardShape.setValue(BoardShape.SQUARE);
        comboBoardShape.setPrefWidth(150);
        GridPane.setConstraints(comboBoardShape, 1, row);
        grid.getChildren().add(comboBoardShape);
        row++;

        // ─── 하단: “게임 시작” 버튼 ───────────────────────────────
        btnStart.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        btnStart.setDisable(true);  // 초기에는 비활성화
        BorderPane.setMargin(btnStart, new Insets(10, 0, 0, 0));
        BorderPane.setAlignment(btnStart, Pos.CENTER);
        root.setBottom(btnStart);

        // Scene & Stage 설정
        Scene scene = new Scene(root, 450, 400);
        setScene(scene);

        // 기본 버튼/필드 상태 갱신
        updateAddButtonState();
        validateStartButton();
    }

    private void setupEventHandlers() {
        // “+ 플레이어 추가” 버튼 클릭
        addPlayerButton.setOnAction(e -> {
            addPlayerField();
            updateAddButtonState();
            validateStartButton();
        });

        // “게임 시작” 버튼 클릭
        btnStart.setOnAction(e -> {
            if (listener != null) {
                String[] names = nameFields.stream()
                        .map(tf -> tf.getText().trim())
                        .toArray(String[]::new);
                int pieces = spinnerPieces.getValue();
                BoardShape shape = comboBoardShape.getValue();
                listener.onStartClicked(names, pieces, shape);
            }
        });
    }

    /**
     * 플레이어 이름 입력란을 하나 추가합니다.
     */
    private void addPlayerField() {
        int index = nameFields.size();

        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label("플레이어 " + (index + 1) + " 이름:");
        lbl.setPrefWidth(100);

        TextField tf = new TextField();
        tf.setPromptText("플레이어 이름");
        tf.setPrefWidth(180);
        tf.addEventFilter(KeyEvent.KEY_RELEASED, ke -> validateStartButton());

        Button btnRemove = new Button("–");
        btnRemove.setStyle("-fx-font-weight: bold;");
        btnRemove.setVisible(index >= MIN_PLAYERS);
        btnRemove.setDisable(index < MIN_PLAYERS);
        btnRemove.setOnAction(e -> {
            int removeIdx = nameFields.indexOf(tf);
            if (removeIdx >= MIN_PLAYERS) {
                nameFields.remove(tf);
                removeButtons.remove(btnRemove);
                rebuildNameListBox();
                updateAddButtonState();
                validateStartButton();
            }
        });

        nameFields.add(tf);
        removeButtons.add(btnRemove);

        hbox.getChildren().addAll(lbl, tf, btnRemove);
        nameListBox.getChildren().add(hbox);
    }

    /**
     * nameFields와 removeButtons 목록에 맞추어 VBox를 재구성합니다.
     */
    private void rebuildNameListBox() {
        nameListBox.getChildren().clear();
        for (int i = 0; i < nameFields.size(); i++) {
            TextField tf = nameFields.get(i);
            Button btnRemove = removeButtons.get(i);

            HBox hbox = new HBox(5);
            hbox.setAlignment(Pos.CENTER_LEFT);

            Label lbl = new Label("플레이어 " + (i + 1) + " 이름:");
            lbl.setPrefWidth(100);
            tf.setPrefWidth(180);

            if (i >= MIN_PLAYERS) {
                btnRemove.setVisible(true);
                btnRemove.setDisable(false);
            } else {
                btnRemove.setVisible(false);
                btnRemove.setDisable(true);
            }

            hbox.getChildren().addAll(lbl, tf, btnRemove);
            nameListBox.getChildren().add(hbox);
        }
    }

    private void updateAddButtonState() {
        addPlayerButton.setDisable(nameFields.size() >= MAX_PLAYERS);
    }

    private void validateStartButton() {
        boolean allValid = true;
        if (nameFields.size() < MIN_PLAYERS) {
            allValid = false;
        } else {
            for (TextField tf : nameFields) {
                if (tf.getText().trim().isEmpty()) {
                    allValid = false;
                    break;
                }
            }
        }
        btnStart.setDisable(!allValid);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ISetupView 인터페이스 구현부
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public void setSetupViewListener(ISetupViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void showView() {
        this.show();
    }

    @Override
    public void closeView() {
        this.close();
    }
}
