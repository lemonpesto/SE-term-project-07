package service;

import model.Board;
import model.Cell;
import model.Game;
import model.Piece;

/**
 * Service: 말 이동 및 룰 적용 처리
 */
public class MoveActionService {
    private final YutThrowService yutService;
    private final RuleEngine ruleEngine;

    public MoveActionService(YutThrowService yutService, RuleEngine ruleEngine) {
        this.yutService = yutService;
        this.ruleEngine = ruleEngine;
    }

    /**
     * 주어진 말(piece)을 steps만큼 이동시키고 룰을 적용합니다.
     */
    public void movePiece(Game game, Piece piece, int steps) {
        Board board = game.getBoard();
        Cell from = piece.getPosition();
        Cell to = board.getNextCell(from, steps);

        from.removePiece(piece);
        to.addPiece(piece);

        ruleEngine.applyGrouping(to);
        ruleEngine.applyCapture(to);
        ruleEngine.applyVictoryCheck(game);
    }
}