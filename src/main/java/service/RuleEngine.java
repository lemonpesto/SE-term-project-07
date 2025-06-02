package service;

import model.Cell;
import model.Piece;
import model.Player;
import java.util.List;

public class RuleEngine{

    private boolean checkSamePiecesInCell(Cell cell){   //cell위에 있는 말들이 같은 말인지 확인해주는 함수. 이 class내에서만 쓸거임.
        //첫번째 말이랑 비교하기. 첫번째 말이랑 비교해서 모두 같으면 업기인거고..첫번째 말이랑 다른 거 있으면 잡는거고..
        //(서로 다른 말이 있다면, 그 여부만 체크해주는 거. 어떤 말이 어떤 말을 잡을지는 applyCapture에서 결정
        List<Piece> occupants = cell.getOccupants();
        Player owner = occupants.get(0).getOwner();
        for (int i = 1; i < occupants.size(); i++) {
            if (!occupants.get(i).getOwner().equals(owner)) {
                return false;
            }
        }
        return true;
    }

    // 업을 상황인지 확인
    public boolean checkGrouping(Cell cell){
        //한 cell에 말 두개 오면 업기
        //이게 룰이 내가 업는 걸 구현하는 건 아닌 것 같은데 무얼하는건지...
        //업을수잇는지 조건체크인가보다
        //모든 턴에서(윷 두 번 던졌으면 각 선택 포함) 체크되어야 할 듯. 만약 true면 무조건 업는. 안 업을수도 잇나..??
        List<Piece> occupants = cell.getOccupants();
        return occupants.size() > 1 && checkSamePiecesInCell(cell);
            //occupants에 있는게 해당 cell위에 있는 piece배열이라고 생각함. 그 배열 길이가 1 초과면(말이 2개이상인) true내보냄.
            //&& cell위의 말들이 모두 같은 팀이면.
    }

    // 잡을 상황인지 확인
    public boolean checkCapture(Cell cell){
        //상대편 말 잡기
        List<Piece> occupants = cell.getOccupants();
        return occupants.size() > 1 && !checkSamePiecesInCell(cell); //여러 말이 있고, 그 중 서로 다른팀의 말이 있다면.
        //잡을 수 있는 상태라는 걸 알려주는 것 뿐. 여기에서 뭐가 뭘 잡을진 다른 class에서 해야할듯.
    }
}