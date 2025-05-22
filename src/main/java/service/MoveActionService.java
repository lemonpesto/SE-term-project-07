package service;

import model.*;

import java.util.List;


public class MoveActionService {
    private final RuleEngine ruleEngine;

    public MoveActionService(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /** 말 이동시킨 후 룰 적용 */
    public void movePiece(Piece piece, ThrowResult result) {
        // 이동
        Cell destination;
        if (result == ThrowResult.BACK_DO) {
            destination = piece.backToPrevious();
        } else {
            destination = moveForward(piece, result.getSteps());
        }

        // 룰 적용
        if(piece.getState()==PieceState.ON_BOARD) {applyRules(piece, destination);}
    }

    /** steps만큼 순방향 이동 후 도착 cell 반환 */
    private Cell moveForward(Piece piece, int steps) {
        Cell current = piece.getPosition();
        //종료조건 체크
        if(current.getId().equals("V0")&&(steps>0)) {
            piece.setStateFinished();
        }

        //현재 셀(출발 셀)에 담긴 다음셀리스트 크기 검사해서, 어느 방향으로 가야할 지 결정 및 이동(step 한 개 소모)
        current  = (current.getNextCells().size() == 1) ? current.getNextCells().get(0) : current.getNextCells().get(1);
        //여기선 걍 무조건 다음칸으로 이동시킴!! (지름길 말고 그냥 길로)
        for (int i = 2; i <= steps; i++) {
            //종료조건 체크
            if(current.getId().equals("V0")&&((steps-i)>0)) {
                piece.setStateFinished();
                break;
            }
            current = current.getNextCells().getLast();
            piece.moveTo(current);

        }
        return current;
    }



    /** 이동 후 적용할 룰들을 분리된 메서드로 구현합니다. */
    private void applyRules(Piece piece, Cell cell) {
        // 말 업기
        if (ruleEngine.applyGrouping(cell)) {
            PieceGroup group = new PieceGroup();
            for (Piece p : cell.getOccupants()) {
                group.grouping(p);
            }
        }
        // 상대 말 잡기
        if (ruleEngine.applyCapture(cell)) {
            // capture 처리 로직 호출 (추가 구현 필요)
        }
    }
}
