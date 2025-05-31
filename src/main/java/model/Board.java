package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 다각형 모양의 윷놀이판 생성 (사각형, 오각형, 육각형만 지원)
 * 둘레는 각 변마다 꼭짓점 셀 1개와 일반 셀 4개로 구성되고
 * 각 꼭짓점에서 중앙 셀까지는 일반 셀 2개씩 연결됨
 */
public class Board {
    private final BoardShape shape;     // 판의 형태
    private final int sides;
    private final int cellsPerEdge = 4; // 꼭짓점 사이의 일반 셀 수

    private final List<Cell> cells;
    private final Map<String, Cell> cellMap;
    private final Cell startCell; // V0
    private final Cell centerCell;

    // Game이 생성될 때 한 번만 세팅해 주는 플레이어 순서 리스트

    /**
     * @param shape 판 형태 (SQUARE, PENTAGON, HEXAGON)
     */
    public Board(BoardShape shape) {
        this.shape = shape;
        this.sides = shape.getVertexCount();
        this.cells = new ArrayList<>();
        this.cellMap = new HashMap<>();

        this.startCell = initBoard();
        this.centerCell = cellMap.get("C");
    }

    /**
     * 보드를 초기화하고 셀 간 연결
     * @return 출발점(V0) 셀
     */
    private Cell initBoard() {
        cells.clear();
        cellMap.clear();
        List<Cell> perimeter = new ArrayList<>();

        // [1] 보드의 둘레 셀 생성: 꼭짓점 + 4개의 일반 셀
        for (int i = 0; i < sides; i++) {
            // 꼭짓점
            Cell vertex = new Cell("V" + i);    // 꼭짓점 id: V0, V1, ...
            cells.add(vertex);
            cellMap.put(vertex.getId(), vertex);
            perimeter.add(vertex);

            // edge 셀
            for (int j = 0; j < cellsPerEdge; j++) {
                Cell edge = new Cell("E" + i + "_" + j); // 일반 셀 id: E0_0, E0_1, ...
                cells.add(edge);
                cellMap.put(edge.getId(), edge);
                perimeter.add(edge);
            }
        }

        // [2] 외곽 순환 연결 (perimeter를 따라 한 바퀴: V0->E0_1->...->E(sides-1)_4->V0) */
        int m = perimeter.size();
        for (int k = 0; k < m; k++) {
            Cell cur = perimeter.get(k);
            Cell nxt = perimeter.get((k + 1) % m);
            cur.addNextCell(nxt);
        }

        // [3] 중앙 셀 생성
        Cell center = new Cell("C");
        cells.add(center);
        cellMap.put(center.getId(), center);

        // [4] 대각 셀 생성 및 연결 (V1..V(sides-2) --> C)
        for (int i = 1; i < sides - 1; i++) {
            // 셀 생성
            Cell d0 = new Cell("D" + i + "_0");
            Cell d1 = new Cell("D" + i + "_1");
            cells.add(d0);
            cells.add(d1);
            cellMap.put(d0.getId(), d0);
            cellMap.put(d1.getId(), d1);

            // 연결
            Cell vertex = perimeter.get(i * (cellsPerEdge + 1)); // 꼭짓점 cell (V1, V2, ...)
            vertex.addNextCell(d0);
            d0.addNextCell(d1);
            d1.addNextCell(center);
        }

        // [5] 마지막 꼭짓점에 대한 대각 셀 생성 및 연결 (C --> V(sides-1))
        Cell vFinal = perimeter.get((sides - 1) * (cellsPerEdge + 1)); // 마지막 꼭짓점 cell
        Cell df_0 = new Cell("D" + (sides - 1) + "_0");
        Cell df_1 = new Cell("D" + (sides - 1) + "_1");
        cells.add(df_0);
        cells.add(df_1);
        cellMap.put(df_0.getId(), df_0);
        cellMap.put(df_1.getId(), df_1);

        center.addNextCell(df_0);
        df_0.addNextCell(df_1);
        df_1.addNextCell(vFinal);

        // [6] 출발점에 대한 대각 셀 생성 및 연결 (C --> V0)
        Cell vStart = perimeter.get(0); // 출발점
        Cell d0_0 = new Cell("D0_0");
        Cell d0_1 = new Cell("D0_1");
        cells.add(d0_0);
        cells.add(d0_1);
        cellMap.put(d0_0.getId(), d0_0);
        cellMap.put(d0_1.getId(), d0_1);

        center.addNextCell(d0_0);    // C --> D0_0
        d0_0.addNextCell(d0_1);      // D0_0 --> D0_1
        d0_1.addNextCell(vStart);    // D0_1 --> V0

        // 출발점 V0 반환
        return vStart;
    }

    // ======= 헬퍼 메소드 =======
    public Cell getCellById(String id) { return cellMap.get(id); }
    public Cell getStartCell() { return startCell; }
    public Cell getCenterCell() { return centerCell; }
    public List<Cell> getAllCells() { return cells; }
    public BoardShape getShape() { return shape; }

    // java swing
    public int getCellsPerEdge() { return cellsPerEdge;}
}
