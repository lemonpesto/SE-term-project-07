package view.swing;

import model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 윷판을 그릴 패널
public class GameBoardPanel extends JPanel {
    private final Board board;    // 윷판 정보를 가진 객체 (네가 만든 model.Board)

    // 패널(=그림판)의 크기, 윷판의 반지름, 각 셀의 크기 설정
    private final int PANEL_SIZE = 520;
    private final int CENTER = PANEL_SIZE / 2;
    private final int RADIUS = 180;
    private final int NODE_SIZE = 32;

    // 생성자: Board 객체 받아서 저장
    public GameBoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE)); // 패널 크기 지정
    }

    // 화면 그릴 때 자동 호출! (절대 직접 부르지 않음)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Swing 내부 기본 작업

        // 윷판 정보를 바탕으로 셀(원), 연결선(선), 셀 이름(글자)을 그림

        // [1] 꼭짓점 위치 계산 (다각형 모양)
        int sides = board.getShape().getVertexCount();    // 꼭짓점 개수
        int cellsPerEdge = board.getCellsPerEdge();       // 변 하나에 edge cell 몇 개

        Point[] vertexPoints = new Point[sides];
        for (int i = 0; i < sides; i++) {
            double theta = 2 * Math.PI * i / sides - Math.PI / 2; // 각도 계산 (12시 방향 시작)
            int x = (int)(CENTER + RADIUS * Math.cos(theta));
            int y = (int)(CENTER + RADIUS * Math.sin(theta));
            vertexPoints[i] = new Point(x, y); // 꼭짓점 좌표 저장
        }

        // [2] 꼭짓점과 edge cell을 perimeterPoints에 저장 (순서대로 원 그릴 준비)
        Point[] perimeterPoints = new Point[sides * (cellsPerEdge + 1)];
        int idx = 0;
        for (int i = 0; i < sides; i++) {
            Point from = vertexPoints[i];
            Point to = vertexPoints[(i+1)%sides]; // 다음 꼭짓점(순환)
            perimeterPoints[idx++] = from;
            for (int j = 1; j <= cellsPerEdge; j++) {
                double t = (double)j / (cellsPerEdge + 1); // 선분 위 분할점 위치
                int x = (int)(from.x * (1 - t) + to.x * t);
                int y = (int)(from.y * (1 - t) + to.y * t);
                perimeterPoints[idx++] = new Point(x, y);
            }
        }

        // [3] 중앙점 좌표
        Point centerPoint = new Point(CENTER, CENTER);

        // [4] 외곽 연결선 그리기 (perimeter)
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < perimeterPoints.length; i++) {
            Point from = perimeterPoints[i];
            Point to = perimeterPoints[(i+1) % perimeterPoints.length];
            g.drawLine(from.x, from.y, to.x, to.y);
        }

        // [5] perimeter 셀 그리기 (꼭짓점=파란색, edge=회색)
        idx = 0;
        for (int i = 0; i < sides; i++) {
            // 꼭짓점 원 (파란색)
            Point v = perimeterPoints[idx++];
            g.setColor(Color.BLUE);
            g.fillOval(v.x-NODE_SIZE/2, v.y-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.setColor(Color.WHITE);
            g.drawString("V"+i, v.x-8, v.y+5);

            // edge 셀 원 (짙은 회색)
            for (int j = 0; j < cellsPerEdge; j++) {
                Point e = perimeterPoints[idx++];
                g.setColor(Color.DARK_GRAY);
                g.fillOval(e.x-NODE_SIZE/2, e.y-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
                g.setColor(Color.WHITE);
                g.drawString("E"+i+"_"+j, e.x-14, e.y+5);
            }
        }

        // [6] 중앙 셀 그리기 (빨간색)
        g.setColor(Color.RED);
        g.fillOval(centerPoint.x-NODE_SIZE/2, centerPoint.y-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
        g.setColor(Color.WHITE);
        g.drawString("C", centerPoint.x-6, centerPoint.y+5);

        // [7] 대각선(D) 셀/연결
        // (여기서부터가 아까 방향 문제가 있던 파트!)
        for (int i = 1; i < sides-1; i++) {
            // V1~Vn-2에서 중앙으로 들어오는 대각선
            Point from = perimeterPoints[i * (cellsPerEdge+1)];
            double t0 = 0.4, t1 = 0.7;
            int x0 = (int)(from.x * (1 - t0) + centerPoint.x * t0);
            int y0 = (int)(from.y * (1 - t0) + centerPoint.y * t0);
            int x1 = (int)(from.x * (1 - t1) + centerPoint.x * t1);
            int y1 = (int)(from.y * (1 - t1) + centerPoint.y * t1);

            g.setColor(Color.MAGENTA);
            g.drawLine(from.x, from.y, x0, y0);
            g.drawLine(x0, y0, x1, y1);
            g.drawLine(x1, y1, centerPoint.x, centerPoint.y);

            g.fillOval(x0-NODE_SIZE/2, y0-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.fillOval(x1-NODE_SIZE/2, y1-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.setColor(Color.WHITE);
            g.drawString("D"+i+"_0", x0-16, y0+5);
            g.drawString("D"+i+"_1", x1-16, y1+5);
        }
        // 중앙→마지막 꼭짓점
        {
            int last = sides-1;
            Point to = perimeterPoints[last * (cellsPerEdge+1)];
            double t0 = 0.4, t1 = 0.7;
            int x0 = (int)(to.x * (1 - t1) + centerPoint.x * t1);
            int y0 = (int)(to.y * (1 - t1) + centerPoint.y * t1);
            int x1 = (int)(to.x * (1 - t0) + centerPoint.x * t0);
            int y1 = (int)(to.y * (1 - t0) + centerPoint.y * t0);

            g.setColor(Color.MAGENTA);
            g.drawLine(centerPoint.x, centerPoint.y, x1, y1);
            g.drawLine(x1, y1, x0, y0);
            g.drawLine(x0, y0, to.x, to.y);

            g.fillOval(x1-NODE_SIZE/2, y1-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.fillOval(x0-NODE_SIZE/2, y0-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.setColor(Color.WHITE);
            g.drawString("D"+last+"_0", x1-16, y1+5);
            g.drawString("D"+last+"_1", x0-16, y0+5);
        }
        // 중앙→출발점
        {
            Point to = perimeterPoints[0];
            double t0 = 0.4, t1 = 0.7;
            int x0 = (int)(to.x * (1 - t1) + centerPoint.x * t1);
            int y0 = (int)(to.y * (1 - t1) + centerPoint.y * t1);
            int x1 = (int)(to.x * (1 - t0) + centerPoint.x * t0);
            int y1 = (int)(to.y * (1 - t0) + centerPoint.y * t0);

            g.setColor(Color.MAGENTA);
            g.drawLine(centerPoint.x, centerPoint.y, x1, y1);
            g.drawLine(x1, y1, x0, y0);
            g.drawLine(x0, y0, to.x, to.y);

            g.fillOval(x1-NODE_SIZE/2, y1-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.fillOval(x0-NODE_SIZE/2, y0-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            g.setColor(Color.WHITE);
            g.drawString("D0_0", x1-16, y1+5);
            g.drawString("D0_1", x0-16, y0+5);
        }
    }
}
