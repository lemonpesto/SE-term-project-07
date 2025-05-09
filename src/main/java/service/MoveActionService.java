package service;

import model.*;

import java.util.List;


public class MoveActionService {
    private final RuleEngine ruleEngine;

    public MoveActionService(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /** 말 이동시킨 후 룰 적용 */
    public void movePiece(Piece piece, ThrowResult result, Game game) {
        // 이동
        Cell destination;
        if (result == ThrowResult.BACK_DO) {
            destination = piece.backToPrevious();
        } else {
            destination = moveForward(piece, result.getSteps());
        }

        // 룰 적용
        applyRules(piece.getOwner(), destination, game);
    }

    /** steps만큼 순방향 이동 후 도착 cell 반환 */
    private Cell moveForward(Piece piece, int steps) {
        Cell current = piece.getPosition();
        for (int i = 0; i < steps; i++) {
            List<Cell> nextList = current.getNextCells();
            Cell next = (nextList.size() == 1) ? nextList.get(0) : nextList.get(1);
            piece.moveTo(next);
            current = next;
        }
        return current;
    }

    /** 이동 후 적용할 룰들을 분리된 메서드로 구현합니다. */
    private void applyRules(Player player, Cell cell, Game game) {
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
