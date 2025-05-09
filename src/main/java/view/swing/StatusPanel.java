package view.swing;

import javax.swing.*;
import java.awt.*;
import model.ThrowResult;

public class StatusPanel extends JPanel {
    private final JLabel resultLabel;

    public StatusPanel() {
        this.setLayout(new BorderLayout());
        resultLabel = new JLabel("결과: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        this.add(resultLabel, BorderLayout.CENTER);
    }

    /**
     * 던진 결과와 추가 턴 여부를 화면에 표시합니다.
     */
    public void updateStatus(ThrowResult result) {
        String text = String.format("결과: %s (%d칸) %s",
                result.name(),
                result.getSteps(),
                result.isExtraTurn() ? "→ 추가 던지기!" : "");
        resultLabel.setText(text);
    }
}