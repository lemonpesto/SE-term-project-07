// src/view/swing/SwingSetupView.java
package view.swing;

import model.BoardShape;
import view.ISetupView;
import view.ISetupViewListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * SwingSetupView
 * -- Swing 기반 설정 화면.
 * 1) 플레이어 이름을 최소 2명~최대 4명까지 동적으로 추가/제거
 * 2) 플레이어당 말 개수(2~5) 선택
 * 3) 보드 모양(SQUARE, PENTAGON, HEXAGON) 선택
 * 4) 사용자가 '게임 시작'을 누르면 ISetupViewListener.onStartClicked 호출
 */
public class SwingSetupView extends JFrame implements ISetupView {
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;
    private static final int DEFAULT_PIECES_PER_PLAYER = 4;

    private final JPanel nameListPanel;
    private final List<JTextField> nameFields = new ArrayList<>();
    private final List<JButton> removeButtons = new ArrayList<>();
    private final JButton addPlayerButton;

    private final JSpinner spinnerNumPieces;
    private final JComboBox<BoardShape> comboBoardShape;
    private final JButton btnStart;

    private ISetupViewListener listener;

    public SwingSetupView() {
        super("윷놀이 게임 설정");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // 최상위 컨테이너(Content Pane) 설정
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // 상단: 제목 라벨
        JLabel lblTitle = new JLabel("윷놀이 설정");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 20f));
        content.add(lblTitle, BorderLayout.NORTH);

        // 중앙: 입력 폼 배치
        JPanel centerPane = new JPanel(new GridBagLayout());
        content.add(centerPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 1) 플레이어 이름 입력 리스트
        nameListPanel = new JPanel();
        nameListPanel.setLayout(new BoxLayout(nameListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(
                nameListPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setPreferredSize(new Dimension(400, 120));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        centerPane.add(scrollPane, gbc);
        row++;

        // 기본 2명 필드 생성
        addPlayerButton = new JButton("+ 플레이어 추가");
        for (int i = 0; i < MIN_PLAYERS; i++) {
            addNameField();
        }

        // 2) "+ 플레이어 추가" 버튼
        addPlayerButton.setFont(addPlayerButton.getFont().deriveFont(Font.BOLD, 12f));
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameFields.size() < MAX_PLAYERS) {
                    addNameField();
                }
                updateAddButtonState();
                validateStartButton();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        centerPane.add(addPlayerButton, gbc);
        row++;

        // 3) 플레이어당 말 개수 설정
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        centerPane.add(new JLabel("말 개수(플레이어당):"), gbc);

        spinnerNumPieces = new JSpinner(new SpinnerNumberModel(
                DEFAULT_PIECES_PER_PLAYER, MIN_PIECES, MAX_PIECES, 1));
        ((JSpinner.DefaultEditor) spinnerNumPieces.getEditor()).getTextField().setColumns(2);
        gbc.gridx = 1;
        centerPane.add(spinnerNumPieces, gbc);
        row++;

        // 4) 보드 모양 선택
        gbc.gridx = 0;
        gbc.gridy = row;
        centerPane.add(new JLabel("보드 모양:"), gbc);

        comboBoardShape = new JComboBox<>(BoardShape.values());
        gbc.gridx = 1;
        centerPane.add(comboBoardShape, gbc);
        row++;

        // 하단: “게임 시작” 버튼
        btnStart = new JButton("게임 시작");
        btnStart.setFont(btnStart.getFont().deriveFont(Font.BOLD, 14f));
        btnStart.setEnabled(false);
        btnStart.addActionListener(e -> {
            if (listener != null) {
                String[] names = nameFields.stream()
                        .map(tf -> tf.getText().trim())
                        .toArray(String[]::new);
                int pieces = (Integer) spinnerNumPieces.getValue();
                BoardShape shape = (BoardShape) comboBoardShape.getSelectedItem();
                listener.onStartClicked(names, pieces, shape);
            }
        });
        content.add(btnStart, BorderLayout.SOUTH);

        pack();
    }

    // ---- ISetupView 인터페이스 구현 ---- //

    @Override
    public void setSetupViewListener(ISetupViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void showView() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    @Override
    public void closeView() {
        SwingUtilities.invokeLater(() -> dispose());
    }

    // ---- 내부 Helper 매서드들 ---- //
    private void addNameField() {
        int index = nameFields.size();

        JPanel rowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 레이블
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbl = new JLabel("플레이어 " + (index + 1) + " 이름:");
        rowPanel.add(lbl, gbc);

        // 텍스트 필드
        JTextField txtName = new JTextField(15);
        gbc.gridx = 1;
        rowPanel.add(txtName, gbc);
        nameFields.add(txtName);

        // 삭제 버튼 (index >= MIN_PLAYERS일 때만 보임)
        JButton btnRemove = new JButton("–");
        btnRemove.setFont(btnRemove.getFont().deriveFont(Font.BOLD, 12f));
        btnRemove.setPreferredSize(new Dimension(45, 25));
        btnRemove.setVisible(index >= MIN_PLAYERS);
        btnRemove.setEnabled(index >= MIN_PLAYERS);

        if (index >= MIN_PLAYERS) {
            gbc.gridx = 2;
            rowPanel.add(btnRemove, gbc);
        }
        removeButtons.add(btnRemove);

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int removeIndex = nameFields.indexOf(txtName);
                if (removeIndex >= MIN_PLAYERS) {
                    nameFields.remove(txtName);
                    removeButtons.remove(btnRemove);
                    rebuildNameListPanel();
                    updateAddButtonState();
                    validateStartButton();
                }
            }
        });

        txtName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateStartButton(); }
            public void removeUpdate(DocumentEvent e) { validateStartButton(); }
            public void changedUpdate(DocumentEvent e) { validateStartButton(); }
        });

        nameListPanel.add(rowPanel);
        nameListPanel.revalidate();
        nameListPanel.repaint();

        updateAddButtonState();
    }

    private void rebuildNameListPanel() {
        nameListPanel.removeAll();
        for (int i = 0; i < nameFields.size(); i++) {
            JTextField txtName = nameFields.get(i);
            JButton btnRemove = removeButtons.get(i);

            JPanel rowPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lbl = new JLabel("플레이어 " + (i + 1) + " 이름:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            rowPanel.add(lbl, gbc);

            gbc.gridx = 1;
            rowPanel.add(txtName, gbc);

            if (i >= MIN_PLAYERS) {
                btnRemove.setVisible(true);
                btnRemove.setEnabled(true);
                gbc.gridx = 2;
                rowPanel.add(btnRemove, gbc);
            } else {
                btnRemove.setVisible(false);
                btnRemove.setEnabled(false);
            }

            nameListPanel.add(rowPanel);
        }
        nameListPanel.revalidate();
        nameListPanel.repaint();
    }

    private void validateStartButton() {
        boolean allFilled = true;
        if (nameFields.size() < MIN_PLAYERS) {
            allFilled = false;
        } else {
            for (JTextField txt : nameFields) {
                if (txt.getText().trim().isEmpty()) {
                    allFilled = false;
                    break;
                }
            }
        }
        btnStart.setEnabled(allFilled);
    }

    private void updateAddButtonState() {
        addPlayerButton.setEnabled(nameFields.size() < MAX_PLAYERS);
    }
}
