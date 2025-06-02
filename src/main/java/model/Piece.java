package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 윷놀이 게임에서 사용되는 말(Piece)
 */
public class Piece {
    private int id; // 말의 고유 ID (각 플레이어마다 자동적으로 0->1->2 ... 방식으로 지정 => 다른 플레이어의 말이면 id값이 같을 수 있음)
    private Player owner; // 이 말을 소유하고 있는 플레이어
    private Cell position; // 말의 현재 위치
    private PieceGroup group; // 말이 속해있는 그룹
    private PieceState state; // 말이 아직 보드판에 나오지 않았는지, 보드판에 있는지, 도착점을 지나 더 이상 움직일 수 없는 말인지 상태를 나타냄
    private final List<Cell> path; // 말이 지나온 cell들의 경로 기록

    // 생성자
    public Piece(int id, Player owner, Cell startPosition) {
        this.id = id;
        this.owner = owner;
        this.position = startPosition;
        this.group = null;
        this.state = PieceState.NOT_STARTED; // 말이 처음 생성될 때는 보드판으로 나가지 않은 상태
        this.path = new ArrayList<>();
        this.path.add(startPosition); // 모든 말의 경로는 출발점부터 시작됨
    }

    // getter
    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public PieceGroup getGroup() { return group; }
    public PieceState getState(){ return state; }
    public List<Cell> getPath() { return new ArrayList<>(path); }

    // setter
    void setGroup(PieceGroup group) {
        this.group = group;
    }

    // 이 말을 pieceGroup에 추가 (이 말이 같은 플레이어의 그룹말에 있는 칸으로 갔을 때 업는 기능)
    public void joinGroup(PieceGroup pieceGroup) {
        pieceGroup.grouping(this);
    }

    // 말을 next로 옮김
    public void moveTo(Cell next) {
        this.position.removePiece(this);    // 현재 칸에서 말 제거 (removePiece() : Cell 클래스에서 해당 Cell에 있는 말을 없애는 메소드를 의미)
        next.addPiece(this);                // 목적지 칸에 추가 (addPiece() : Cell 클래스에서 해당 Cell에 있는 말을 추가하는 메소드를 의미)
        this.position = next;               // 말의 위치를 next cell로 변경
        this.path.add(next);                // 경로 기록
    }

    // 백도에 의해 말을 이전 cell로 옮김
    public Cell backToPrevious() {
        // 시작점에서 백도가 나오는 경우 뒤로 갈 수 있는 cell이 없음
        if (path.size() < 2) {
            return this.position;
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