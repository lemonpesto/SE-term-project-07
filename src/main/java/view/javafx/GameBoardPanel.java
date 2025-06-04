package view.javafx;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.Board;
import model.Cell;
import model.Game;
import model.Piece;
import model.PieceState;
import model.Player;
import view.IGameViewListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * GameBoardPanel
 *
 * - Canvas 위에 “보드판(라인+셀)”과 “칸 위 Occupant(말)”을 그립니다.
 * - 사용자가 말(Piece)을 클릭하면 등록된 IGameViewListener.onPieceClicked(piece)를 호출합니다.
 * - JavaFXGameView 에서 이 패널을 중앙에 배치하고,
 *   updateBoard() 호출 시 drawBoard()만 실행하면 전체 보드가 갱신됩니다.
 */
public class GameBoardPanel extends Pane {

    private final Game game;
    private final Board board;
    private Canvas canvas;
    private IGameViewListener listener;

    // Piece → 화면상의 클릭 범위(Rectangle2D) 매핑
    private final Map<Piece, Rectangle2D> pieceBounds = new HashMap<>();

    // ─── 상수 (보드 크기·위치 계산용) ───────────────────────────────────────────
    private static final double CANVAS_SIZE = 520.0;
    private static final double CENTER = CANVAS_SIZE / 2.0;
    private static final double RADIUS = 180.0;
    private static final double NODE_SIZE = 32.0;
    private static final double PIECE_RADIUS = 10.0;
    private static final double OFFBOARD_RADIUS = RADIUS + 40.0;
    private static final double OFFBOARD_PIECE_RADIUS = 10.0;
    private static final double OFFBOARD_GAP = 4.0;
    private static final double OVERLAP_GAP = 12.0;
    // ────────────────────────────────────────────────────────────────────────────

