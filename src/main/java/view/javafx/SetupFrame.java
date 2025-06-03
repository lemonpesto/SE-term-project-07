package view.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.BoardShape;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX 버전의 SetupFrame 역할을 하는 클래스입니다.
 * – 플레이어 이름 입력란을 기본 2개로 시작하고,
 *   “+ 플레이어 추가” 버튼을 눌러 최대 4명까지 추가 혹은 삭제할 수 있습니다.
 * – 플레이어당 말 개수와 보드 모양을 선택할 수 있고,
 *   모든 입력란이 채워질 때만 “게임 시작” 버튼이 활성화됩니다.
 * – 한눈에 4명의 입력란을 모두 볼 수 있도록 높이를 고정하였습니다.
 */
public class SetupFrame extends Stage {

    /** Swing 쪽 SetupFrame.SetupListener와 동일한 역할을 하는 인터페이스 */
    public interface SetupListener {
        void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape);
    }

    private SetupListener listener;

    // 최소/최대 플레이어 수
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    // 최소/최대 말 개수
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;
    // 기본 말 개수
    private static final int DEFAULT_PIECES = 4;

    // 플레이어 이름 입력 필드를 담아둘 컨테이너 (VBox)
    private final VBox nameFieldsBox = new VBox(10);
    // 실제 TextField 객체를 보관할 리스트
    private final List<TextField> nameFields = new ArrayList<>();
    // 각 TextField 옆에 붙는 제거 버튼(–)을 보관할 리스트
    private final List<Button> removeButtons = new ArrayList<>();

    // “+ 플레이어 추가” 버튼
    private final Button addPlayerButton = new Button("+ 플레이어 추가");
    // “게임 시작” 버튼
    private final Button startButton = new Button("게임 시작");
    // 플레이어당 말 개수 Spinner
    private final Spinner<Integer> spinnerPieces = new Spinner<>(MIN_PIECES, MAX_PIECES, DEFAULT_PIECES);
    // 보드 모양 ComboBox
    private final ComboBox<BoardShape> comboShape = new ComboBox<>();

    public SetupFrame() {
        setTitle("JavaFX 윷놀이 게임 설정");

        // 전체 창 크기를 넉넉히 설정 (예: 450x550)
        setWidth(450);
        setHeight(550);

        // ─────────────────────────────────────────────────────────────────────────
        // 1) 상단 제목 “윷놀이 게임 설정”
        // ─────────────────────────────────────────────────────────────────────────
        Label titleLabel = new Label("윷놀이 게임 설정");
        titleLabel.setFont(Font.font(24));
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 0, 10, 0));

        // ─────────────────────────────────────────────────────────────────────────
        // 2) 초기 플레이어 이름 입력란 2개 생성
        // ─────────────────────────────────────────────────────────────────────────
        for (int i = 0; i < MIN_PLAYERS; i++) {
            addNameField();
        }
        // nameFieldsBox 높이를 고정하여 최대 4명의 입력란이 한눈에 보이도록 설정
        nameFieldsBox.setPrefHeight(4 * 50);  // 각 행 대략 50px 높이로 가정
        nameFieldsBox.setMinHeight(4 * 50);
        nameFieldsBox.setMaxHeight(4 * 50);
        nameFieldsBox.setFillWidth(true);

        // ─────────────────────────────────────────────────────────────────────────
        // 3) “+ 플레이어 추가” 버튼 설정
        // ─────────────────────────────────────────────────────────────────────────
        HBox addBox = new HBox(addPlayerButton);
        addBox.setAlignment(Pos.CENTER_RIGHT);
        addBox.setPadding(new Insets(5, 0, 5, 0));
        addPlayerButton.setOnAction(e -> {
            if (nameFields.size() < MAX_PLAYERS) {
                addNameField();
                updateAddButtonState();
                validateStartButton();
            }
        });
        updateAddButtonState(); // 초기 상태 확인

        // ─────────────────────────────────────────────────────────────────────────
        // 4) 플레이어당 말 개수 설정 Spinner
        // ─────────────────────────────────────────────────────────────────────────
        Label lblPieces = new Label("말 개수(플레이어당):");
        spinnerPieces.setPrefWidth(80);
        HBox rowPieces = new HBox(10, lblPieces, spinnerPieces);
        rowPieces.setPadding(new Insets(5));
        rowPieces.setMaxWidth(Double.MAX_VALUE);

        // ─────────────────────────────────────────────────────────────────────────
        // 5) 보드 모양 ComboBox 설정
        // ─────────────────────────────────────────────────────────────────────────
        Label lblShape = new Label("보드 모양:");
        comboShape.getItems().addAll(BoardShape.values());
        comboShape.setValue(BoardShape.SQUARE);
        HBox rowShape = new HBox(10, lblShape, comboShape);
        rowShape.setPadding(new Insets(5));
        rowShape.setMaxWidth(Double.MAX_VALUE);

        // ─────────────────────────────────────────────────────────────────────────
        // 6) “게임 시작” 버튼 설정 (초기 비활성)
        // ─────────────────────────────────────────────────────────────────────────
        HBox startBox = new HBox(startButton);
        startBox.setAlignment(Pos.CENTER);
        startBox.setPadding(new Insets(10, 0, 0, 0));
        startButton.setDisable(true);
        startButton.setMinWidth(200);

        startButton.setOnAction(e -> {
            if (listener != null) {
                // 이름 배열 생성
                String[] names = nameFields.stream()
                        .map(tf -> {
                            String txt = tf.getText().trim();
                            return txt.isEmpty() ? "Player" + (nameFields.indexOf(tf) + 1) : txt;
                        })
                        .toArray(String[]::new);
                // 선택된 말 개수, 보드 모양 가져오기
                int pieces = spinnerPieces.getValue();
                BoardShape shape = comboShape.getValue();
                // 리스너에게 전달
                listener.onStartClicked(names, pieces, shape);
            }
        });

        // ─────────────────────────────────────────────────────────────────────────
        // 7) 전체 레이아웃 구성 (VBox)
        // ─────────────────────────────────────────────────────────────────────────
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                titleBox,
                nameFieldsBox,
                addBox,
                rowPieces,
                rowShape,
                startBox
        );
        // 폭 맞춤: 모든 자식이 VBox 폭(450)에 맞추도록 설정
        content.setFillWidth(true);
        VBox.setVgrow(nameFieldsBox, Priority.ALWAYS);

        Scene scene = new Scene(content);
        setScene(scene);
    }

    /**
     * SetupListener를 등록합니다.
     */
    public void setSetupListener(SetupListener listener) {
        this.listener = listener;
    }

    /**
     * 이름 입력란과 “–” 버튼을 하나 추가하는 메서드.
     * 최대 MAX_PLAYERS(4)명까지만 허용하며, index 2 이상일 때에만 삭제 버튼을 활성화합니다.
     */
    private void addNameField() {
        int index = nameFields.size() + 1; // 새로 추가될 플레이어 번호 (1-based)

        // HBox 한 줄(Row) 생성
        HBox row = new HBox(10);
        row.setPadding(new Insets(5));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        Label lbl = new Label("플레이어 " + index + " 이름:");
        lbl.setPrefWidth(110);
        TextField tf = new TextField("Player" + index);
        tf.setPrefWidth(200);

        Button btnRemove = new Button("–");
        boolean canRemove = (index > MIN_PLAYERS);
        btnRemove.setDisable(!canRemove);
        btnRemove.setVisible(canRemove);

        btnRemove.setOnAction(e -> {
            int removeIndex = nameFields.indexOf(tf);
            nameFieldsBox.getChildren().remove(removeIndex);
            nameFields.remove(removeIndex);
            removeButtons.remove(btnRemove);
            rebuildNameLabels();
            updateAddButtonState();
            validateStartButton();
        });

        tf.textProperty().addListener((obs, oldVal, newVal) -> validateStartButton());

        nameFields.add(tf);
        removeButtons.add(btnRemove);

        row.getChildren().addAll(lbl, tf, btnRemove);
        nameFieldsBox.getChildren().add(row);
    }

    /**
     * “플레이어 N 이름” 레이블 숫자를 현재 순서에 맞춰 재조정합니다.
     */
    private void rebuildNameLabels() {
        for (int i = 0; i < nameFieldsBox.getChildren().size(); i++) {
            HBox row = (HBox) nameFieldsBox.getChildren().get(i);
            Label lbl = (Label) row.getChildren().get(0);
            lbl.setText("플레이어 " + (i + 1) + " 이름:");
            Button btnRemove = (Button) row.getChildren().get(2);
            if (i + 1 > MIN_PLAYERS) {
                btnRemove.setVisible(true);
                btnRemove.setDisable(false);
            } else {
                btnRemove.setVisible(false);
                btnRemove.setDisable(true);
            }
        }
    }

    /**
     * “+ 플레이어 추가” 버튼을 활성/비활성화합니다.
     */
    private void updateAddButtonState() {
        addPlayerButton.setDisable(nameFields.size() >= MAX_PLAYERS);
    }

    /**
     * “게임 시작” 버튼을 활성/비활성화합니다.
     */
    private void validateStartButton() {
        boolean allFilled = nameFields.size() >= MIN_PLAYERS;
        if (allFilled) {
            for (TextField tf : nameFields) {
                if (tf.getText().trim().isEmpty()) {
                    allFilled = false;
                    break;
                }
            }
        }
        startButton.setDisable(!allFilled);
    }
}
