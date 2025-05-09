package view.swing;

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
        // 서비스 인스턴스 생성 (Random 주입용 생성자 사용 가능)
        this.throwService = new YutThrowService();

        frame = new JFrame("윷 던지기 데모");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        controlPanel = new ControlPanel();
        statusPanel = new StatusPanel();

        // 컨트롤 패널에 버튼 리스너 등록
        controlPanel.setThrowListener(e -> onThrowYut());

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(statusPanel, BorderLayout.CENTER);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
    }

    private void onThrowYut() {
        // 던지기 실행 후 결과 화면 업데이트
        ThrowResult result = throwService.throwYut();
        statusPanel.updateStatus(result);
    }

    /**
     * 화면을 보이게 합니다.
     */
    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}