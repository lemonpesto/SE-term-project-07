package service;

import model.*;


public class MoveActionService {
    private final RuleEngine ruleEngine;

    public MoveActionService(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /**
     * 윷 던지기 결과 result에 의해 destination cell로 이동한 후 룰 적용
     */
    public void movePiece(Piece piece, ThrowResult result) {
        // result에 의해 결정된 도착 cell인 destination으로 이동
        Cell destination;
        if (result == ThrowResult.BACK_DO) {
            destination = piece.backToPrevious();
        } else {
            destination = moveForward(piece, result.getSteps());
        }

        // 도착 cell에서 룰 적용 (잡기, 업기)
        applyRules(piece, destination);
    }

    /** steps만큼 순방향 이동 후 도착 cell 반환 */
    private Cell moveForward(Piece piece, int steps) {
        Cell current = piece.getPosition(); // 현재 위치

        // 윷을 던진 결과에 대응하는 칸 수 만큼 전진
        for (int i = 0; i < steps; i++) {
            // 나린이 코드
        }
        return current;
    }

    /** piece가 도착한 cell에서 룰 적용 (잡기, 업기) */
    private void applyRules(Piece piece, Cell cell) {
        // cell에 같은 플레이어의 말이 기존에 존재했다면 말을 업기
        if (ruleEngine.checkGrouping(cell)) {
            // 기존에 존재한 말이 단독으로 존재했는지, 그룹으로 존재하는지 확인하고 업어야 함
            PieceGroup group = new PieceGroup();
            for (Piece p : cell.getOccupants()) {
                group.grouping(p);
            }
        }
        // cell에 다른 플레이어의 말이 기존에 존재했다면 말을 잡기
        if (ruleEngine.checkCapture(cell)) {
            // capture 처리 로직 호출 (추가 구현 필요)
        }
    }
}
