package model;

import java.util.List;
import java.util.ArrayList;

public class PieceGroup {
    private final Player owner;
    private final List<Piece> pieces = new ArrayList<>();
    private List<Cell> path;

    public PieceGroup(Player owner) {
        this.owner = owner;
        this.path = new ArrayList<>();
    }

    // --- Getter --- //

    // 그룹에 속한 Piece 리스트 반환
    public List<Piece> getPieces() {
        return pieces;
    }

    // 그룹 소유자 플레이어 반환
    public Player getOwner() {
        return owner;
    }

    // 그룹에 속한 말 개수 반환
    public int size() {
        return pieces.size();
    }

    // 그룹이 현재 올라가 있는 Cell 반환
    public Cell getCurrentCell() {
        if (pieces.isEmpty()) {
            return null;
        }
        return pieces.get(0).getPosition();
    }

    // --- Setter --- //
    // 새로 병합된 말의 path로 갱신
    private void setGroupPath(Piece newPiece) {
        path = newPiece.getPath();
    }

    // --- Helper --- //

    // 말을 이 그룹에 추가
    public void grouping(Piece piece) {
        if (!pieces.contains(piece)) {
            pieces.add(piece);
            piece.setGroup(this);
            setGroupPath(piece);
        }
    }

    // 그룹 해체 (1. 다른 유저의 말/그룹에 의해 이 그룹이 잡히거나 2. 도착하면 => 그룹 해체)
    // 그룹 자체를 없애는 건 게임 시스템 차원에서 관리해주는 거로 가정하겠음
    public void breakUp() {
        pieces.clear();
    }

    // 그룹에 있는 모든 말들을 dest Cell로 이동
    public void moveGroupTo(Cell dest) {
        if (pieces.isEmpty()) {
            return;
        }
        // 그룹에 있는 모든 말들을 dest Cell로 이동
        for (Piece piece : pieces) {
            piece.moveTo(dest);
        }
    }

    public Cell backToPrevious() {
        if (path.size() < 2) {
            throw new IllegalStateException("더 이상 뒤로 돌아갈 수 없습니다."); // 이거 어칼까?
        }
        // 마지막 기록 제거
        path.remove(path.size() - 1);
        // 이전 셀
        Cell prev = path.get(path.size() - 1);
        // 실제 위치 업데이트
        moveGroupTo(prev);
        return getCurrentCell();
    }
}