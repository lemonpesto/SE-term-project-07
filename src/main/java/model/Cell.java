package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 보드의 개별 셀을 나타냅니다.
 */
public class Cell {
    private final String id;
    private List<Cell> nextCells = new ArrayList<>();
    private List<Piece> occupants = new ArrayList<>();

    public Cell(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Cell> getNextCells() {
        return nextCells;
    }

    public void setNextCells(List<Cell> nextCells) {
        this.nextCells = nextCells;
    }

    public List<Piece> getOccupants() {
        return occupants;
    }

    /**
     * 셀에 말을 추가하고, 말의 위치를 이 셀로 설정합니다.
     */
    public void addPiece(Piece piece) {
        occupants.add(piece);
        piece.setPosition(this);
    }

    /**
     * 셀에서 말을 제거합니다.
     */
    public void removePiece(Piece piece) {
        occupants.remove(piece);
    }
}