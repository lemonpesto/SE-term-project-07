package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 다각형 모양의 윷놀이판 생성
 * 둘레는 각 변마다 꼭짓점 셀 1개와 일반 셀 4개로 구성되고
 * 각 꼭짓점에서 중앙 셀까지는 일반 셀 2개씩 연결됨
 */
public class Board {
    private final int sides;               // 다각형 변의 개수(n)
    private final int cellsPerEdge = 4; // 변당 일반 셀 개수
    private final int cellsToCenter = 2;   // 중심 연결 시 일반 셀 개수
    private final List<Cell> cells = new ArrayList<>();

    /**
     * n각형 보드 생성
     * @param sides 변의 개수 (3 이상)
     */
    public Board(int sides) {
        this.sides = sides;
        initBoard();
    }

    public List<Cell> getCells() {
        return cells;
    }

    /**
     * 보드를 초기화하고 셀 간 연결을 설정합니다.
     */
    private void initBoard() {
        cells.clear();
        // 1) 둘레(perimeter) 셀 생성 및 순환 연결
        List<Cell> perimeter = new ArrayList<>();
        for (int i = 0; i < sides; i++) {
            // 꼭짓점 셀
            Cell vertex = new Cell("V" + i);
            cells.add(vertex);
            perimeter.add(vertex);
            // 변당 일반 셀 생성
            for (int j = 1; j <= cellsPerEdge; j++) {
                Cell edge = new Cell("E" + i + "_" + j);
                cells.add(edge);
                perimeter.add(edge);
            }
        }
        // 순환 연결
        int m = perimeter.size();
        for (int k = 0; k < m; k++) {
            Cell curr = perimeter.get(k);
            curr.getNextCells().add(perimeter.get((k + 1) % m));
        }

        // 2) 중앙 셀 생성
        Cell center = new Cell("C");
        cells.add(center);

        // 3) 각 꼭짓점에서 중앙까지 연결 (일반 셀 2개)
        for (int i = 0; i < sides; i++) {
            Cell d1 = new Cell("D" + i + "_1");
            Cell d2 = new Cell("D" + i + "_2");
            cells.add(d1);
            cells.add(d2);
            // 해당 꼭짓점 셀 위치 계산: i * (cellsPerEdge + 1)
            Cell vertex = perimeter.get(i * (cellsPerEdge + 1));
            vertex.getNextCells().add(d1);
            d1.getNextCells().add(d2);
            d2.getNextCells().add(center);
        }
        // 중앙에서 시작점(첫 번째 꼭짓점)으로 반환 연결
        center.getNextCells().add(perimeter.get(0));
    }

    /**
     * 주어진 셀에서 steps만큼 이동한 목표 셀을 반환합니다.
     * 분기점이 여러 개일 경우 첫 번째 연결을 따릅니다.
     * @param from 시작 셀
     * @param steps 이동할 칸 수
     * @return 목표 셀
     */
    public Cell getNextCell(Cell from, int steps) {
        Cell current = from;
        for (int i = 0; i < steps; i++) {
            List<Cell> next = current.getNextCells();
            if (next.isEmpty()) {
                throw new IllegalStateException("다음 셀이 존재하지 않습니다: " + current.getId());
            }
            current = next.get(0);
        }
        return current;
    }
}
