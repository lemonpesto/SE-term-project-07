// src/view/swing/SwingGameView.java
package view.swing;

import model.Piece;
import model.Board;
import model.Game;
import model.ThrowResult;
import view.IGameView;
import view.IGameViewListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SwingGameView
 *
 * - IGameView 인터페이스를 구현합니다.
 * - GameBoardPanel을 중앙에 배치하여 보드와 말을 그립니다.
 * - 하단에 <랜덤 윷 던지기> / <지정 윷 던지기> 버튼을
 *   상태 메시지 위, 가운데 고정으로 배치합니다.
 * - “지정 윷 던지기” 버튼을 누르면
 *   JOptionPane으로 빽도/도/개/걸/윷/모 중 선택하게 한 뒤
 *   선택된 ThrowResult를 컨트롤러로 전달합니다.
 * - 말 클릭 시, 컨트롤러에 클릭된 Piece를 전달합니다.
 *   (단, 소유주 검사는 컨트롤러에서 수행)
 */
public class SwingGameView extends JPanel implements IGameView {

    private final GameBoardPanel boardPanel;

    private final JButton randomThrowButton;
    private final JButton fixedThrowButton;

    private final JLabel statusLabel;

    private IGameViewListener listener;
    private boolean pieceSelectable = false;

    public SwingGameView(Game game) {
        setLayout(new BorderLayout());

        // ---- [1] 중앙: 보드판 패널 ---- //
        boardPanel = new GameBoardPanel(game.getBoard(), game);
        add(boardPanel, BorderLayout.CENTER);

        // 말 클릭 이벤트 처리
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!pieceSelectable || listener == null) {
                    return;
                }
                Point clickPoint = e.getPoint();
                Piece clicked = boardPanel.getPieceAtPoint(clickPoint);
                if (clicked != null) {
                    // 소유주 검사는 GameController에서 수행하므로, 여기서는 단순 전달
                    listener.onPieceClicked(clicked);
                }
            }
        });

        // ────────────────────────────────────────────────────────────────────
        // [2] 하단: 버튼 패널 + 상태 메시지 패널 합친 영역
        // ────────────────────────────────────────────────────────────────────
        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));
        add(southContainer, BorderLayout.SOUTH);

        // 2-1) 버튼 패널: <랜덤 윷 던지기> / <지정 윷 던지기> 버튼을 가운데 정렬
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        randomThrowButton = new JButton("랜덤 윷 던지기");
        fixedThrowButton = new JButton("지정 윷 던지기");

        randomThrowButton.setEnabled(true);
        fixedThrowButton.setEnabled(true);
        pieceSelectable = false;

        buttonPane.add(randomThrowButton);
        buttonPane.add(fixedThrowButton);
        southContainer.add(buttonPane);

        // 2-2) 상태 메시지 패널: 라벨을 가운데 정렬
        JPanel statusPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        statusLabel = new JLabel("게임 준비 중...");
        statusPane.add(statusLabel);
        southContainer.add(statusPane);

        // ────────────────────────────────────────────────────────────────────
        // [3] 버튼 동작: 컨트롤러로 이벤트 전달
        // ────────────────────────────────────────────────────────────────────
        randomThrowButton.addActionListener(e -> {
            if (listener != null) {
                listener.onRandomThrowClicked();
            }
        });

        fixedThrowButton.addActionListener(e -> {
            // “지정 윷 던지기” 버튼 클릭 시
            String[] options = { "BACK_DO", "DO", "GAE", "GEOL", "YUT", "MO" };
            String choice = (String) JOptionPane.showInputDialog(
                    SwingGameView.this,
                    "윷 결과를 선택하세요:",
                    "지정 윷 던지기",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[1]  // 기본값 DO
            );
            if (choice != null && listener != null) {
                ThrowResult tr = ThrowResult.valueOf(choice);
                listener.onFixedThrowClicked(tr);
            }
        });
    }

    /** ========= IGameView 인터페이스 구현 ========= **/

    @Override
    public void showWindow() {
        JFrame frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void updateBoard() {
        boardPanel.repaint();
    }

    @Override
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void showWinnerDialog(String winnerName) {
        JOptionPane.showMessageDialog(
                this,
                winnerName + "님이 승리하셨습니다!",
                "게임 종료",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void setGameViewListener(IGameViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void setPieceSelectable(boolean enabled) {
        this.pieceSelectable = enabled;
    }

    @Override
    public void setThrowEnabled(boolean enabled) {
        randomThrowButton.setEnabled(enabled);
        fixedThrowButton.setEnabled(enabled);
    }
}
