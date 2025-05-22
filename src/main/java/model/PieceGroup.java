package model;

import java.util.List;
import java.util.ArrayList;

public class PieceGroup {
    private List<Piece> pieceGroup = new ArrayList<>();

    // getter
    public List<Piece> getPieceGroup() {
        return pieceGroup;
    }

    // 말을 이 그룹에 추가
    public void grouping(Piece piece) {
        if (!pieceGroup.contains(piece)) {
            pieceGroup.add(piece);
            piece.setGroup(this);
        }
    }

    // 그룹 해체 (1. 다른 유저의 말/그룹에 의해 이 그룹이 잡히거나 2. 도착하면 => 그룹 해체)
    // 그룹 자체를 없애는 건 게임 시스템 차원에서 관리해주는 거로 가정하겠음
    public void breakUp() {
        pieceGroup.clear();
    }


    //나린-아직 약간스 이해안됨. group을 piece에서만 접근가능한 것 같은데, 각 피스별로 movegroup시행하면 group한에 여러 피르가 들어있을 땐,
        //한 피스만 실행되어야 함. group으로 붂여있을 땐 한 피스만 이 movegroup메소드를 쓸 수 잇게해야.. group안에 head인 피스를 정해둔다거나..
    // 그룹에 있는 모든 말들을 dest Cell로 이동
    public void moveGroup(Cell dest) {
        if (pieceGroup.isEmpty()) {
            return;
        }
        // 그룹에 있는 모든 말들을 dest Cell로 이동
        for (Piece piece : pieceGroup) {
            piece.moveTo(dest);
        }
    }
}