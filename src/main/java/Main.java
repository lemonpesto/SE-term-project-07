
import model.Board;
import model.BoardShape;
import view.swing.GameBoardPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 판 생성 (원하는 모양 선택: SQUARE, PENTAGON, HEXAGON)
        Board board = new Board(BoardShape.HEXAGON);

        // Swing 창 생성
        JFrame frame = new JFrame("윷놀이판");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 판 패널 추가
        frame.add(new GameBoardPanel(board));
        frame.pack();
        frame.setLocationRelativeTo(null); // 화면 중앙에 창 위치
        frame.setVisible(true);
    }
}

