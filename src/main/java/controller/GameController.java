package controller;

import model.*;
import service.MoveActionService;
import service.RuleEngine;
import service.YutThrowService;
import view.swing.ControlPanel;
import view.swing.GameBoardPanel;
import view.swing.StatusPanel;

import javax.swing.*;

public class GameController {
    private final Game game;
    private final GameBoardPanel boardPanel;
    private final ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private final YutThrowService throwService;
    private final MoveActionService moveService;
    private final RuleEngine ruleEngine;

    private Board board;

    public GameController(Game game, GameBoardPanel boardPanel, ControlPanel controlPanel, StatusPanel statusPanel) {
        this.game = game;
        this.boardPanel = boardPanel;
        this.controlPanel = controlPanel;
        this.statusPanel = statusPanel;
        this.throwService = new YutThrowService();
        this.moveService = new MoveActionService(new RuleEngine());
        this.ruleEngine = new RuleEngine();

        controlPanel.setRandomListener(e -> onThrow(null));
        controlPanel.setDesignatedListener(e -> onThrow(controlPanel.getSelectedResult()));

//
//        board = game.getBoard();
//        Cell start = board.getStartCell();
//        for (Player p : game.getPlayers()) {
//            for (Piece piece : p.getPieces()) {
//                piece.moveTo(start); // 꼭 있어야 함!
//            }
//        }

    }

    private void onThrow(ThrowResult designated) {
        ThrowResult result = null;
        if(designated==null) result = throwService.throwRandom();
//        ThrowResult result = (designated == null)
//                ? throwService.throwRandom()
//                : throwService.throwDesignated(designated);
        statusPanel.updateStatus(result);

        // simple: move first available piece
        Piece piece = game.getPlayers().get(0).selectPiece();
        moveService.movePiece(piece, result, game);

        boardPanel.repaint();
        if (game.isFinished()) {
            JOptionPane.showMessageDialog(boardPanel, "게임 종료! 승리: " + game.getWinner().getName());
        }


    }

    public Board getBoard(){
        return game.getBoard();
    }
}