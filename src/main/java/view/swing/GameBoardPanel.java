package view.swing;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static model.PieceState.NOT_STARTED;

/**
 * GameBoardPanel
 * : 보드, 셀, 현재 위치의 피스를 그림
 */
public class GameBoardPanel extends JPanel {
    private final Board board;
    private final Game game;      // 게임 정보 (플레이어, 말 상태 정보 필요)

    // 보드 기본 사이즈, 중앙 좌표, 보드 반지름
    private static final int PANEL_SIZE = 520;
    private static final int CENTER = PANEL_SIZE / 2;
    private static final int RADIUS = 180;

    // 셀 크기
    private static final int NODE_SIZE = 32;

    // 피스 관련 상수
    private static final int PIECE_RADIUS = 10;             // On-board 피스 반지름
    private static final int OFFBOARD_RADIUS = RADIUS + 40; // Off-board 배치 기준 반경
    private static final int OFFBOARD_PIECE_RADIUS = 10;    // Off-board 피스 반지름
    private static final int OVERLAP_GAP = 12;             // 피스 간 겹침 픽셀 수

    // 클릭 시 피스 찾기용: Piece --> 화면상의 Rectangle
    private final Map<Piece, Rectangle> pieceBounds = new HashMap<>();

    public GameBoardPanel(Board board, Game game) {
        this.board = board;
        this.game = game;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 새로 그릴 때마다 이전 피스 Bounds 초기화
        pieceBounds.clear();

        int sides = board.getShape().getVertexCount();
        int cellsPerEdge = board.getCellsPerEdge();

        // --- 1) 꼭짓점 좌표 계산 --- //
        Point[] vertexPoints = new Point[sides];
        for (int i = 0; i < sides; i++) {
            double theta = Math.PI / 2 - 2 * Math.PI * i / sides; // 12시부터 반시계
            int x = (int) (CENTER + RADIUS * Math.cos(theta));
            int y = (int) (CENTER + RADIUS * Math.sin(theta));
            vertexPoints[i] = new Point(x, y);
        }

        // --- 2) edge 셀 좌표 계산 --- //
        Point[][] edgePoints = new Point[sides][cellsPerEdge];
        for (int i = 0; i < sides; i++) {
            Point from = vertexPoints[i];
            Point to = vertexPoints[(i + 1) % sides];
            for (int j = 0; j < cellsPerEdge; j++) {
                double t = (double) (j + 1) / (cellsPerEdge + 1);
                int ex = (int) (from.x * (1 - t) + to.x * t);
                int ey = (int) (from.y * (1 - t) + to.y * t);
                edgePoints[i][j] = new Point(ex, ey);
            }
        }

        // --- 3) 중앙 셀 좌표 --- //
        Point centerPoint = new Point(CENTER, CENTER);

        // --- 4) 대각선 셀 좌표 계산 함수 --- //
        java.util.function.Function<String, Point> getPosition = (id) -> {
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
                Point v = vertexPoints[vi];
                double t;
                if (vi == 0 || vi == sides - 1) {
                    // D0_0, D0_1, Dn-1_0, Dn-1_1 : 중앙 --> 꼭짓점 방향
                    t = (dj == 0) ? 0.7 : 0.4;
                    int x = (int) (v.x * (1 - t) + centerPoint.x * t);
                    int y = (int) (v.y * (1 - t) + centerPoint.y * t);
                    return new Point(x, y);
                } else {
                    // V1~Vn-2: 꼭짓점 --> 중앙 방향
                    t = (dj == 0) ? 0.4 : 0.7;
                    int x = (int) (v.x * (1 - t) + centerPoint.x * t);
                    int y = (int) (v.y * (1 - t) + centerPoint.y * t);
                    return new Point(x, y);
                }
            }
            return new Point(0, 0); // 예외 처리용
        };

        // --- 5) 모든 셀 연결선 그리기 --- //
        g.setColor(Color.LIGHT_GRAY);
        for (Cell cell : board.getAllCells()) {
            Point from = getPosition.apply(cell.getId());
            for (Cell next : cell.getNextCells()) {
                Point to = getPosition.apply(next.getId());
                g.drawLine(from.x, from.y, to.x, to.y);
            }
        }

        // --- 6) 모든 셀 테두리 및 배경 그리기 --- //
        for (Cell cell : board.getAllCells()) {
            Point p = getPosition.apply(cell.getId());
            if (cell.getId().startsWith("V") || cell.getId().equals("C")) {
                // 특수 셀: 이중 테두리, 빨강 채우기
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_SIZE / 2 - 5, p.y - NODE_SIZE / 2 - 5, NODE_SIZE + 10, NODE_SIZE + 10);
                g.setColor(Color.RED);
                g.fillOval(p.x - NODE_SIZE / 2 - 5, p.y - NODE_SIZE / 2 - 5, NODE_SIZE + 10, NODE_SIZE + 10);
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_SIZE / 2, p.y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);

