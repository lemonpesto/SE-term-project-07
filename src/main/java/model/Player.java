package model;

import java.util.List;
import java.util.ArrayList;

public class Player {
    private int id; // 플레이어 고유 ID (유저가 설정하지 않고, 게임 시스템 내에서 auto increment 적용할 예정)
    private String name; // 플레이어 이름 (게임 시작할 때 유저가 직접 설정할 수 있도록)
    private List<Piece> pieces; // 플레이어가 가진 모든 말 (최소 2개 ~ 최대 5개)
    private List<PieceGroup> pieceGroups; // 플레이어의 말이 업고 업혀
    private boolean state; // 플레이어가 모든 말을 내보냈는지 여부

    // 생성자 -> 게임 시작할 때 호출할 것 같음
    public Player(int id, String name, int piecesNum) {
        this.id = id;
        this.name = name;
        this.pieces = new ArrayList<>(piecesNum);
        this.pieceGroups = new ArrayList<>(piecesNum);

        // 유저가 설정한 개수만큼 말 생성
        for (int i = 0; i < piecesNum; i++) {
            pieces.add(new Piece(i, this));
        }
    }

    // getter
    public int getId() { return id; }
    public String getName() { return name; }
    public List<Piece> getPieces() { return pieces; }

//    // 유저가 윷 던지기 (랜덤 결과)
//    public YutThrow requestThrow() {
//        YutThrow y = new YutThrow();
//        y.generateRandom();        // YutThrow 내부에서 랜덤 결과 생성
//        return y;
//    }
//
//    // 유저가 윷 던지기 (지정 결과)
//    public YutThrow requestThrow(ThrowResult preset) {
//        YutThrow y = new YutThrow();
//        y.setResult(preset);       // 빽도, 도, 개, … 를 직접 설정
//        return y;
//    }
//
//    /**
//     * 던진 결과에 따라 어떤 말을 이동시킬지 선택합니다.
//     * 실제 구현 시 RuleEngine.canMove(...) 등을 사용해
//     * 이동 가능 여부를 판단하도록 확장하세요.
//     */
//    public Piece selectPiece(YutThrow result) {
//        for (Piece p : pieces) {
//            // 예시) if (RuleEngine.canMove(p, result)) return p;
//        }
//        // 모두 불가능하거나 로직 미구현 시 첫 번째 말 반환
//        return pieces.isEmpty() ? null : pieces.get(0);
//    }
}
