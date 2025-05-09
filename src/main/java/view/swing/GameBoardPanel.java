package view.swing;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GameBoardPanel extends JPanel {
    private final Board board;
    private final Map<Cell, Point> positions = new HashMap<>();
    private final int CELL_SIZE = 20;
    private Piece selectedPiece;

    public interface PieceSelectionListener {
        void onPieceSelected(Piece piece);
    }

    private PieceSelectionListener listener;

    public void setPieceSelectionListener(PieceSelectionListener listener) {
        this.listener = listener;
    }

    public GameBoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
        computeSquarePositions(); // 사각형 위치 계산
        setupMouseListener();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point click = e.getPoint();
                for (Map.Entry<Cell, Point> entry : positions.entrySet()) {
                    Point p = entry.getValue();
                    if (p.distance(click) <= CELL_SIZE) {
                        Cell clickedCell = entry.getKey();
                        List<Piece> occupants = clickedCell.getOccupants();
                        if (!occupants.isEmpty()) {
                            selectedPiece = occupants.get(0);
                            if (listener != null) listener.onPieceSelected(selectedPiece);
                            repaint();
                        }
                        break;
                    }
                }
            }
        });
    }

    private void computeSquarePositions() {
        int gap = 50;
        int sizePerEdge = 4;
        Point center = new Point(getPreferredSize().width / 2, getPreferredSize().height / 2);

        // 꼭짓점 V0~V3
        Point[] vertices = {
                new Point(center.x - gap * 3, center.y - gap * 3), // V0
                new Point(center.x + gap * 3, center.y - gap * 3), // V1
                new Point(center.x + gap * 3, center.y + gap * 3), // V2
                new Point(center.x - gap * 3, center.y + gap * 3)  // V3
        };

        for (int i = 0; i < 4; i++) {
            positions.put(getCellById("V" + i), vertices[i]);
        }

        for (int i = 0; i < 4; i++) {
            Point from = vertices[i];
            Point to = vertices[(i + 1) % 4];

            int dx = (to.x - from.x) / (sizePerEdge + 1);
            int dy = (to.y - from.y) / (sizePerEdge + 1);

            for (int j = 0; j < sizePerEdge; j++) {
                String id = "E" + i + "_" + j;
                Cell c = getCellById(id);
                if (c != null) {
                    int x = from.x + dx * (j + 1);
                    int y = from.y + dy * (j + 1);
                    positions.put(c, new Point(x, y));
                }
            }
        }

        // 중앙 C
        Cell centerCell = getCellById("C");
        if (centerCell != null) {
            positions.put(centerCell, center);
        }

        // 대각선 셀
        for (Cell c : board.getCells()) {
            if (!positions.containsKey(c) && c.getId().startsWith("D")) {
                Cell parent = findParentCell(c);
                if (parent != null && positions.containsKey(parent)) {
                    Point from = positions.get(parent);
                    int x = (center.x + from.x) / 2;
                    int y = (center.y + from.y) / 2;
                    positions.put(c, new Point(x, y));
                }
            }
        }
    }

    private Cell findParentCell(Cell child) {
        for (Cell c : board.getCells()) {
            if (c.getNextCells().contains(child)) return c;
        }
        return null;
    }

    private Cell getCellById(String id) {
        for (Cell c : board.getCells()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 선 그리기
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            for (Cell nxt : c.getNextCells()) {
                Point q = positions.get(nxt);
                if (p != null && q != null) {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(p.x, p.y, q.x, q.y);
                }
            }
        }

        // 셀 및 말 그리기
        for (Cell c : board.getCells()) {
            Point p = positions.get(c);
            if (p == null) continue;

            g2.setColor(Color.DARK_GRAY);
            g2.fillOval(p.x - CELL_SIZE / 2, p.y - CELL_SIZE / 2, CELL_SIZE, CELL_SIZE);
            g2.setColor(Color.WHITE);
            g2.drawString(c.getId(), p.x - 10, p.y - 10);

            int offset = 0;
            for (Piece piece : c.getOccupants()) {
                int px = p.x - 6 + offset;
                int py = p.y - 6;

                Color color = getPlayerColor(piece.getOwner().getId());
                g2.setColor(color);
                g2.fillOval(px, py, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawOval(px, py, 12, 12);

                if (piece == selectedPiece) {
                    g2.setColor(Color.YELLOW);
                    g2.drawOval(px - 2, py - 2, 16, 16);
                }

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString(String.valueOf(piece.getId()), px + 3, py + 10);

                offset += 14;
            }
        }
    }

    private Color getPlayerColor(int playerId) {
        return switch (playerId % 4) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void clearSelection() {
        selectedPiece = null;
        repaint();
    }
}
