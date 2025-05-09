package model;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int id; // 말의 고유 ID (각 플레이어마다 자동적으로 0->1->2 ... 방식으로 지정 => 다른 플레이어의 말이면 id값이 같을 수 있음)
    private Player owner; // 이 말을 소유하고 있는 플레이어
    private Cell position; // 말의 현재 위치
    private List<Cell> path; // 말이 거쳐온 Cell들의 리스트 (연속 백도 처리 용도)
    private PieceGroup group; // 말이 속해있는 그룹
    private PieceState state; // 말이 아직 보드판에 나오지 않았는지, 보드판에 있는지, 도착점을 지나 더 이상 움직일 수 없는 말인지 상태를 나타냄

    // 생성자
    public Piece(int id, Player owner, Cell startPosition) {
        this.id = id;
        this.owner = owner;
        this.position = null; // 말이 시작할 떄 시작점에 위치하는데, 이 시작점 칸을 구분할 수 있는 변수가 Cell 클래스에 있나여
        this.path = new ArrayList<>();
        this.path.add(startPosition); // 모든 말의 경로는 출발점에서 시작
        this.group = null; // null로 해도 되나여
        this.state = PieceState.NOT_STARTED; // 말이 처음 생성될 때는 보드판으로 나가지 않은 상태
    }

    // getter
    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public List<Cell> getPath() { return path; }
    public PieceGroup getGroup() { return group; }
    public PieceState getState(){ return state; }

    // 말을 dest칸으로 옮김
    public void moveTo(Cell dest) {
        this.position.removePiece(this); // 현재 위치한 칸에서 말 제거
        dest.addPiece(this); // dest 칸으로 이동
        this.position = dest; // 말의 현재 위치를 dest로 변경
        this.path.add(dest); // 말이 이동한 경로를 기록
    }

    // 말을 한 칸 전 Cell로 이동
    public Cell oneStepBack() {
        // 현재 말이 출발점에 위치하지 않는다면
        if (path.size() >= 2) {
            // 말이 거쳐온 경로 중 현재 위치한 Cell 제거
            path.remove(path.size() - 1);
            // 백도에 의해 돌아갈 한 칸 전 Cell
            Cell oneStepBackCell = path.get(path.size() - 1);
            // 실제 위치 업데이트
            this.position.removePiece(this); // 현재 위치한 Cell에서 말을 없앰
            oneStepBackCell.addPiece(this); // 한 칸 전 Cell에 말을 추가
            this.position = oneStepBackCell; // 말의 위치 재설정
            return oneStepBackCell;
        }
        else {
            return null;
        }
    }

    // setter
    void setGroup(PieceGroup group) {
        this.group = group;
    }

    // 이 말을 pieceGroup에 추가 (이 말이 같은 플레이어의 그룹말에 있는 칸으로 갔을 때 업는 기능)
    public void joinGroup(PieceGroup pieceGroup) {
        pieceGroup.grouping(this);
    }
}
