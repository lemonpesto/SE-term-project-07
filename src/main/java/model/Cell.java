package model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final String id;
    private List<Cell> nextCells = new ArrayList<>(); // 바로 다음 cell
    private List<Piece> occupants = new ArrayList<>(); // 해당 셀에 있는 말

    public Cell(String id) {
        this.id = id; // 꼭짓점: V0, V1, ..., 일반 셀: E0_0, E0_1, ...
    }

    public void setNextCells(List<Cell> nextCells) { this.nextCells = nextCells;}

    public String getId() { return id;}
    public List<Cell> getNextCells() { return nextCells; }
    public List<Piece> getOccupants() { return occupants; }

    // 셀에 말 추가
    public void addPiece(Piece piece) {
        occupants.add(piece);
    }

    // 셀에서 말 제거
    public void removePiece(Piece piece) {
        occupants.remove(piece);
    }
}