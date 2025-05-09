package view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private final JButton throwButton;

    public ControlPanel() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        throwButton = new JButton("윷 던지기");
        this.add(throwButton);
    }

    /**
     * 윷 던지기 버튼에 리스너를 등록합니다.
     */
    public void setThrowListener(ActionListener listener) {
        throwButton.addActionListener(listener);
    }
}