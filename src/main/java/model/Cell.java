package model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final String id;
    private List<Cell> nextCells; // 최대 2개 (외곽길, 대각길)
    private List<Piece> occupants; // 현재 이 cell에 올라온 말들

    public Cell(String id) {
        this.id = id; // 꼭짓점: V0, V1, ..., 일반 셀: E0_0, E0_1, ...
        this.nextCells = new ArrayList<>(2);
        this.occupants = new ArrayList<>();
    }

    public void addNextCell(Cell cell) { this.nextCells.add(cell);}

    public String getId() { return id;}
    public List<Cell> getNextCells() { return nextCells; }
    public int getNextCellCount() { return nextCells.size(); }
    public Cell getNextCell(int index) { return nextCells.get(index); }

    // 말 관리
    public List<Piece> getOccupants() { return occupants; }
    public void addPiece(Piece piece) { occupants.add(piece); } // 셀에 말 추가
    public void removePiece(Piece piece) { occupants.remove(piece); } // 셀에서 말 제거

    // 특수 Cell 체크
    public boolean isStartCell() {
        return id.equals("V0"); // 또는 상황에 맞게 수정
    }
    public boolean isLastVertex(BoardShape shape) {
        // 예시: 오각형이면 "V4", 사각형이면 "V3"
        int lastIdx = shape.getVertexCount() - 1;
        return id.equals("V" + lastIdx);
    }
    public boolean isCenter() {
        return id.equals("C");
    }
    public boolean isMiddleVertex(BoardShape shape) { // 가운데 꼭짓점 Cell (출발점/마지막 제외)
        if (!id.startsWith("V")) return false;
        if (isStartCell() || isLastVertex(shape)) return false;
        // 예: 오각형 - V1, V2, V3 / 사각형 - V1, V2
        try {
            int idx = Integer.parseInt(id.substring(1));
            return idx > 0 && idx < (shape.getVertexCount() - 1);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}