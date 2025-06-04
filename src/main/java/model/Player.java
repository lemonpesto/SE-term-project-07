package model;

import java.util.List;
import java.util.ArrayList;


public class Player {
    private int id; // 플레이어 고유 ID (유저가 설정하지 않고, 게임 시스템 내에서 auto increment 적용할 예정)
    private String name; // 플레이어 이름 (게임 시작할 때 유저가 직접 설정할 수 있도록)
    private List<Piece> pieces; // 플레이어가 가진 모든 말 (최소 2개 ~ 최대 5개)
    private List<PieceGroup> pieceGroups; // 플레이어가 가진 말 그룹 (게임 도중 발생한 업힘)
    private boolean isFinished; // 플레이어가 모든 말을 내보냈는지 여부 (0이면 게임 중, 1이면 플레이어의 모든 말이 도착하여 끝남)

    // 생성자 -> 게임 시작할 때 호출할 것 같음
    public Player(int id, String name, int piecesNum, Cell startCell) {
        this.id = id;
        this.name = name;
        this.pieces = new ArrayList<>(piecesNum);
        this.pieceGroups = new ArrayList<>(piecesNum);
        this.isFinished = false;

        // 유저가 설정한 개수만큼 말 생성
        for (int i = 0; i < piecesNum; i++) {
            pieces.add(new Piece(i, this, startCell));
        }
    }

    // getter
    public int getId() { return id; }
    public String getName() { return name; }
    public java.util.List<Piece> getPieces() { return pieces; }
    public List<PieceGroup> getPieceGroups() { return pieceGroups; }
    public boolean getIsFinished() { return isFinished; }

    // 플레이어의 말들 중에서 (내보내지 않은 상태의) 선택
    public Piece selectPiece() {
        for (Piece piece : pieces) {
            // 아직 들어오지 않은 말만 이동 가능
            if(piece.getState() != PieceState.FINISHED){
                return piece;
            }
        }
        return null;
    }

    // 플레이어가 모든 말을 내보냈는지 확인하고, 그렇다면 finishFlag를 true로 설정 후 반환
    public boolean checkAllPiecesFinished() {
        if(isFinished){ // 모든 말을 내보낸 플레이어인 경우 바로 true 반환
            return true;
        }
        for(Piece piece : pieces){
            // 플레이어가 가진 말의 상태가 하나라도 FINISHED 상태가 아니라면 isFinished를 false로 설정하고 함수 종료
            if(!(piece.getState() == PieceState.FINISHED)){
                return false;
            }
        }
        return this.isFinished = true; // 모든 말을 내보냈다면 isFinished를 true로 갱신
    }

    // 플레이어의 모든 말이 아직 출발하지 않은 상태라면 true 반환
    private boolean checkAllPiecesNotStarted(){
        for(Piece piece : pieces){
            // 플레이어가 가진 말의 상태가 하나라도 NOT_STARTED 상태가 아니라면 false
            if(!(piece.getState() == PieceState.NOT_STARTED)){
                return false;
            }
        }
        return true;
    }

    public boolean hasNoMovablePiece(ThrowResult throwResult){
        // 1) 보드 위에 있는 말이 없는 경우
        if(checkAllPiecesNotStarted()){
            return true;
        }
        // 2) 뒤로 이동 가능한 말이 있는지 검사
        // 보드 위에 있는 피스가 오직 '도 --> 빽도' 경로로 출발점에 도착한 피스밖에 없는 경우만 true
        if(throwResult == ThrowResult.BACK_DO){
            for(Piece piece : pieces){
                if(piece.getState() == PieceState.ON_BOARD){
                    if(!(piece.getCurrentCell().isStartCell())){
                        // 보드 위에 있는 피스의 위치가 출발점이 아니라면: 이동 가능
                        return false;
                    } else{
                        // 보드 위에 있는 피스의 위치가 출발점인데, 도--> 빽도 경로로 도착한 피스가 아니라면
                        if(piece.getPath().size() > 1){
                            return false;
                        }
                    }
                }
            }
            return true;
        } else{
            return false;
        }
    }
}