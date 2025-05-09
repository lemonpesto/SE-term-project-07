package view.swing;

import model.Board;
import model.Cell;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameBoardPanel extends JPanel {
    private final Board board;
    private final Map<Cell, Point> positions = new HashMap<>();
    private static final int CELLS_PER_EDGE = 4;
    private static final int CELLS_TO_CENTER = 2;
    private final int RADIUS = 250;
    private final int CELL_SIZE = 20;

    private static final Pattern PAT_VERTEX = Pattern.compile("V(\\d+)");
    private static final Pattern PAT_EDGE   = Pattern.compile("E(\\d+)_(\\d+)");
    private static final Pattern PAT_DIAG   = Pattern.compile("D(\\d+)_(\\d+)");

    public GameBoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        computePositions();
    }

    private void computePositions() {
        positions.clear();
        List<Cell> cells = board.getCells();
        int sides = board.getSides();
        Point center = new Point(getWidth()/2, getHeight()/2);

        Map<Integer, Point> vertexPoints = new HashMap<>();
        double[] angles = new double[sides];
        for (int i = 0; i < sides; i++) {
            angles[i] = -Math.PI/2 + 2*Math.PI*i/sides;
        }
        for (Cell c : cells) {
            Matcher mv = PAT_VERTEX.matcher(c.getId());
            if (mv.matches()) {
                int idx = Integer.parseInt(mv.group(1));
                int x = center.x + (int)(RADIUS * Math.cos(angles[idx]));
                int y = center.y + (int)(RADIUS * Math.sin(angles[idx]));
                Point p = new Point(x, y);
                positions.put(c, p);
                vertexPoints.put(idx, p);
            }
        }

        for (Cell c : cells) {
            if ("C".equals(c.getId())) {
                positions.put(c, center);
            }
        }

        for (Cell c : cells) {
            Matcher me = PAT_EDGE.matcher(c.getId());
            if (me.matches()) {
                int vi = Integer.parseInt(me.group(1));
                int ei = Integer.parseInt(me.group(2));
                Point v1 = vertexPoints.get(vi);
                Point v2 = vertexPoints.get((vi + 1) % sides);
                double t = (ei + 1) / (double)(board.getCellsPerEdge() + 1);
                int x = (int)(v1.x * (1 - t) + v2.x * t);
                int y = (int)(v1.y * (1 - t) + v2.y * t);
                positions.put(c, new Point(x, y));
            }
        }

        for (Cell c : cells) {
            Matcher md = PAT_DIAG.matcher(c.getId());
            if (md.matches()) {
                int vi = Integer.parseInt(md.group(1));
                int di = Integer.parseInt(md.group(2));
                Point vpt = vertexPoints.get(vi);
                double t = (di + 1) / 3.0;
                int x, y;
                if (vi == 0 || vi == sides - 1) {
                    // center -> vertex
                    x = (int)(center.x * (1 - t) + vpt.x * t);
                    y = (int)(center.y * (1 - t) + vpt.y * t);
                } else {
                    // vertex -> center
                    x = (int)(vpt.x * (1 - t) + center.x * t);
                    y = (int)(vpt.y * (1 - t) + center.y * t);
                }
                positions.put(c, new Point(x, y));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw connections
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            for (Cell nxt : c.getNextCells()) {
                Point q = positions.get(nxt);
                if (p != null && q != null) {
                    g2.drawLine(p.x, p.y, q.x, q.y);
                }
            }
        }

        // Draw cells and pieces
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            if (p == null) continue;
            g2.fillOval(p.x - CELL_SIZE/2, p.y - CELL_SIZE/2, CELL_SIZE, CELL_SIZE);
            int offset = 0;
            for (Piece piece : c.getOccupants()) {
                g2.setColor(new Color(
                        (piece.getOwner().getId() * 50) % 256,
                        (piece.getOwner().getId() * 80) % 256,
                        (piece.getOwner().getId() * 110) % 256
                ));
                g2.fillOval(p.x - 6 + offset, p.y - 6 + offset, 12, 12);
                offset += 14;
            }
            g2.setColor(Color.BLACK);
        }
    }
}