                if (cell.isStartCell()) {
                    g.setColor(Color.BLACK);
                    g.drawString("START", p.x - 20, p.y + 6);
                }
            } else {
                // 일반 셀: 단일 테두리, 흰색 채우기
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_SIZE / 2, p.y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
                g.setColor(Color.WHITE);
                g.fillOval(p.x - NODE_SIZE / 2, p.y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
            }
        }

        // --- 7) On-board 말 그리기 --- //
        for (Cell cell : board.getAllCells()) {
            Point cellCenter = getPosition.apply(cell.getId());
            List<Piece> occupants = cell.getOccupants();
            if (occupants.isEmpty()) continue;

            // 셀 위에 있는 피스 개수
            int count = occupants.size();
            for (int i = 0; i < count; i++) {
                Piece piece = occupants.get(i);
                // 출발점이면서 아직 NOT_STARTED인 말은 그리지 않음
                if (cell.isStartCell() && piece.getState() == NOT_STARTED) {
                    continue;
                }

                // count개의 피스를 일렬로 그리기(가운데 정렬)
                double baseOffset = (2 * PIECE_RADIUS - OVERLAP_GAP);
                double offsetX_double = (i - (count - 1) / 2.0) * baseOffset;
                int offsetX = (int) Math.round(offsetX_double);
                int px = cellCenter.x + offsetX;
                int py = cellCenter.y; // y는 중앙

                Color pieceColor = getColorForPlayer(piece.getOwner());
                g.setColor(pieceColor);
                g.fillOval(px - PIECE_RADIUS, py - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(px - PIECE_RADIUS, py - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);

                // 클릭 범위 저장
                Rectangle bounds = new Rectangle(px - PIECE_RADIUS, py - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                pieceBounds.put(piece, bounds);
            }
        }

        // --- 8) Off-board 말 그리기 --- //
        List<Player> players = game.getPlayers();
        int numPlayers = players.size();

        for (int idx = 0; idx < numPlayers; idx++) {
            Player player = players.get(idx);
            Point quadCenter = computeQuadrantPoint(idx, numPlayers);

            // 플레이어 이름 그리기(Off-board 말 위쪽)
            String playerName = player.getName();
            FontMetrics fm = g.getFontMetrics();
            int nameWidth = fm.stringWidth(playerName);
            int nameX = quadCenter.x - nameWidth / 2;
            int nameY = quadCenter.y - OFFBOARD_PIECE_RADIUS * 2 - 2;
            g.setColor(Color.BLACK);
            g.drawString(playerName, nameX, nameY);

            // Off-board 상태(Not Started)인 말들 가져오기
            List<Piece> pieces = player.getPieces();
            int count = 0;
            for (Piece piece : pieces) {
                if (piece.getState() == NOT_STARTED) {
                    count++;
                }
            }
            if (count == 0) continue;

            // 실제 Off-board 말들 그리기
            int drawnIndex = 0;
            for (Piece piece : pieces) {
                if (piece.getState() != NOT_STARTED) continue;

                double baseOffset = (2 * OFFBOARD_PIECE_RADIUS - OVERLAP_GAP);
                double offsetX_double = (drawnIndex - (count - 1) / 2.0) * baseOffset;
                int offsetX = (int) Math.round(offsetX_double);
                int px = quadCenter.x + offsetX;
                int py = quadCenter.y;

                Color pieceColor = getColorForPlayer(player);
                g.setColor(pieceColor);
                g.fillOval(px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2);

                // 클릭 범위 저장
                Rectangle bounds = new Rectangle(
                        px - OFFBOARD_PIECE_RADIUS, py - OFFBOARD_PIECE_RADIUS,
                        OFFBOARD_PIECE_RADIUS * 2, OFFBOARD_PIECE_RADIUS * 2);
                pieceBounds.put(piece, bounds);

                drawnIndex++;
            }
        }
    }

    /**
     * 클릭된 좌표 p에 해당하는 Piece 반환함
     * -- p가 어느 말의 Bounding Box 안에 들어오면 그 Piece를 반환
     * -- 없으면 null
     */
    public Piece getPieceAtPoint(Point p) {
        for (Map.Entry<Piece, Rectangle> entry : pieceBounds.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 플레이어 인덱스와 전체 플레이어 수에 따라 Off-board 말들을 놓을 위치를 반환함
     * -- 2명: 제2, 4 사분면
     * -- 3명: 제1, 2, 4사분면
     * -- 4명: 제1, 2, 3, 4사분면
     */
    private Point computeQuadrantPoint(int idx, int numPlayers) {
        int qx = 0, qy = 0;
        switch (numPlayers) {
            case 2:
                if (idx == 0) {
                    qx = CENTER - OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER + OFFBOARD_RADIUS;
                }
                break;
            case 3:
                if (idx == 0) {
                    qx = CENTER - OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else if (idx == 1) {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER + OFFBOARD_RADIUS;
                }
                break;
            case 4:
                if (idx == 0) {
                    qx = CENTER - OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else if (idx == 1) {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else if (idx == 2) {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER + OFFBOARD_RADIUS;
                } else {
                    qx = CENTER - OFFBOARD_RADIUS;
                    qy = CENTER + OFFBOARD_RADIUS;
                }
                break;
            default:
                qx = CENTER;
                qy = CENTER;
                break;
        }
        return new Point(qx, qy);
    }

    /**
     * 플레이어 객체의 인덱스를 기준으로 피스 색을 정함
     */
    private Color getColorForPlayer(Player player) {
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
