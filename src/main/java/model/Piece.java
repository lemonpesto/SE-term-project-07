package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임에서 사용되는 말(Piece)입니다.
 * 이동 경로(path)를 기록하여 다중 Back-Do 처리를 지원합니다.
 */
public class Piece {
    private int id; // 말의 고유 ID (각 플레이어마다 자동적으로 0->1->2 ... 방식으로 지정 => 다른 플레이어의 말이면 id값이 같을 수 있음)
    private Player owner; // 이 말을 소유하고 있는 플레이어
    private Cell position; // 말의 현재 위치
    private PieceGroup group; // 말이 속해있는 그룹
    private PieceState state; // 말이 아직 보드판에 나오지 않았는지, 보드판에 있는지, 도착점을 지나 더 이상 움직일 수 없는 말인지 상태를 나타냄
    private List<Cell> path;    // 이동 경로 기록

    // 생성자
    public Piece(int id, Player owner, Cell startPosition) {
        this.id = id;
        this.owner = owner;
        this.position = startPosition; // 말이 시작할 떄 시작점에 위치하는데, 이 시작점 칸을 구분할 수 있는 변수가 Cell 클래스에 있나여
        this.group = null; // null로 해도 되나여
        this.state = PieceState.NOT_STARTED; // 말이 처음 생성될 때는 보드판으로 나가지 않은 상태
        this.path = new ArrayList<>();
        this.path.add(startPosition); // 모든 말의 경로는 출발점부터 시작됨
    }

    // --- Getter --- //

    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public PieceGroup getGroup() { return group; }
    public PieceState getState(){ return state; }
    public List<Cell> getPath() { return new ArrayList<>(path); }

    public Cell getCurrentCell() {
        return path.getLast();
    }

    // --- Setter --- //

    public void setGroup(PieceGroup group) {
        this.group = group;
    }
    public void setState(PieceState state) {
        this.state = state;
    }
    public void setPosition(Cell position){
        this.position = position;
    }
    public void setPath(List<Cell> path){
        this.path = path;
    }

    // --- Helper --- //

    // 이 말을 pieceGroup에 추가 (이 말이 같은 플레이어의 그룹말에 있는 칸으로 갔을 때 업는 기능)
    public void joinGroup(PieceGroup pieceGroup) {
        pieceGroup.grouping(this);
    }

    // 말을 dest칸으로 옮김
    public void moveTo(Cell dest) {
        position.removePiece(this); // 현재 칸에서 말 제거 (removePiece() : Cell 클래스에서 해당 Cell에 있는 말을 없애는 메소드를 의미)
        dest.addPiece(this);        // 목적지 칸에 추가 (addPiece() : Cell 클래스에서 해당 Cell에 있는 말을 추가하는 메소드를 의미)
        position = dest; // 말의 위치를 dest Cell로 변경
        if(dest.isStartCell() && (state == PieceState.NOT_STARTED || path.size() == 2)){ // 말 잡힌 경우 or 도 --> 빽도로 출발점 도착한 경우
            path.clear();
        }
        path.add(dest); // 경로 기록
    }

}