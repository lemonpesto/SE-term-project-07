package view;

import model.Piece;
import model.Board;
import model.Game;
import model.Player;
import view.IGameView;
import view.IGameViewListener;
import view.swing.GameBoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SwingGameView
 *
 * - IGameView 인터페이스를 구현합니다.
 * - 내부에 GameBoardPanel을 배치하여 “보드+말”을 그립니다.
 * - “윷 던지기” 버튼, 상태 표시 레이블을 포함합니다.
 * - GameController를 IGameViewListener로 등록하면, 버튼/말 클릭 이벤트를 컨트롤러에 전달합니다.
 */
public class SwingGameView extends JPanel implements IGameView {

    private final GameBoardPanel boardPanel;   // 실제 보드판과 말(Occupant/Off-board)을 그리는 패널
    private final JButton btnThrow;            // “윷 던지기” 버튼
    private final JLabel statusLabel;          // 상태 메시지 표시용 라벨

    // 게임 로직을 처리할 컨트롤러를 참조하기 위한 리스너
    private IGameViewListener listener;

    // “말 클릭”을 허용할지 여부 플래그
    private boolean pieceSelectable = false;

    public SwingGameView(Game game) {
        setLayout(new BorderLayout());

        // 보드판 패널 생성 (Board + Game 인자로 전달)
        boardPanel = new GameBoardPanel(game.getBoard(), game);
        add(boardPanel, BorderLayout.CENTER);

        // 마우스 클릭 이벤트를 “말 선택”으로 처리하도록 설정
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!pieceSelectable || listener == null) {
                    return;
                }
                Point clickPoint = e.getPoint();
                Piece clicked = boardPanel.getPieceAtPoint(clickPoint);
                if (clicked != null) {
                    listener.onPieceClicked(clicked);
                }
            }
        });

        // 2) 하단(또는 우측)에 “상태 + 버튼” 패널
        JPanel controlPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("게임 준비 중...");
        btnThrow = new JButton("윷 던지기");

        // 초기 상태: “윷 던지기”만 활성화, 말 선택은 비활성
        btnThrow.setEnabled(true);
        pieceSelectable = false;

        controlPane.add(statusLabel);
        controlPane.add(btnThrow);
        add(controlPane, BorderLayout.SOUTH);

        // 3) 버튼 클릭 시 컨트롤러(onThrowButtonClicked) 호출
        btnThrow.addActionListener(e -> {
            if (listener != null) {
                listener.onThrowButtonClicked();
            }
        });
    }

    /** ========= IGameView 인터페이스 구현 ========= **/

    @Override
    public void showWindow() {
        // SwingGameView 자체를 JFrame에 넣어서 보여주는 예시
        JFrame frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void repaintBoard() {
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
        // 시각적 표시를 원하면 boardPanel.repaint()를 호출해도 좋습니다.
    }
}