    public GameBoardPanel(Board board, Game game) {
        this.game = game;
        this.board = game.getBoard();

        // 1) Canvas 생성
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        getChildren().add(canvas);

        // 2) 마우스 클릭 핸들러: 클릭한 좌표에 말이 있으면 listener.onPieceClicked(piece) 호출
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (listener == null) return;
            double x = e.getX();
            double y = e.getY();
            Piece clicked = getPieceAt(x, y);
            if (clicked != null) {
                listener.onPieceClicked(clicked);
            }
        });

        // 3) Pane 크기 고정
        setPrefSize(CANVAS_SIZE, CANVAS_SIZE);
        setMinSize(CANVAS_SIZE, CANVAS_SIZE);
        setMaxSize(CANVAS_SIZE, CANVAS_SIZE);

        // 처음 한 번 그려둡니다
        drawBoard();
    }

    /**
     * IGameViewListener 를 등록합니다.
     * 보드판 위 말 클릭 시 onPieceClicked(piece) 가 호출됩니다.
     */
    public void setGameViewListener(IGameViewListener listener) {
        this.listener = listener;
    }

    /**
     * 전체 보드(라인+셀+말+off-board 말)를 다시 그립니다.
     * JavaFX UI 스레드에서 호출되어야 하므로, 외부에서 updateBoard()를 통해 호출하세요.
     */
    public void drawBoard() {
        if (canvas == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        pieceBounds.clear();

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

        // [4] getPosition: cellId → 화면 좌표 매핑 함수
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

        // [5] 셀 간 연결선 그리기
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
                // 꼭짓점(C 또는 V) 셀: 이중 테두리 + 빨강 채워 넣기
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
                // 일반 edge 셀: 흰색 채우기 + 검정 테두리
                gc.setFill(Color.WHITE);
                gc.fillOval(cx - NODE_SIZE / 2, cy - NODE_SIZE / 2,
                        NODE_SIZE, NODE_SIZE);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(cx - NODE_SIZE / 2, cy - NODE_SIZE / 2,
                        NODE_SIZE, NODE_SIZE);
            }
        }

        // [7] Cell 위 Occupant(말) 그리기 + 클릭 범위 저장 (한 줄 가운데 정렬)
        for (Cell cell : board.getAllCells()) {
            Point2D cellCenter = getPosition.apply(cell.getId());
            List<Piece> occupants = cell.getOccupants();
            if (occupants.isEmpty()) continue;

            int count = occupants.size();
            for (int i = 0; i < count; i++) {
                Piece piece = occupants.get(i);

                // 출발점 셀 위에서 아직 시작하지 않은 말은 건너뜀
                if (cell.isStartCell() && piece.getState() == PieceState.NOT_STARTED) {
                    continue;
                }
                if (piece.getState() != PieceState.ON_BOARD) continue;

                // count개의 피스를 일렬로 그리기(가운데 정렬)
                double baseOffset = (2 * PIECE_RADIUS - OVERLAP_GAP);
                double offsetX_double = (i - (count - 1) / 2.0) * baseOffset;
                double px = cellCenter.getX() + offsetX_double;
                double py = cellCenter.getY(); // y는 중앙

                // 플레이어별 색 결정
                javafx.scene.paint.Color pieceColor = getColorForPlayer(piece.getOwner());
                gc.setFill(pieceColor);
                gc.fillOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);

                // 클릭 범위 저장
                Rectangle2D bounds = new Rectangle2D(
                        px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2
                );
                pieceBounds.put(piece, bounds);
            }
        }

        // [8] Off-board 말 그리기 (각 플레이어별 사분면 위치)
        List<Player> players = game.getPlayers();
        int nPlayers = players.size();
        for (int idx = 0; idx < nPlayers; idx++) {
            Player player = players.get(idx);
            Point2D quadCenter = computeQuadrantPoint(idx, nPlayers);

            // 플레이어 이름
            String playerName = player.getName();
            gc.setFill(Color.BLACK);
            gc.fillText(playerName,
                    quadCenter.getX() - playerName.length() * 3,
                    quadCenter.getY() - OFFBOARD_PIECE_RADIUS * 2 - OFFBOARD_GAP);

            // Off-board(아직 시작 안 했거나 잡혀서 돌아온) 말 표시
            // 2) Off‐board(아직 시작 안 했거나 잡힌) 말들을 수집
            List<Piece> offboardPieces = player.getPieces().stream()
                    .filter(p -> p.getState() == PieceState.NOT_STARTED)
                    .toList();

            int count = offboardPieces.size();
            if (count == 0) continue;

            // 3) 한 줄로 가운데 정렬할 때 사용할 간격 계산
            double baseOffsetOff = (2 * OFFBOARD_PIECE_RADIUS - OVERLAP_GAP);

            for (int i = 0; i < count; i++) {
                Piece piece = offboardPieces.get(i);

                // 3‐1) 한 줄로 가운데 정렬: (i - (count-1)/2) * baseOffsetOff
                double offsetX_double = (i - (count - 1) / 2.0) * baseOffsetOff;
                double px = quadCenter.getX() + offsetX_double;
                double py = quadCenter.getY(); // 수직(centerY)는 그대로 유지

                // 3‐2) 말 색상 결정
                javafx.scene.paint.Color pc = getColorForPlayer(player);
                gc.setFill(pc);
                gc.fillOval(
                        px - OFFBOARD_PIECE_RADIUS,
                        py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2,
                        OFFBOARD_PIECE_RADIUS * 2
                );
                gc.setStroke(Color.BLACK);
                gc.strokeOval(
                        px - OFFBOARD_PIECE_RADIUS,
                        py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2,
                        OFFBOARD_PIECE_RADIUS * 2
                );

                // 3‐3) 클릭 영역 저장
                Rectangle2D bounds = new Rectangle2D(
                        px - OFFBOARD_PIECE_RADIUS,
                        py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2,
                        OFFBOARD_PIECE_RADIUS * 2
                );
                pieceBounds.put(piece, bounds);
            }
        }
    }

    /**
     * JavaFX UI 스레드가 아닌 곳에서 호출 시, 내부적으로 Platform.runLater를 통해
     * drawBoard()를 호출합니다.
     */
    public void updateBoard() {
        if (Platform.isFxApplicationThread()) {
            drawBoard();
        } else {
            Platform.runLater(this::drawBoard);
        }
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 화면상의 (x,y) 좌표에 해당하는 Piece가 있으면 반환, 없으면 null
    private Piece getPieceAt(double x, double y) {
        for (Map.Entry<Piece, Rectangle2D> entry : pieceBounds.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 플레이어별 사분면 중심 좌표 계산
    private Point2D computeQuadrantPoint(int idx, int nPlayers) {
        double qx = CENTER, qy = CENTER;
        switch (nPlayers) {
            case 2 -> {
                if (idx == 0) { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else          { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            case 3 -> {
                if (idx == 0)       { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 1)  { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else                { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            case 4 -> {
                if (idx == 0)       { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 1)  { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER - OFFBOARD_RADIUS; }
                else if (idx == 2)  { qx = CENTER + OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
                else                { qx = CENTER - OFFBOARD_RADIUS; qy = CENTER + OFFBOARD_RADIUS; }
            }
            default -> { /* 1명 혹은 5명 이상은 중앙 표시 */ }
        }
        return new Point2D(qx, qy);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 플레이어별 색상 결정 (0→파랑, 1→초록, 2→주황, 3→자홍, 그 외→검정)
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
}