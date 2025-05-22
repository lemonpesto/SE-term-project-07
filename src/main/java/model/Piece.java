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
    private PieceState state; // 말이 아직 보드판에 나오지 않았는지(출발 전), 보드판에 있는지(진행 중),
                                // 도착점을 지나 더 이상 움직일 수 없는 말(끝남)인지 상태를 나타냄
    private final List<Cell> path;    // 이동 경로 기록

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

    // getter
    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public PieceGroup getGroup() { return group; }
    public PieceState getState(){ return state; }
    public List<Cell> getPath() { return new ArrayList<>(path); }

    //setter
    public void setStateOnBoard(){
        this.state = PieceState.ON_BOARD;
    }

    public void setStateFinished(){
        this.state = PieceState.FINISHED;
    }

    /*업기 관련*/
    void setGroup(PieceGroup group) {
        this.group = group;
    }

    // 이 말을 pieceGroup에 추가 (말 업는 기능. *말이 해당 플레이어의 그룹말이 있는 칸으로 갔을 때)
    public void joinGroup(PieceGroup pieceGroup) {  //나린 - 이 piecegroup을 어떻게 만들어서 전해줄 수 있는거임??
        pieceGroup.grouping(this);
    }



    /*말 이동 관련*/
    // 말을 dest칸으로 옮김
    public void moveTo(Cell dest) {
        this.position.removePiece(this);    // 현재 칸에서 말 제거 (removePiece() : Cell 클래스에서 해당 Cell에 있는 말을 없애는 메소드를 의미)
        dest.addPiece(this);                // 목적지 칸에 추가 (addPiece() : Cell 클래스에서 해당 Cell에 있는 말을 추가하는 메소드를 의미)
        this.position = dest;               // 말의 위치를 dest Cell로 변경
        this.path.add(dest);                // 경로 기록  *나린 - dest셀 한개만 들어가는 것인지 확인 필요. dest까지 가는 셀들이 모두 들어가야 함!
                                            //만약 한 개만 들어간다면, 현 위치부터 dest셀까지의 모든 셀들을 path에 저장하는 거 여기에서 간단히 구현하거나 따로 구현하거나 해야 됨.
        // 말이 한 바퀴를 돌아 시작 Cell에 도착했을 때 말의 상태를 바꾸는 로직을 설계 중이었는데 시작 Cell을 어떻게 인식하느냐...에 대한 문제
            // -> check_piece_finished 함수를 어딘가에서 구현해서 moveTo를 부르는 클래스에서 실행시키자.
        // 빽도에 의해 다시 시작 Cell로 돌아온 경우는 어떻게 예외 처리를 할 건지...에 대한 고민
            // -> 현재 cell이 시작 cell인지&&말 상태가 ON_BOARD인지 점검 -> 맞다면 : 도개걸윷모 나오면 탈출, 빽도 나오면 가만히 있기.(그대로 시작 cell유지)
            // 글고 이거 실행도 moveTo를 부르는 클래스에서 체크하는 느낌으로 하자...그럼 이런 조건 체크 클래스인가가 잇으면 좋겟군. 이미 잇을지도.
    }

    public Cell backToPrevious() {
        if (path.size() <= 1) {
            throw new IllegalStateException("더 이상 뒤로 돌아갈 수 없습니다."); // 이거 어칼까?
        }
        // 마지막 기록 제거
        path.remove(path.size() - 1);
        // 이전 셀
        Cell prev = path.get(path.size() - 1);
        // 실제 위치 업데이트
        this.position.removePiece(this);    //현재 셀에서 내 말 삭제
        prev.addPiece(this);                //머르겟다 아래랑 비슷한거아닌가
        this.position = prev;               //이전 셀에 내 말 추가
        return prev;
    }
}