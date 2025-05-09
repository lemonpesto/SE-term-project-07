// src/main/java/service/MovementService.java
package service;

import model.Board;
import model.Cell;
import model.Piece;
import model.ThrowResult;
import model.Game;
import service.RuleEngine;

/**
 * 말의 이동과 이동 후 룰 적용을 담당하는 서비스 클래스입니다.
 */
public class MovementService {
    private final Board board;
    private final RuleEngine ruleEngine;

    /**
     * @param board 윷놀이판 모델
     * @param ruleEngine 이동 후 룰(집단화, 포획, 승리판정, 추가턴)를 적용할 엔진
     */
    public MovementService(Board board, RuleEngine ruleEngine) {
        this.board = board;
        this.ruleEngine = ruleEngine;
    }

    /**
     * 주어진 말(piece)을 ThrowResult 결과에 따라 이동시키고,
     * 이동 후 관련 규칙을 순서대로 적용합니다.
     *
     * @param piece 이동할 말
     * @param result 윷 던지기 결과
     * @param game 현재 게임 상태
     */
    public void movePiece(Piece piece, ThrowResult result, Game game) {
        Cell from = piece.getPosition();
        Cell to;
        // BACK_DO 처리: 이전 셀을 찾아 이동
        if (result == ThrowResult.BACK_DO) {
            to = findPreviousCell(from);
            if (to == null) {
                throw new IllegalStateException("뒤로 이동할 셀이 없습니다: " + from.getId());
            }
        } else {
            // 도, 개, 걸, 윷, 모
            to = board.getNextCell(from, result.getSteps());
        }

        // 실제 말 이동
        piece.moveTo(to);

        // 이동 후 룰 적용
        ruleEngine.applyGrouping(to);
        ruleEngine.applyCapture(to);
        ruleEngine.applyVictoryCheck(game);
        ruleEngine.applyExtraTurn(game, result);
    }

    /**
     * 현재 셀에서 한 칸 이전에 있던 셀을 찾습니다.
     * 보드 전체를 순회하며 next 포인터로부터 역으로 검색합니다.
     */
    private Cell findPreviousCell(Cell current) {
        for (Cell cell : board.getCells()) {
            if (cell.getNext() == current) {
                return cell;
            }
        }
        return null;
    }
}
