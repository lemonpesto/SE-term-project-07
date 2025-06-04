package service;

import model.Cell;
import model.Piece;
import model.PieceState;
import model.Player;
import java.util.List;
import java.util.stream.Collectors;

public class RuleEngine{

    // 현재 셀에 있는 ON_BOARD 상태의 피스들이 모두 같은 소유자인지 체크
    private boolean checkSamePiecesInCell(Cell cell) {//cell위에 있는 말들이 같은 말인지 확인해주는 함수. 이 class내에서만 쓸거임.
        //첫번째 말이랑 비교하기. 첫번째 말이랑 비교해서 모두 같으면 업기인거고..첫번째 말이랑 다른 거 있으면 잡는거고..
        //(서로 다른 말이 있다면, 그 여부만 체크해주는 거. 어떤 말이 어떤 말을 잡을지는 applyCapture에서 결정
        // 셀 위의 피스들 중 ON_BOARD 상태인 것만 모음
        List<Piece> onBoardPieces = cell.getOccupants().stream()
                .filter(p -> p.getState() == PieceState.ON_BOARD)
                .collect(Collectors.toList());

        if (onBoardPieces.size() < 2) {
            // ON_BOARD 피스가 2개 미만이면 그룹화 조건 불충분
            return false;
        }

        Player owner = onBoardPieces.get(0).getOwner();
        // 나머지 ON_BOARD 피스들이 모두 동일한 플레이어인지 확인
        for (int i = 1; i < onBoardPieces.size(); i++) {
            if (!onBoardPieces.get(i).getOwner().equals(owner)) {
                return false;
            }
        }
        return true;
    }

    public boolean applyGrouping(Cell cell){
//        //한 cell에 말 두개 오면 업기
//        //이게 룰이 내가 업는 걸 구현하는 건 아닌 것 같은데 무얼하는건지...
//        //업을수잇는지 조건체크인가보다
//        //모든 턴에서(윷 두 번 던졌으면 각 선택 포함) 체크되어야 할 듯. 만약 true면 무조건 업는. 안 업을수도 잇나..??
//        List<Piece> occupants = cell.getOccupants();
//        return occupants.size() > 1 && checkSamePiecesInCell(cell);
//            //occupants에 있는게 해당 cell위에 있는 piece배열이라고 생각함. 그 배열 길이가 1 초과면(말이 2개이상인) true내보냄.
//            //&& cell위의 말들이 모두 같은 팀이면.

        // 셀 위 ON_BOARD 피스들 중 2개 이상이고, 모두 같은 소유자라면 true
        return checkSamePiecesInCell(cell);

    }

    public boolean applyCapture(Cell cell){
//        //상대편 말 잡기
//        List<Piece> occupants = cell.getOccupants();
//        return occupants.size() > 1 && !checkSamePiecesInCell(cell); //여러 말이 있고, 그 중 서로 다른팀의 말이 있다면.
//        //잡을 수 있는 상태라는 걸 알려주는 것 뿐. 여기에서 뭐가 뭘 잡을진 다른 class에서 해야할듯

        // 셀 위 ON_BOARD 피스들만 모읍니다.
        List<Piece> onBoardPieces = cell.getOccupants().stream()
                .filter(p -> p.getState() == PieceState.ON_BOARD)
                .collect(Collectors.toList());

        if (onBoardPieces.size() < 2) {
            // ON_BOARD 피스가 2개 미만이면 캡처 조건 불충분
            return false;
        }

        Player firstOwner = onBoardPieces.get(0).getOwner();
        // ON_BOARD 피스들 사이에 다른 소유자가 섞여 있는지 검사
        for (int i = 1; i < onBoardPieces.size(); i++) {
            if (!onBoardPieces.get(i).getOwner().equals(firstOwner)) {
                return true;
            }
        }
        return false;
    }
}