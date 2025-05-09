package view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import model.ThrowResult;

public class ControlPanel extends JPanel {
    private final JButton randomButton;
    private final JButton designatedButton;
    private final JComboBox<ThrowResult> designatedCombo;

    public ControlPanel() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 랜덤 던지기 버튼
        randomButton = new JButton("랜덤 윷 던지기");
        this.add(randomButton);

        // 지정 던지기 콤보박스 + 버튼
        designatedCombo = new JComboBox<>(ThrowResult.values());
        this.add(designatedCombo);
        designatedButton = new JButton("지정 윷 던지기");
        this.add(designatedButton);
    }

    /** 랜덤 던지기 버튼 리스너 등록 */
    public void setRandomListener(ActionListener listener) {
        randomButton.addActionListener(listener);
    }

    /** 지정 던지기 버튼 리스너 등록 */
    public void setDesignatedListener(ActionListener listener) {
        designatedButton.addActionListener(listener);
    }

    /** 콤보박스에서 선택된 ThrowResult 반환 */
    public ThrowResult getSelectedResult() {
        return (ThrowResult) designatedCombo.getSelectedItem();
    }
}