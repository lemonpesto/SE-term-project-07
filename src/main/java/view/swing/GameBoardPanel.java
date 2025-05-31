package view.swing;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// 윷판을 그릴 패널
public class GameBoardPanel extends JPanel {
    private final Board board;    // 윷판 정보를 가진 객체
    private final Game game;      // 게임 전반 정보를 가진 객체(플레이어+말 상태)

    // 판의 기본 사이즈(픽셀), 중앙 좌표, 보드 원형 반지름
    private static final int PANEL_SIZE = 520;
    private static final int CENTER = PANEL_SIZE / 2;
    private static final int RADIUS = 180;         // “보드” 반경

    // Cell(칸) 하나를 그릴 때 사용할 원 크기
    private static final int NODE_SIZE = 32;

    // ===== 말 그리기용 상수 =====
    private static final int PIECE_RADIUS = 10;          // 보드 위(Occupant) 말 반지름
    private static final int OFFBOARD_RADIUS = RADIUS + 40; // Off-board 말 배치 기준 반경
    private static final int OFFBOARD_PIECE_RADIUS = 10; // Off-board 말 반지름
    private static final int OFFBOARD_GAP = 4;           // Off-board 말들 간 간격

    // 클릭 시 말 찾기용: Piece --> 화면상의 영역(Rectangle)
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

        // 매번 새로 그릴 때마다 이전 말 Bounds 기록은 초기화
        pieceBounds.clear();

        int sides = board.getShape().getVertexCount();
        int cellsPerEdge = board.getCellsPerEdge();

        // [1] 꼭짓점 cell 좌표 계산
        Point[] vertexPoints = new Point[sides];
        Point[][] edgePoints = new Point[sides][cellsPerEdge];
        for (int i = 0; i < sides; i++) {
            double theta = Math.PI / 2 - 2 * Math.PI * i / sides; // 18시부터 반시계방향
            int x = (int)(CENTER + RADIUS * Math.cos(theta));
            int y = (int)(CENTER + RADIUS * Math.sin(theta));
            vertexPoints[i] = new Point(x, y);
        }

        // [2] edge cell 좌표 계산
        for (int i = 0; i < sides; i++) {
            Point from = vertexPoints[i];
            Point to = vertexPoints[(i+1)%sides];
            for (int j = 0; j < cellsPerEdge; j++) {
                double t = (double)(j + 1) / (cellsPerEdge + 1); // (1/5, 2/5, 3/5, 4/5)
                int ex = (int)(from.x * (1 - t) + to.x * t);
                int ey = (int)(from.y * (1 - t) + to.y * t);
                edgePoints[i][j] = new Point(ex, ey);
            }
        }

        // [3] 중앙 cell 좌표
        Point centerPoint = new Point(CENTER, CENTER);

