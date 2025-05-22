package view.swing;

import model.Board;
import model.Cell;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 윷판을 그릴 패널
public class GameBoardPanel extends JPanel {
    private final Board board;    // 윷판 정보를 가진 객체

    // 판의 기본 사이즈(화면 크기), 중앙, 반지름, ...
    private static final int PANEL_SIZE = 520;
    private static final int CENTER = PANEL_SIZE / 2;
    private static final int RADIUS = 180;        // 꼭짓점 원의 반지름
    private static final int NODE_SIZE = 32;      // 셀(원) 크기

    public GameBoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBackground(Color.WHITE);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
    }
}
