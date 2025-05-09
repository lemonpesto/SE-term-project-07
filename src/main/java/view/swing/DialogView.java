package view.swing;

import javax.swing.*;
import java.awt.*;
import config.GameConfig;
import model.BoardShape;

/**
 * 게임 시작 전 설정(참가자 수, 말 개수, 보드 형태)을 입력받는 다이얼로그
 */
public class DialogView extends JDialog {
    private JComboBox<Integer> playerCountBox;
    private JComboBox<Integer> pieceCountBox;
    private JComboBox<BoardShape> shapeBox;
    private GameConfig resultConfig;

    public DialogView(Frame owner) {
        super(owner, "게임 설정", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("참가자 수 (2~4):"), gbc);
        gbc.gridx = 1;
        playerCountBox = new JComboBox<>(new Integer[]{2, 3, 4});
        add(playerCountBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("말 개수 (2~5):"), gbc);
        gbc.gridx = 1;
        pieceCountBox = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        add(pieceCountBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("보드 형태:"), gbc);
        gbc.gridx = 1;
        shapeBox = new JComboBox<>(BoardShape.values());
        add(shapeBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> onCancel());

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onOk() {
        int players = (Integer) playerCountBox.getSelectedItem();
        int pieces = (Integer) pieceCountBox.getSelectedItem();
        BoardShape shape = (BoardShape) shapeBox.getSelectedItem();
        // 콤보박스로부터 제공되는 값은 항상 유효하므로 추가 검증 없이 설정
        this.resultConfig = new GameConfig(players, pieces, shape);
        dispose();
    }

    private void onCancel() {
        this.resultConfig = null;
        dispose();
    }

    /**
     * 다이얼로그를 표시하고, 사용자가 확인을 누르면 GameConfig 반환
     * 취소 시 null 반환
     */
    public GameConfig showDialog() {
        setVisible(true);
        return resultConfig;
    }
}