        // [4] 대각 cell 좌표
        // D0, D(n-1)은 중앙-->꼭짓점 방향, D1~D(n-2)는 꼭짓점-->중앙 방향
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
                // 방향성에 따라 계산법 달라짐!
                double t;
                if (vi == 0 || vi == sides-1) {
                    // D0_0, D0_1, Dn-1_0, Dn-1_1 : 중앙→꼭짓점 방향
                    t = (dj == 0) ? 0.7 : 0.4; // Di_0이 더 바깥쪽
                    int x = (int)(v.x * (1-t) + centerPoint.x * t);
                    int y = (int)(v.y * (1-t) + centerPoint.y * t);
                    return new Point(x, y);
                } else {
                    // V1~Vn-2: 꼭짓점→중앙 방향
                    t = (dj == 0) ? 0.4 : 0.7; // Di_0이 더 꼭짓점 쪽
                    int x = (int)(v.x * (1-t) + centerPoint.x * t);
                    int y = (int)(v.y * (1-t) + centerPoint.y * t);
                    return new Point(x, y);
                }
            }
            return new Point(0,0); // 혹시나 예외
        };

        // [3] 모든 셀 연결선 그리기
        g.setColor(Color.LIGHT_GRAY);
        for (Cell cell : board.getAllCells()) {
            Point from = getPosition.apply(cell.getId());
            for (Cell next : cell.getNextCells()) {
                Point to = getPosition.apply(next.getId());
                g.drawLine(from.x, from.y, to.x, to.y);
            }
        }

        // [4] 모든 셀을 type별로 그리기
        for (Cell cell : board.getAllCells()) {
            Point p = getPosition.apply(cell.getId());

            // 테두리 지정 및 색상 채우기
            if (cell.getId().startsWith("V") || cell.getId().equals("C")) { // 특수 cell: 이중 테두리, 빨강
                // 바깥쪽 테두리
                g.setColor(Color.BLACK); // 예시
                g.drawOval(p.x - NODE_SIZE/2 - 5, p.y - NODE_SIZE/2 - 5, NODE_SIZE + 10, NODE_SIZE + 10);

                // 색상
                g.setColor(Color.RED);
                g.fillOval(p.x - NODE_SIZE/2 - 5, p.y - NODE_SIZE/2 - 5, NODE_SIZE + 10, NODE_SIZE + 10);

                // 안쪽 테두리
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_SIZE/2, p.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);

                // 출발점 셀 표시
                if(cell.isStartCell()){
                    g.setColor(Color.BLACK);
                    g.drawString("START", p.x - 20, p.y + 6);
                }
            }
            else{ // edge cell: 단일 테두리, 하양
                // 테두리
                g.setColor(Color.BLACK);
                g.drawOval(p.x - NODE_SIZE/2, p.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);

                // 색상
                g.setColor(Color.WHITE);
                g.fillOval(p.x - NODE_SIZE/2, p.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            }

            // 셀 id 출력
//            g.setColor(Color.BLACK);
//            g.drawString(cell.getId(), p.x - 12, p.y + 6);
        }

        // [7] Cell 위 Occupant(말) 그리기
        for (Cell cell : board.getAllCells()) {
            Point cellCenter = getPosition.apply(cell.getId());
            List<Piece> occupants = cell.getOccupants();
            if (occupants.isEmpty()) continue;

            int count = occupants.size();
            for (int i = 0; i < count; i++) {
                Piece piece = occupants.get(i);
                // 같은 Cell에 여러 말이 겹치지 않도록 사분면 식으로 배치
                int offsetX = ((i % 2) == 0) ? -PIECE_RADIUS : PIECE_RADIUS;
                int offsetY = ((i < 2) ? -PIECE_RADIUS : PIECE_RADIUS);
                int px = cellCenter.x + offsetX;
                int py = cellCenter.y + offsetY;

                Color pieceColor = getColorForPlayer(piece.getOwner());
                g.setColor(pieceColor);
                g.fillOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);

                // 클릭 범위 저장
                Rectangle bounds = new Rectangle(
                        px - PIECE_RADIUS, py - PIECE_RADIUS,
                        PIECE_RADIUS * 2, PIECE_RADIUS * 2);
                pieceBounds.put(piece, bounds);
            }
        }

        // [8] Off-board 말 그리기: “사분면 고정 위치”에 플레이어별로 모아서 배치
        List<Player> players = game.getPlayers();
        int nPlayers = players.size();

        for (int idx = 0; idx < nPlayers; idx++) {
            Player player = players.get(idx);
            // “idx”가 가리키는 사분면 계산
            //  2명 → idx=0→Ⅱ, idx=1→Ⅳ
            //  3명 → idx=0→Ⅱ, idx=1→Ⅰ, idx=2→Ⅳ
            //  4명 → idx=0→Ⅱ, idx=1→Ⅰ, idx=2→Ⅳ, idx=3→Ⅲ
            Point quadCenter = computeQuadrantPoint(idx, nPlayers);

            // “플레이어 이름”을 사분면의 중앙(조금 위) 에 그린다.
            String playerName = player.getName();
            FontMetrics fm = g.getFontMetrics();
            int nameWidth = fm.stringWidth(playerName);
            // 가운데 정렬: quadCenter.x - nameWidth/2, quadCenter.y - PIECE_RADIUS*2 - 8 정도로 배치
            int nameX = quadCenter.x - nameWidth / 2;
            int nameY = quadCenter.y - OFFBOARD_PIECE_RADIUS * 2 - OFFBOARD_GAP;
            g.setColor(Color.BLACK);
            g.drawString(playerName, nameX, nameY);

            // Off-board 상태(Not Started)인 말들 그리기
            List<Piece> pieces = player.getPieces();
            int drawnCount = 0;
            for (Piece piece : pieces) {
                if (piece.getState() == PieceState.NOT_STARTED) {
                    // idx번째 사분면의 quadCenter 근처에 뜨는 말 위치 계산
                    // 4개일 때는 quadCenter 주변에 2x2 격자로 배치
                    int col = drawnCount % 2;       // 0 또는 1
                    int row = drawnCount / 2;       // 0 또는 1
                    int dx = (col == 0) ? -OFFBOARD_PIECE_RADIUS : OFFBOARD_PIECE_RADIUS;
                    int dy = (row == 0) ? -OFFBOARD_PIECE_RADIUS : OFFBOARD_PIECE_RADIUS;
                    // 실제 화면 좌표
                    int px = quadCenter.x + dx;
                    int py = quadCenter.y + dy;

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

                    drawnCount++;
                }
            }
        }
    }

    /**
     * 클릭된 좌표(Point p)에 해당하는 Piece를 반환한다.
     * - p가 어느 말의 Bounding Box(Rectangle) 안에 들어오면 그 Piece를 반환
     * - 없으면 null
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
     * 플레이어 인덱스(idx)와 전체 플레이어 수(nPlayers)에 따라
     * Off-board 말들을 놓을 “사분면 중심 좌표”를 반환한다.
     *
     * 사분면 분배 기준:
     *  - nPlayers == 2: idx=0 → II사분면, idx=1 → Ⅳ사분면
     *  - nPlayers == 3: idx=0 → II, idx=1 → Ⅰ, idx=2 → Ⅳ
     *  - nPlayers == 4: idx=0 → II, idx=1 → Ⅰ, idx=2 → Ⅳ, idx=3 → Ⅲ
     *
     * 사분면별 중심 좌표 계산:
     *  - I사분면: (CENTER + OFFBOARD_RADIUS, CENTER - OFFBOARD_RADIUS)
     *  - II:       (CENTER - OFFBOARD_RADIUS, CENTER - OFFBOARD_RADIUS)
     *  - III:      (CENTER - OFFBOARD_RADIUS, CENTER + OFFBOARD_RADIUS)
     *  - IV:       (CENTER + OFFBOARD_RADIUS, CENTER + OFFBOARD_RADIUS)
     */
    private Point computeQuadrantPoint(int idx, int nPlayers) {
        // 2명, 3명, 4명만 처리(1명일 때는 CENTER)
        int qx = 0, qy = 0;
        switch (nPlayers) {
            case 2:
                // idx=0 → II, idx=1 → Ⅳ
                if (idx == 0) {
                    qx = CENTER - OFFBOARD_RADIUS;
                    qy = CENTER - OFFBOARD_RADIUS;
                } else {
                    qx = CENTER + OFFBOARD_RADIUS;
                    qy = CENTER + OFFBOARD_RADIUS;
                }
                break;
            case 3:
                // idx=0→II, idx=1→I, idx=2→Ⅳ
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
                // idx=0→II, idx=1→I, idx=2→Ⅳ, idx=3→III
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
                // 1명 혹은 그 외(예: 5명 이상)인 경우, 그냥 CENTER 사용
                qx = CENTER;
                qy = CENTER;
                break;
        }
        return new Point(qx, qy);
    }

    /**
     * 플레이어(Player) 객체의 인덱스를 기준으로 말 색을 정한다.
     * 예) playerIndex 0→BLUE, 1→GREEN, 2→ORANGE, 3→MAGENTA, 나머지→BLACK
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

