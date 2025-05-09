package service;

import model.Cell;
import model.Piece;
import model.ThrowResult;
import model.Game;
import java.util.List;

/**
 * 말의 이동과 이동 후 룰 적용을 담당하는 서비스 클래스입니다.
 */
public class MovementService {
    private final RuleEngine ruleEngine;

    public MovementService(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /**
     * 말(piece)을 ThrowResult 결과에 따라 이동시키고,
     * 이동 후 모든 룰을 적용합니다.
     */
    public void movePiece(Piece piece, ThrowResult result, Game game) {
        if (result == ThrowResult.BACK_DO) {
            // 다중 Back-Do 지원
            Cell prev = piece.backToPrevious();
            // 포획 및 승리판정
            ruleEngine.applyCapture(prev);
            ruleEngine.applyVictoryCheck(game);
        } else {
            int steps = result.getSteps();
            Cell current = piece.getPosition();
            // steps만큼 이동
            for (int i = 0; i < steps; i++) {
                List<Cell> nextList = current.getNextCells();
                Cell next = (nextList.size() == 1) ? nextList.get(0) : nextList.get(1);
                piece.moveTo(next);
                current = next;
            }
            // 이동 후 룰 적용
            ruleEngine.applyGrouping(current);
            ruleEngine.applyCapture(current);
            ruleEngine.applyVictoryCheck(game);
            ruleEngine.applyExtraTurn(game, result);
        }
    }
}