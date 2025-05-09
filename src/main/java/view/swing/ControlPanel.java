package view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import model.Piece;
import model.ThrowResult;

public class ControlPanel extends JPanel {
    private final JComboBox<Integer> pieceCombo;
    private final JButton randomBtn;
    private final JComboBox<ThrowResult> throwCombo;
    private final JButton designatedBtn;

    public ControlPanel() {
        pieceCombo = new JComboBox<>();
        randomBtn = new JButton("랜덤 던지기");
        throwCombo = new JComboBox<>(ThrowResult.values());
        designatedBtn = new JButton("지정 던지기");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("말 선택:"));
        add(pieceCombo);
        add(randomBtn);
        add(throwCombo);
        add(designatedBtn);
    }

    public void setupPieceSelector(java.util.List<Piece> pieces) {
        pieceCombo.removeAllItems();
        for (Piece p : pieces) {
            pieceCombo.addItem(p.getId());
        }
    }

    public int getSelectedPieceId() {
        return (Integer) pieceCombo.getSelectedItem();
    }

    public void setRandomListener(ActionListener l) {
        randomBtn.addActionListener(l);
    }

    public void setDesignatedListener(ActionListener l) {
        designatedBtn.addActionListener(l);
    }

    public ThrowResult getSelectedThrow() {
        return (ThrowResult) throwCombo.getSelectedItem();
    }

    public ThrowResult getSelectedResult() {
        return getSelectedThrow();
    }
}