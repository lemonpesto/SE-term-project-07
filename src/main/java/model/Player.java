package model;

import service.YutThrowService;

import java.util.List;
import java.util.ArrayList;

public class Player {
    private int id; // 플레이어 고유 ID (유저가 설정하지 않고, 게임 시스템 내에서 auto increment 적용할 예정)
    private String name; // 플레이어 이름 (게임 시작할 때 유저가 직접 설정할 수 있도록)
    private List<Piece> pieces; // 플레이어가 가진 모든 말 (최소 2개 ~ 최대 5개)
    private List<PieceGroup> pieceGroups; // 플레이어가 가진 말 그룹 (게임 도중 발생한 업힘)
    private boolean isFinished; // 플레이어가 모든 말을 내보냈는지 여부 (0이면 게임 중, 1이면 플레이어의 모든 말이 도착하여 끝남)

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

    // 랜덤 윷 던지기
    public YutThrowService throwYut() {
        YutThrowService throwResult = new YutThrowService();
        throwResult.throwYut();
        return throwResult;
    }

    // 지정 윷 던지기 (게임 시스템 상에서 유저가 도개걸윷모빽도 중 누른 버튼 정보를 추출했다고 가정)
    public YutThrowService throwYut(ThrowResult preset) {
        YutThrowService throwResult = new YutThrowService();
        throwResult.setResult(preset);
        return throwResult;
    }

    // 어떤 말을 움직일지 선택 (실제로 움직이는 것은 movePiece()에서)
    public Piece selectPiece() {
        for (Piece piece : pieces) {
            // 아직 들어오지 않은 말만 이동 가능
            if(piece.getState() == PieceState.NOT_STARTED || piece.getState() == PieceState.ON_BOARD){
                // 의문점 1 : 이 말을 선택했다는 것을 어떻게 아는지
                if(){
                    return piece;
                }
            }
        }
    }

    // 선택한 말(piece)을 원하는 곳(dest)으로 이동
    public void movePiece(Piece piece, Cell dest) {
        for (Piece curPiece : pieces){
            // 이동하려는 말의 ID와 현재 순회 중인 말의 ID가 같다면 이동
            if(piece.getId() == curPiece.getId()){
                piece.moveTo(dest);
            }
        }
    }
}
