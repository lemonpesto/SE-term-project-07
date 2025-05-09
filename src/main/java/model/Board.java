package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 다각형 모양의 윷놀이판 생성 (사각형, 오각형, 육각형만 지원)
 * 둘레는 각 변마다 꼭짓점 셀 1개와 일반 셀 4개로 구성되고
 * 각 꼭짓점에서 중앙 셀까지는 일반 셀 2개씩 연결됨
 */
public class Board {
    private final BoardShape shape;     // 판의 형태
    private final int sides;            // 변의 개수
    private final int cellsPerEdge = 4; // 꼭짓점 사이의 일반 셀 수
    private final int cellsToCenter = 2;// 꼭짓점과 중앙을 연결하는 일반 셀 수

    private final List<Cell> cells = new ArrayList<>();
    private final Cell startCell;
    /**
     * @param shape 판 형태 (SQUARE, PENTAGON, HEXAGON)
     */
    public Board(BoardShape shape) {
        this.shape = shape;
        switch (shape) { // 형태에 따른 변의 개수
            case SQUARE: this.sides = 4; break;
            case PENTAGON: this.sides = 5; break;
            case HEXAGON: this.sides = 6; break;
            default: this.sides = 4; break;
        }
        this.startCell = initBoard();
    }

    public List<Cell> getCells() {
        return cells;
    }

    /** 보드의 시작 셀 반환 */
    public Cell getStartCell() {
        return startCell;
    }

    /**
     * 보드를 초기화하고 셀 간 연결
     * @return 출발점(V0) 셀
     */
    private Cell initBoard() {
        cells.clear();
        List<Cell> perimeter = new ArrayList<>();

        /** 보드의 둘레 셀 생성: 꼭짓점 + 4개의 일반 셀 */
        for (int i = 0; i < sides; i++) {
            // 꼭짓점
            Cell vertex = new Cell("V" + i);    // 꼭짓점 id: V0, V1, ...
            cells.add(vertex);
            perimeter.add(vertex);
            // 일반 셀
            for (int j = 0; j < cellsPerEdge; j++) {
                Cell edge = new Cell("E" + i + "_" + j); // 일반 셀 id: E0_0, E0_1, ...
                cells.add(edge);
                perimeter.add(edge);
            }
        }

        /** 외곽 순환 연결 (V0->E0_1->...->E(sides-1)_4->V0) */
        int m = perimeter.size();
        for (int k = 0; k < m; k++) {
            Cell cur = perimeter.get(k);
            Cell nxt = perimeter.get((k + 1) % m);
            cur.getNextCells().add(nxt);
        }

        /** 중앙 셀 생성 */
        Cell center = new Cell("C");
        cells.add(center);

        /** 대각선 경로용 셀 생성 및 연결 ( V1..V(sides-2)만 C로 향함 ) */
        for (int i = 1; i < sides - 1; i++) {
            // 셀 생성
            Cell d0 = new Cell("D" + i + "_0");
            Cell d1 = new Cell("D" + i + "_1");
            cells.add(d0);
            cells.add(d1);
            // 연결
            Cell vertex = perimeter.get(i * (cellsPerEdge + 1)); // 꼭짓점 cell (V1, V2, ...)
            vertex.getNextCells().add(d0);
            d0.getNextCells().add(d1);
            d1.getNextCells().add(center);
        }

        /** 중앙 셀 --> 마지막 꼭짓점 방향의 셀 생성 및 연결*/
        Cell vFinal = perimeter.get((sides - 1) * (cellsPerEdge + 1)); // 마지막 꼭짓점 cell
        Cell df_0 = new Cell("D" + (sides - 1) + "_0");
        Cell df_1 = new Cell("D" + (sides - 1) + "_1");
        center.getNextCells().add(df_0);
        df_0.getNextCells().add(df_1);
        df_1.getNextCells().add(vFinal);

        /** 중앙 셀 --> 출발점 방향의 셀 생성 및 연결*/
        Cell vStart = perimeter.get(0); // 출발점
        Cell d0_0 = new Cell("D0_0");
        Cell d0_1 = new Cell("D0_1");
        center.getNextCells().add(d0_0);
        d0_0.getNextCells().add(d0_1);
        d0_1.getNextCells().add(vStart);

        /** 출발점 V0 반환 */
        return perimeter.get(0);
    }
}
