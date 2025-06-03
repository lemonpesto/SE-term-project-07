package view.javafx;

import controller.GameController;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Board;
import model.Cell;
import model.Game;
import model.Piece;
import model.PieceState;
import model.Player;
import view.IGameView;
import view.IGameViewListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JavaFXGameView
 *
 * – IGameView 인터페이스를 구현한 JavaFX 버전 게임 화면입니다.
 * – showWindow()가 호출되면 게임 창을 표시합니다.
 * – GameController는 외부에서 생성되어 setGameViewListener로 전달됩니다.
 */
public class JavaFXGameView implements IGameView {
    private final Game game;
    private IGameViewListener listener;
    private Canvas boardCanvas;
    private Label statusLabel;
    private Button btnThrow;
    private Stage stage;

    // Piece → 화면상의 Rectangle2D 영역 매핑
    private final Map<Piece, javafx.geometry.Rectangle2D> pieceBounds = new HashMap<>();

    private static final double CANVAS_SIZE = 520;
    private static final double CENTER = CANVAS_SIZE / 2.0;
    private static final double RADIUS = 180.0;
    private static final double NODE_SIZE = 32.0;
    private static final double PIECE_RADIUS = 10.0;
    private static final double OFFBOARD_RADIUS = RADIUS + 40.0;
    private static final double OFFBOARD_PIECE_RADIUS = 10.0;
    private static final double OFFBOARD_GAP = 4.0;

    public JavaFXGameView(Game game) {
        this.game = game;
    }

    /**
     * IGameView.showWindow()
     * – 새로운 Stage를 만들고, 보드 + 컨트롤을 배치한 후 창을 표시합니다.
     * – GameController는 별도로 생성하지 않고, 외부에서 setGameViewListener로 설정됩니다.
     */
    @Override
    public void showWindow() {
        // JavaFX Application Thread에서 실행되도록 보장
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(this::showWindow);
            return;
        }

        stage = new Stage();
        stage.setTitle("JavaFX 윷놀이 게임");

        // (1) Canvas 생성 및 초기 draw
        boardCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        drawBoard();  // 최초 한 번만 그립니다.

