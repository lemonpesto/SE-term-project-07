package view.swing;

import controller.GameController;
import service.YutThrowService;
import model.ThrowResult;

import javax.swing.*;
import java.awt.*;

public class SwingGameView {
    private final JFrame frame;
    private final ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private final YutThrowService throwService;


    public SwingGameView() {
        this.throwService = new YutThrowService();

        frame = new JFrame("윷 던지기 데모");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        controlPanel = new ControlPanel();
        statusPanel = new StatusPanel();

        // 이벤트 등록
        controlPanel.setRandomListener(e -> onRandomThrow());
        controlPanel.setDesignatedListener(e -> onDesignatedThrow());

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(statusPanel, BorderLayout.CENTER);
        frame.setSize(500, 250);
        frame.setLocationRelativeTo(null);
    }

    /** 랜덤 윷 던지기 */
    private void onRandomThrow() {
        ThrowResult result = throwService.throwRandom();
        statusPanel.updateStatus(result);
    }

    /** 지정 윷 던지기 */
    private void onDesignatedThrow() {
        ThrowResult selected = controlPanel.getSelectedResult();
        ThrowResult result = throwService.throwDesignated(selected);
        statusPanel.updateStatus(result);
    }

    /** 화면 표시 */
    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}