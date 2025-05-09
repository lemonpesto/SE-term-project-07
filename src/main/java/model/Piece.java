package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임에서 사용되는 말(Piece)입니다.
 * 이동 경로(path)를 기록하여 다중 Back-Do 처리를 지원합니다.
 */
public class Piece {
    private final String id;
    private final Player owner;
    private Cell position;
    private final List<Cell> path;    // 이동 경로 기록
    private boolean grouped;

    /**
     * @param id 말 고유 ID
     * @param owner 소유 플레이어
     * @param startPosition 출발 셀 (path 첫 원소)
     */
    public Piece(String id, Player owner, Cell startPosition) {
        this.id = id;
        this.owner = owner;
        this.position = startPosition;
        this.grouped = false;
        this.path = new ArrayList<>();
        // 출발점 기록
        this.path.add(startPosition);
        startPosition.addPiece(this);
    }

    public String getId() { return id; }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public boolean isGrouped() { return grouped; }
    public void setGrouped(boolean g) { this.grouped = g; }

    /** 이동 경로 전체를 반환합니다. */
    public List<Cell> getPath() {
        return new ArrayList<>(path);
    }

    /**
     * 한 칸 앞으로 이동하며 path에 기록합니다.
     * @param dest 이동할 셀
     */
    public void moveTo(Cell dest) {
        // 현재 칸에서 제거
        this.position.removePiece(this);
        // 목적지 칸에 추가
        dest.addPiece(this);
        // 위치 업데이트
        this.position = dest;
        // 경로 기록
        this.path.add(dest);
    }

    /**
     * Back-Do: path에 기록된 마지막 셀을 제거하고 이전 셀로 이동합니다.
     * @return 복귀한 셀
     */
    public Cell backToPrevious() {
        if (path.size() < 2) {
            throw new IllegalStateException("더 이상 뒤로 돌아갈 수 없습니다.");
        }
        // 마지막 기록 제거
        path.remove(path.size() - 1);
        // 이전 셀
        Cell prev = path.get(path.size() - 1);
        // 실제 위치 업데이트
        this.position.removePiece(this);
        prev.addPiece(this);
        this.position = prev;
        return prev;
    }
}