        // (2) 클릭 리스너: Piece 클릭 시 listener.onPieceClicked(piece) 호출
        boardCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (listener == null) return;
            double x = e.getX();
            double y = e.getY();
            Piece clicked = getPieceAtPoint(x, y);
            if (clicked != null) {
                listener.onPieceClicked(clicked);
            }
        });

        // (3) 상태 라벨: 현재 차례 플레이어에게 "윷을 던지세요!" 메시지
        statusLabel = new Label(game.getCurrentPlayer().getName() + "님, 윷을 던지세요!");
        statusLabel.setMinHeight(30);

        // (4) "윷 던지기" 버튼
        btnThrow = new Button("윷 던지기");
        btnThrow.setMinHeight(30);
        btnThrow.setOnAction(e -> {
            if (listener != null) {
                listener.onThrowButtonClicked();
            }
        });

        // (5) 하단 HBox: statusLabel + btnThrow
        HBox controlBox = new HBox(10, statusLabel, btnThrow);
        controlBox.setStyle("-fx-padding: 10;");  // 여백
        controlBox.setMinHeight(40);

        // (6) 레이아웃: BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(boardCanvas);
        root.setBottom(controlBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(CANVAS_SIZE + 20);
        stage.setHeight(CANVAS_SIZE + 100);

        // (7) 창 표시
        stage.show();
    }

    @Override
    public void setGameViewListener(IGameViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateBoard() {
        // JavaFX UI 스레드에서 drawBoard() 호출
        javafx.application.Platform.runLater(this::drawBoard);
    }

    @Override
    public void updateStatus(String message) {
        javafx.application.Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }

    @Override
    public void setPieceSelectable(boolean enabled) {
        // 필요하다면 drawBoard()에서 시각적 변화를 줄 수 있음
    }

    @Override
    public void showWinnerDialog(String winnerName) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("게임 종료");
            alert.setHeaderText(null);
            alert.setContentText(winnerName + "님이 승리하셨습니다!");
            alert.showAndWait();
        });
    }

    // =======================================
    // 보드판과 말 그리기
    // =======================================
    private void drawBoard() {
        if (boardCanvas == null) return;

        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        Board board = game.getBoard();
        int sides = board.getShape().getVertexCount();
        int cellsPerEdge = board.getCellsPerEdge();

        // [1] 꼭짓점 좌표 계산
        Point2D[] vertexPoints = new Point2D[sides];
        for (int i = 0; i < sides; i++) {
            double theta = Math.PI / 2.0 - 2.0 * Math.PI * i / sides;
            double x = CENTER + RADIUS * Math.cos(theta);
            double y = CENTER + RADIUS * Math.sin(theta);
            vertexPoints[i] = new Point2D(x, y);
        }

        // [2] edge cell 좌표 계산
        Point2D[][] edgePoints = new Point2D[sides][cellsPerEdge];
        for (int i = 0; i < sides; i++) {
            Point2D from = vertexPoints[i];
            Point2D to = vertexPoints[(i + 1) % sides];
            for (int j = 0; j < cellsPerEdge; j++) {
                double t = (j + 1) / (double) (cellsPerEdge + 1);
                double ex = from.getX() * (1 - t) + to.getX() * t;
                double ey = from.getY() * (1 - t) + to.getY() * t;
                edgePoints[i][j] = new Point2D(ex, ey);
            }
        }

        // [3] 중앙 좌표
        Point2D centerPoint = new Point2D(CENTER, CENTER);

        // [4] getPosition 함수
        Function<String, Point2D> getPosition = id -> {
            if (id.startsWith("V")) {
                int idx = Integer.parseInt(id.substring(1));
                return vertexPoints[idx];
            }
            if (id.startsWith("E")) {
                String[] parts = id.substring(1).split("_");
                int vi = Integer.parseInt(parts[0]);
                int ej = Integer.parseInt(parts[1]);
                return edgePoints[vi][ej];
            }
            if (id.equals("C")) {
                return centerPoint;
            }
            if (id.startsWith("D")) {
                String[] parts = id.substring(1).split("_");
                int vi = Integer.parseInt(parts[0]);
                int dj = Integer.parseInt(parts[1]);
                Point2D v = vertexPoints[vi];
                double t;
                if (vi == 0 || vi == sides - 1) {
                    t = (dj == 0) ? 0.7 : 0.4;
                } else {
                    t = (dj == 0) ? 0.4 : 0.7;
                }
                double x = v.getX() * (1 - t) + centerPoint.getX() * t;
                double y = v.getY() * (1 - t) + centerPoint.getY() * t;
                return new Point2D(x, y);
            }
            return new Point2D(0, 0);
        };

        // [5] 셀 연결선 그리기
        gc.setStroke(Color.LIGHTGRAY);
        for (Cell cell : board.getAllCells()) {
            Point2D from = getPosition.apply(cell.getId());
            for (Cell next : cell.getNextCells()) {
                Point2D to = getPosition.apply(next.getId());
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
            }
        }

        // [6] 셀 테두리·색 채우기
        for (Cell cell : board.getAllCells()) {
            Point2D p = getPosition.apply(cell.getId());
            double cx = p.getX(), cy = p.getY();
            if (cell.getId().startsWith("V") || cell.getId().equals("C")) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.0);
                gc.strokeOval(cx - NODE_SIZE / 2 - 5, cy - NODE_SIZE / 2 - 5,
                        NODE_SIZE + 10, NODE_SIZE + 10);
                gc.setFill(Color.RED);
                gc.fillOval(cx - NODE_SIZE / 2 - 5, cy - NODE_SIZE / 2 - 5,
                        NODE_SIZE + 10, NODE_SIZE + 10);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(cx - NODE_SIZE / 2, cy - NODE_SIZE / 2,
                        NODE_SIZE, NODE_SIZE);
                if (cell.isStartCell()) {
                    gc.setFill(Color.BLACK);
                    gc.fillText("START", cx - NODE_SIZE / 2, cy + 6);
                }
            } else {
                gc.setFill(Color.WHITE);
                gc.fillOval(cx - NODE_SIZE / 2, cy - NODE_SIZE / 2,
                        NODE_SIZE, NODE_SIZE);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(cx - NODE_SIZE / 2, cy - NODE_SIZE / 2,
                        NODE_SIZE, NODE_SIZE);
            }
        }

        // [7] Occupant(칸 위 말) 그리기 + 클릭 범위 저장
        pieceBounds.clear();
        for (Cell cell : board.getAllCells()) {
            Point2D cellCenter = getPosition.apply(cell.getId());
            List<Piece> occupants = cell.getOccupants();
            if (occupants.isEmpty()) continue;
            int count = occupants.size();
            for (int i = 0; i < count; i++) {
                Piece piece = occupants.get(i);
                double offsetX = (i % 2 == 0) ? -PIECE_RADIUS : PIECE_RADIUS;
                double offsetY = (i < 2) ? -PIECE_RADIUS : PIECE_RADIUS;
                double px = cellCenter.getX() + offsetX;
                double py = cellCenter.getY() + offsetY;

                javafx.scene.paint.Color pieceColor = getColorForPlayer(piece.getOwner());
                gc.setFill(pieceColor);
                gc.fillOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);

                javafx.geometry.Rectangle2D bounds = new javafx.geometry.Rectangle2D(
                        px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2
                );
                pieceBounds.put(piece, bounds);
            }
        }

        // [8] Off-board 말 그리기
        List<Player> players = game.getPlayers();
        int nPlayers = players.size();
        for (int idx = 0; idx < nPlayers; idx++) {
            Player player = players.get(idx);
            Point2D quadCenter = computeQuadrantPoint(idx, nPlayers);

            String playerName = player.getName();
            gc.setFill(Color.BLACK);
            gc.fillText(playerName,
                    quadCenter.getX() - playerName.length() * 3,
                    quadCenter.getY() - OFFBOARD_PIECE_RADIUS * 2 - OFFBOARD_GAP);

            int drawnCount = 0;
            for (Piece piece : player.getPieces()) {
                if (piece.getState() == PieceState.NOT_STARTED) {
                    int col = drawnCount % 2;
                    int row = drawnCount / 2;
                    double dx = (col == 0) ? -OFFBOARD_PIECE_RADIUS : OFFBOARD_PIECE_RADIUS;
                    double dy = (row == 0) ? -OFFBOARD_PIECE_RADIUS : OFFBOARD_PIECE_RADIUS;
                    double px = quadCenter.getX() + dx;
                    double py = quadCenter.getY() + dy;

                    javafx.scene.paint.Color pc = getColorForPlayer(player);
                    gc.setFill(pc);
                    gc.fillOval(px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                            OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2);
                    gc.setStroke(Color.BLACK);
                    gc.strokeOval(px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                            OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2);

                    javafx.geometry.Rectangle2D bounds = new javafx.geometry.Rectangle2D(
                            px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                            OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2
                    );
                    pieceBounds.put(piece, bounds);
                    drawnCount++;
                }
            }
        }
    }

    private Piece getPieceAtPoint(double x, double y) {
        for (Map.Entry<Piece, javafx.geometry.Rectangle2D> entry : pieceBounds.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Point2D computeQuadrantPoint(int idx, int nPlayers) {
        double qx = CENTER, qy = CENTER;
        switch (nPlayers) {
            case 2 -> {
                if (idx == 0) { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else          { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            case 3 -> {
                if (idx == 0) { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 1) { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            case 4 -> {
                if (idx == 0) { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 1) { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 2) { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
                else { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            default -> {}
        }
        return new Point2D(qx, qy);
    }

    private javafx.scene.paint.Color getColorForPlayer(Player player) {
        int idx = player.getId();
        return switch (idx % 4) {
            case 0 -> Color.BLUE;
            case 1 -> Color.GREEN;
            case 2 -> Color.ORANGE;
            case 3 -> Color.MAGENTA;
            default -> Color.BLACK;
        };
    }

    /** 단순 2D 좌표용 내부 클래스 */
    private static class Point2D {
        private final double x, y;
        public Point2D(double x, double y) { this.x = x; this.y = y; }
        public double getX() { return x; }
        public double getY() { return y; }
    }
}