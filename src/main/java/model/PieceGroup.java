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

    public List<Cell> getPath() {
        return path;
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
        return path.getLast();
    }

    // --- Setter --- //
    // 새로 병합된 말의 path로 갱신
    public void setPiecesPath(List<Cell> newPath) {
        for(Piece p : pieces){
            p.setPath(newPath);
        }
    }
    private void setGroupPath(Piece newPiece) {
        path = newPiece.getPath();
    }

    public void setPiecesState(PieceState state) {
        for(Piece p : pieces){
            p.setState(state);
        }
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

    // 그룹에서 piece 제거
    public void remove(Piece piece) {
        if (pieces.remove(piece)) {
            piece.setGroup(null);
        }
    }

    // 그룹 해체 (1. 다른 유저의 말/그룹에 의해 이 그룹이 잡히거나 2. 도착하면 => 그룹 해체)
    // 그룹 자체를 없애는 건 게임 시스템 차원에서 관리해주는 거로 가정하겠음
    public void breakUp() {
        pieces.clear();
    }

    // 그룹에 있는 모든 말들을 dest Cell로 이동
    public void moveGroupTo(Cell nextCell) {
        if (pieces.isEmpty()) {
            return;
        }
        // 그룹에 있는 모든 말들을 dest Cell로 이동
        for (Piece piece : pieces) {
            piece.moveTo(nextCell);
            setGroupPath(piece);
        }
    }

    public Cell backToPrevious() {
        if (path.size() < 2) {
            throw new IllegalStateException("더 이상 뒤로 돌아갈 수 없습니다."); // 이거 어칼까?
        } else if(path.size() > 2){
            // 마지막 기록 제거
            path.remove(path.size() - 1);
            // 돌아갈 이전 Cell
            Cell prev = path.get(path.size() - 1);
            path.remove(path.size() - 1);
            // 그룹 내 모든 말들 경로 중간 업데이트
            setPiecesPath(path);
            // 실제 위치 업데이트
            moveGroupTo(prev);
        } // else: path가 startCell, 첫 번째 cell인 경우는 첫 번째 cell을 삭제하지 않음
        else{ // 도 위치(E0_0)에서 빽도 나온 상황
            moveGroupTo(path.get(0));
        }

        System.out.println("빽도 적용 후 경로: ");
        for(Cell c : path){
            System.out.print(c.getId()+" ");
        }
        return getCurrentCell();
    }

//
//    public void handleFinished(Cell start){
//        if(start.isStartCell()){
//
//        }
//        for (Piece piece : pieces) {
//            piece.moveTo(nextCell);
//            setGroupPath(piece);
//        }
//    }
}