// src/view/swing/SwingGameView.java
package view.swing;

import controller.SetupControllerSwing;
import model.*;
import view.IGameView;
import view.IGameViewListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * SwingGameView
 *
 * -- GameBoardPanel 중앙 배치
 * -- 하단에 <랜덤 윷 던지기> / <지정 윷 던지기> 버튼을
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


        // ---- [2] 하단: 버튼 패널, 상태 메시지 패널 ---- //

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

        // ---- [3] 버튼 동작: 컨트롤러로 이벤트 전달 ---- //

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
    public void delayNextTurn(Runnable action, int delayMillis) {
        Timer timer = new Timer(delayMillis, e -> {
            action.run();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void showRankingDialog(List<Player> ranking) {
        // “최종 등수”를 문자열로 만들기
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            sb.append((i + 1)).append("등: ").append(p.getName()).append("\n");
        }

        // 옵션 버튼 배열: “다시 시작”, “종료”
        String[] options = { "다시 시작", "종료" };

        // showOptionDialog를 사용하여 커스텀 버튼 두 개 표시
        int choice = JOptionPane.showOptionDialog(
                this,
                sb.toString(),
                "최종 등수",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]  // 기본 선택: “다시 시작”
        );

        // choice == 0 → “다시 시작” / choice == 1 → “종료” / 혹은 그 외(-1)도 “종료”로 처리
        if (choice == 0) {
            // 1) 현재 게임 창 닫기
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) {
                win.dispose();
            }
            // 2) 다시 설정 창을 띄우기 위해 SetupController 새로 생성
            new SetupControllerSwing();
        } else {
            // “종료” 혹은 창을 닫아도 choice가 -1로 내려올 경우
            System.exit(0);
        }
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