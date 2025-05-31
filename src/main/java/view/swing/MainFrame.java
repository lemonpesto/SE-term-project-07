package view.swing;

import config.GameConfig;
import controller.GameController;
import model.Board;
import model.BoardShape;
import model.Cell;
import model.Game;
import model.Piece;
import model.ThrowResult;
import service.MoveActionService;
import service.RuleEngine;
import service.YutThrowService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final DialogView dialog;
    private final ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private final GameBoardPanel boardPanel;
//    private final GameController controller;

    public MainFrame() {
        super("OOAD 윷놀이 게임");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1) 설정 다이얼로그
        dialog = new DialogView(this);
        GameConfig config = dialog.showDialog();
        if (config == null) System.exit(0);

        // 2) 모델 초기화
        String[] names = new String[config.getNumPlayers()];
        for (int i = 0; i < names.length; i++) names[i] = "Player " + (i + 1);
        Game game = new Game(
                config.getNumPlayers(), names,
                config.getPiecesPerPlayer(), config.getBoardShape()
        );

        // 3) 뷰 초기화
        controlPanel = new ControlPanel();
        statusPanel = new StatusPanel();
        boardPanel = new GameBoardPanel(game.getBoard(), game);

        controlPanel.setupPieceSelector(game.getPlayers().get(0).getPieces());

        add(controlPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        // 4) 컨트롤러 연결
//        controller = new GameController(
//                game, boardPanel, controlPanel, statusPanel
//        );
    }

    public void start() {
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().start());
    }
}