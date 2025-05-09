// ControlPanel.java
package view.swing;

import model.ThrowResult;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private JButton throwButton;
    private JLabel resultLabel;

    public ControlPanel(ActionListener throwListener) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        throwButton = new JButton("윷 던지기");
        throwButton.addActionListener(throwListener);
        add(throwButton);

        resultLabel = new JLabel("결과: -");
        resultLabel.setFont(new Font("Serif", Font.BOLD, 18));
        add(resultLabel);
    }

    public void setResult(ThrowResult res) {
        resultLabel.setText("결과: " + res.name() +
                " (이동 " + res.getSteps() +
                (res.isExtraTurn() ? ", 추가 턴" : "") + ")");
    }
}
