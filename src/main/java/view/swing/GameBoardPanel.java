package view.swing;

import config.GameConfig;
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

// custom drawing of the board and pieces
public class GameBoardPanel extends JPanel {
    private final Board board;
    private final Map<Cell, Point> positions = new HashMap<>();
    private final int RADIUS = 250;
    private final int CELL_SIZE = 20;

    public GameBoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
        computePositions();
    }

    private void computePositions() {
        List<Cell> perimeter = board.getCells();
        // perimeter cells are first sides*(cellsPerEdge+1) entries
        List<Cell> ring = perimeter.subList(0, perimeter.size());
        int m = ring.size();
        Point center = new Point(getPreferredSize().width / 2, getPreferredSize().height / 2);
        double angleStep = 2 * Math.PI / m;
        for (int i = 0; i < m; i++) {
            double theta = -Math.PI / 2 + i * angleStep;
            int x = (int) (center.x + RADIUS * Math.cos(theta));
            int y = (int) (center.y + RADIUS * Math.sin(theta));
            positions.put(ring.get(i), new Point(x, y));
        }
        // center cell
        positions.put(board.getStartCell(), center);
        for (Cell c : board.getCells()) {
            if (!positions.containsKey(c) && c.getId().startsWith("D")) {
                // place diagonal path cells roughly halfway to center
                positions.put(c, new Point(center.x, center.y));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw edges
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            for (Cell nxt : c.getNextCells()) {
                Point q = positions.get(nxt);
                if (p != null && q != null) {
                    g2.drawLine(p.x, p.y, q.x, q.y);
                }
            }
        }
        // draw cells & pieces
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            if (p != null) {
                g2.fillOval(p.x - CELL_SIZE / 2, p.y - CELL_SIZE / 2, CELL_SIZE, CELL_SIZE);
                // draw pieces in this cell
                int offset = 0;
                for (Piece piece : c.getOccupants()) {
                    Color col = new Color((piece.getOwner().getId() * 50) % 256,
                            (piece.getOwner().getId() * 80) % 256,
                            (piece.getOwner().getId() * 110) % 256);
                    g2.setColor(col);
                    g2.fillOval(p.x - 6 + offset, p.y - 6 + offset, 12, 12);
                    offset += 14;
                }
                g2.setColor(Color.BLACK);
            }
        }
    }
}