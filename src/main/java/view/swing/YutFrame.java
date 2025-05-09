// YutFrame.java
package view.swing;

import model.ThrowResult;
import service.YutThrowService;
import config.GameConfig;
import model.Game;
import javax.swing.*;
import java.awt.*;

public class YutFrame extends JFrame {
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private YutThrowService throwService;
    private GameConfig config;
    private Game game;

    public YutFrame() {
        super("윷놀이 Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        // 모델·서비스 초기화
        config = new GameConfig();
        //config.startNewGame();
        game = config.getGame();
        throwService = new YutThrowService();

        // UI 컴포넌트 생성
        boardPanel = new BoardPanel(game.getBoard());
        controlPanel = new ControlPanel(e -> onThrow());

        // 레이아웃 배치
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void onThrow() {
        // 1) 윷 던지기 결과
        ThrowResult res = throwService.throwYut();
        controlPanel.setResult(res);

        // 2) 게임 로직 처리
        game.applyThrow(res);
        if (!res.isExtraTurn()) {
            game.endTurn();
        }

        // 3) UI 업데이트
        boardPanel.repaint();

        // 4) 승리 체크
        if (game.isVictory()) {
            JOptionPane.showMessageDialog(this,
                    game.getCurrentPlayer().getName() + "님 승리!", "게임 종료",
                    JOptionPane.INFORMATION_MESSAGE);
            config.onGameEnd();
            game = config.getGame();
            boardPanel.setBoard(game.getBoard());
            boardPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(YutFrame::new);
    }
}

