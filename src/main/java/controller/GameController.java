package controller;

import model.Game;
import model.Piece;
import model.Player;
import model.ThrowResult;
import service.MoveActionService;
import service.RuleEngine;
import service.YutThrowService;
import view.swing.ControlPanel;
import view.swing.GameBoardPanel;
import view.swing.StatusPanel;

import javax.swing.*;
import java.util.List;

public class GameController {
    private final Game game;
    private final GameBoardPanel boardPanel;
    private final ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private final YutThrowService throwService;
    private final MoveActionService moveService;
    private final RuleEngine ruleEngine;

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
    }

    private void onThrow(ThrowResult designated) {
        ThrowResult result = (designated == null)
                ? throwService.throwRandom()
                : throwService.throwDesignated(designated);
        statusPanel.updateStatus(result);

        // simple: move first available piece
        Piece piece = game.getPlayers().get(0).selectPiece(0);
        moveService.movePiece(piece, result, game);

        boardPanel.repaint();
        if (game.isFinished()) {
            List<Player> finished = game.getFinishedPlayers();
            StringBuilder sb = new StringBuilder("게임 종료!\n");
            for (int i = 0; i < finished.size(); i++) {
                sb.append(i + 1)
                        .append("위: ")
                        .append(finished.get(i).getName())
                        .append("\n");
            }
            JOptionPane.showMessageDialog(boardPanel, sb.toString());
        }
    }
}