package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 보드의 한 칸을 나타내며, 최단 경로 기반으로 다음 셀 하나를 가리키는 next 포인터를 가집니다.
 */
public class Cell {
    private final String id;
    private Cell next;
    private final List<Piece> occupants;

    /**
     * 셀 식별자만 설정하고 초기화합니다.
     * @param id 셀 고유 ID
     */
    public Cell(String id) {
        this.id = id;
        this.occupants = new ArrayList<>();
    }

    /** 셀 ID를 반환합니다. */
    public String getId() {
        return id;
    }

    /** 다음으로 이동할 셀을 반환합니다. */
    public Cell getNext() {
        return next;
    }

    /** next 포인터를 설정합니다. */
    public void setNext(Cell next) {
        this.next = next;
    }

    /** 이 칸에 위치한 말 목록을 반환합니다. */
    public List<Piece> getOccupants() {
        return occupants;
    }

    /** 칸에 말을 추가합니다. */
    public void addPiece(Piece piece) {
        occupants.add(piece);
    }

    /** 칸에서 말을 제거합니다. */
    public void removePiece(Piece piece) {
        occupants.remove(piece);
    }
}