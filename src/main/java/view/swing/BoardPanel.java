// BoardPanel.java
package view.swing;

import model.Board;
import model.Cell;
import model.Piece;
import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel(Board board) {
        this.board = board;
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 보드 셀 그리기
        for (Cell cell : board.getCells()) {
            Rectangle r = cell.getBounds();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.BLACK);
            g.drawRect(r.x, r.y, r.width, r.height);
        }

        // 말 그리기
        for (Cell cell : board.getCells()) {
            int dx = cell.getBounds().x + 5;
            int dy = cell.getBounds().y + 5;
            for (Piece p : cell.getOccupants()) {
                Image img = p.getImage();
                if (img != null) {
                    g.drawImage(img, dx, dy, 30, 30, this);
                }
                dx += 32;
            }
        }
    }
}

