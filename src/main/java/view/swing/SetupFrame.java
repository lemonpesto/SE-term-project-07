package view.swing;

// 필요한 Swing 및 AWT 관련 클래스들을 가져옵니다.
import controller.GameController;
import model.BoardShape;
import model.Game;
import view.IGameView;

import javax.swing.*;                   // JFrame, JPanel, JButton, JTextField, JLabel 등
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener; // 텍스트 필드 변경을 감지하기 위한 리스너
import javax.swing.border.EmptyBorder;    // 빈 여백을 설정하기 위함
import java.awt.*;                       // LayoutManagers, Dimension, Insets 등
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * SetupFrame
 *
 * - 최종 사용자가 게임을 시작하기 전에
 *   1) 플레이어 이름을 동적으로 추가/제거 (최소 2명, 최대 4명)
 *   2) 플레이어당 말 개수(2~5)를 설정
 *   3) 보드 모양(SQUARE, PENTAGON, HEXAGON) 선택
 *   등을 입력하고 “게임 시작” 버튼을 눌러 실제 게임 화면으로 넘어가게 합니다.
 *
 * - Java Swing을 처음 접하는 사람도 이해할 수 있도록
 *   코드 곳곳에 상세 주석(한국어)을 달아두었습니다.
 */
public class SetupFrame extends JFrame {
    // 최소/최대 플레이어 수, 말 개수 범위, 기본 말 개수 등 상수 정의
    private static final int MIN_PLAYERS = 2;           // 최소 2명
    private static final int MAX_PLAYERS = 4;           // 최대 4명
    private static final int MIN_PIECES = 2;            // 말 개수 최소 2개
    private static final int MAX_PIECES = 5;            // 말 개수 최대 5개
    private static final int DEFAULT_PIECES_PER_PLAYER = 4; // 기본값 4개

    // 플레이어 이름 입력 필드를 동적으로 관리할 패널
    private final JPanel nameListPanel;
    // 실제 텍스트 입력창(JTextField)들을 보관하는 리스트
    private final java.util.List<JTextField> nameFields = new ArrayList<>();
    // 각 입력창 옆의 삭제(–) 버튼을 보관하는 리스트
    private final java.util.List<JButton> removeButtons = new ArrayList<>();
    // "+ 플레이어 추가" 버튼
    private final JButton addPlayerButton;

    // 플레이어당 말 개수를 설정하는 스피너(숫자 조절기)
    private final JSpinner spinnerNumPieces;
    // 보드 모양(SQUARE, PENTAGON, HEXAGON)을 선택하는 콤보박스
    private final JComboBox<BoardShape> comboBoardShape;
    // 모든 정보를 입력한 뒤 게임을 실제로 시작하는 버튼
    private final JButton btnStart;


    // 뷰가 “이벤트를 통보”할 리스너 타입
    public interface SetupListener {
        void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape);
    }

    private SetupListener listener; // 실제로 어떤 객체가 이 이벤트를 처리할지 할당해 놓을 필드

    public void setSetupListener(SetupListener listener) {
        this.listener = listener;
    }

    /**
     * 생성자: SetupFrame을 초기화하고 화면 구성하기
     */
    public SetupFrame() {
        super("윷놀이 설정");  // JFrame의 제목을 설정

        // 기본 닫기 동작: 창 우상단 'X' 버튼을 누르면 프로그램 종료
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 초기 창 크기를 설정 (너비 500, 높이 400)
        setSize(500, 400);
        // 창 가운데 띄우기
        setLocationRelativeTo(null);

        addPlayerButton = new JButton("+ 플레이어 추가");

        // ───────────────────────────────────────────────────────────────────────
        // 전체 레이아웃 설정
        // ───────────────────────────────────────────────────────────────────────

        // content: JFrame의 최상위 컨테이너 역할을 하는 JPanel
        // BorderLayout을 사용하여, NORTH(상단), CENTER(중앙), SOUTH(하단) 등으로 구성
        JPanel content = new JPanel(new BorderLayout(10, 10));
        // content에 바깥 여백(패딩)을 주기 위해 EmptyBorder 사용 (상하좌우 10픽셀)
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        // 기본 컨텐트 팬을 content로 설정
        setContentPane(content);

        // ───────────────────────────────────────────────────────────────────────
        // 상단 (NORTH): 안내 문구
        // ───────────────────────────────────────────────────────────────────────

        // lblTitle: 사용자가 설정 메뉴를 이해할 수 있도록 안내하는 라벨
        JLabel lblTitle = new JLabel("윷놀이");
        // 가운데 정렬
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        // 폰트를 굵게, 크기 16으로 설정
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 20f));
        // content의 상단(NORTH)에 추가
        content.add(lblTitle, BorderLayout.NORTH);

        // ───────────────────────────────────────────────────────────────────────
        // 중앙 (CENTER): 입력 폼들이 들어가는 부분
        // ───────────────────────────────────────────────────────────────────────

        // centerPane: 실제 입력 컴포넌트들을 배치할 패널
        // GridBagLayout을 사용하여 유연한 그리드(격자) 형태로 컴포넌트 배치
        JPanel centerPane = new JPanel(new GridBagLayout());
        content.add(centerPane, BorderLayout.CENTER);

        // GridBagConstraints: GridBagLayout에서 컴포넌트 배치 규칙을 지정하는 객체
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);  // 컴포넌트 주변에 8픽셀 여백
        gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
        gbc.fill = GridBagConstraints.HORIZONTAL; // 가로로 늘려서 채우기

        int row = 0; // 그리드에서 몇 번째 행에 추가할지 기억하는 변수

        // ───────────────────────────────────────────────────────────────────────
        // 1) 플레이어 이름 입력 동적 리스트
        // ───────────────────────────────────────────────────────────────────────

        // nameListPanel: 여러 개의 플레이어 이름 입력 행을 세로로 쌓아둘 패널
        // 세로 방향 BoxLayout 사용
        nameListPanel = new JPanel();
        nameListPanel.setLayout(new BoxLayout(nameListPanel, BoxLayout.Y_AXIS));

        // scrollPane: nameListPanel을 스크롤 가능하게 감싸주는 역할
        JScrollPane scrollPane = new JScrollPane(
                nameListPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,     // 필요하면 세로 스크롤바 표시
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER        // 가로 스크롤바는 절대 표시하지 않음
        );
        // 스크롤 영역의 선호 크기를 설정 (너비 400, 높이 120)
        scrollPane.setPreferredSize(new Dimension(400, 120));
        // 스크롤팬에 테두리(Border)를 없애서 깔끔하게 보이도록 함
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // GridBag 제약: 컬럼 0, 행 row(row=0), colspan=2
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;  // 두 개의 그리드 칸을 가로로 차지
        centerPane.add(scrollPane, gbc);
        row++;  // 다음 행으로 이동

        // 초기 상태: 최소 2명의 플레이어 이름 입력칸을 생성
        for (int i = 0; i < MIN_PLAYERS; i++) {
            addNameField();
        }

        // ───────────────────────────────────────────────────────────────────────
        // "+ 플레이어 추가" 버튼
        // ───────────────────────────────────────────────────────────────────────

        // 버튼 폰트를 굵게, 크기 12로 설정
        addPlayerButton.setFont(addPlayerButton.getFont().deriveFont(Font.BOLD, 12f));
        // 버튼 클릭 시 실행할 액션을 지정
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 현재 등록된 이름 필드 수가 최대(MAX_PLAYERS)보다 작으면 새 필드 추가
                if (nameFields.size() < MAX_PLAYERS) {
                    addNameField();
                }
                // '+ 플레이어 추가' 버튼 활성/비활성 상태 업데이트
                updateAddButtonState();
                // '게임 시작' 버튼 활성/비활성 상태 업데이트
                validateStartButton();
            }
        });

        // 그리드에 추가: 컬럼 0, 행 row, colspan=2
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        centerPane.add(addPlayerButton, gbc);
        row++;

        // ───────────────────────────────────────────────────────────────────────
        // 2) 플레이어당 말 개수 설정
        // ───────────────────────────────────────────────────────────────────────

        // 레이블: "말 개수(플레이어당):"
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;  // 한 칸만 사용
        centerPane.add(new JLabel("말 개수(플레이어당):"), gbc);

        // spinnerNumPieces: 숫자를 ↑↓ 버튼으로 조절할 수 있는 JSpinner
        spinnerNumPieces = new JSpinner(new SpinnerNumberModel(
                DEFAULT_PIECES_PER_PLAYER, // 초기값 4
                MIN_PIECES,                // 최소 2
                MAX_PIECES,                // 최대 5
                1                           // 증감 단위 1
        ));
        // JSpinner 내부의 텍스트 필드 너비를 2자리 정도로 설정
        ((JSpinner.DefaultEditor) spinnerNumPieces.getEditor())
                .getTextField().setColumns(2);

        // 그리드: 컬럼 1, 행 row
        gbc.gridx = 1;
        centerPane.add(spinnerNumPieces, gbc);
        row++;

        // ───────────────────────────────────────────────────────────────────────
        // 3) 보드 모양 선택
        // ───────────────────────────────────────────────────────────────────────

        // 레이블: "보드 모양:"
        gbc.gridx = 0;
        gbc.gridy = row;
        centerPane.add(new JLabel("보드 모양:"), gbc);

        // comboBoardShape: 열거형 BoardShape 값을 선택할 수 있는 JComboBox
        comboBoardShape = new JComboBox<>(BoardShape.values());
        gbc.gridx = 1;
        centerPane.add(comboBoardShape, gbc);
        row++;

        // ───────────────────────────────────────────────────────────────────────
        // 하단 (SOUTH): "게임 시작" 버튼
        // ───────────────────────────────────────────────────────────────────────

        btnStart = new JButton("게임 시작");
        // 버튼 폰트 굵게, 크기 14 지정
        btnStart.setFont(btnStart.getFont().deriveFont(Font.BOLD, 14f));
        // 초기에는 비활성화 상태, 사용자가 모든 이름을 입력해야 활성화됨
        btnStart.setEnabled(false);

        // 버튼 클릭 시, 단순히 리스너에게만 “시작 클릭됨”을 알려준다.
        btnStart.addActionListener(e -> {
            if (listener != null) {
                // ① 플레이어 이름 배열을 추출
                String[] names = nameFields.stream()
                        .map(tf -> tf.getText().trim())
                        .toArray(String[]::new);
                // ② 말 개수와 보드 모양을 가져옴
                int pieces = (Integer) spinnerNumPieces.getValue();
                BoardShape shape = (BoardShape) comboBoardShape.getSelectedItem();
                // ③ listener에게 이벤트 통보
                listener.onStartClicked(names, pieces, shape);
            }
        });

        // content의 하단(SOUTH)에 '게임 시작' 버튼 추가
        content.add(btnStart, BorderLayout.SOUTH);

        // 모든 컴포넌트를 추가한 뒤 JFrame 크기를 적절히 조절
        pack();
    }

    //──────────────────────────────────────────────────────────────────────────
    // 동적으로 "플레이어 이름" 입력 필드를 추가하는 메서드
    //──────────────────────────────────────────────────────────────────────────
    private void addNameField() {
        // 현재 nameFields.size()가 몇 명인지 결정 (0-based)
        int index = nameFields.size();

        // 하나의 “입력 행(Row)”을 담을 패널을 생성. GridBagLayout을 사용.
        JPanel rowPanel = new JPanel(new GridBagLayout());
        // GridBagConstraints를 새로 생성하여,
        // 왼쪽 정렬(anchor=WEST), 가로로 늘리기(fill=HORIZONTAL), 4픽셀 여백 등 지정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // (1) 레이블: "플레이어 N 이름:"
        gbc.gridx = 0; // 첫 번째 열
        gbc.gridy = 0; // 첫 번째 행
        JLabel lbl = new JLabel("플레이어 " + (index + 1) + " 이름:");
        rowPanel.add(lbl, gbc);

        // (2) 텍스트 필드: 실제 이름을 입력하는 곳
        JTextField txtName = new JTextField(15);
        gbc.gridx = 1; // 두 번째 열
        rowPanel.add(txtName, gbc);
        // 리스트에 저장해 두면, 나중에 이름을 가져올 때 편리
        nameFields.add(txtName);

        // (3) 삭제(–) 버튼: index >= MIN_PLAYERS(2)인 경우에만 보이도록
        JButton btnRemove = new JButton("–");
        btnRemove.setFont(btnRemove.getFont().deriveFont(Font.BOLD, 12f));
        // 기본 버튼 크기를 45x25 픽셀 정도로 설정 (적당히)
        btnRemove.setPreferredSize(new Dimension(45, 25));
        // 첫 2개의 필드는 삭제할 수 없으므로, 보이지 않도록 설정
        btnRemove.setVisible(index >= MIN_PLAYERS);
        btnRemove.setEnabled(index >= MIN_PLAYERS);

        if (index >= MIN_PLAYERS) {
            // 세 번째 이후 필드일 때 삭제 버튼을 우측 열에 추가
            gbc.gridx = 2; // 세 번째 열
            rowPanel.add(btnRemove, gbc);
        }
        removeButtons.add(btnRemove); // 나중에 참조하기 위해 리스트에 추가

        // 삭제 버튼 클릭 시 해당 입력 행을 완전히 제거하는 동작
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int removeIndex = nameFields.indexOf(txtName);
                // removeIndex >= MIN_PLAYERS인 경우만 삭제 가능
                if (removeIndex >= MIN_PLAYERS) {
                    // 리스트에서 텍스트 필드와 버튼 제거
                    nameFields.remove(txtName);
                    removeButtons.remove(btnRemove);
                    // 패널을 다시 그려서 레이블(플레이어 번호) 등을 재정렬
                    rebuildNameListPanel();
                    updateAddButtonState();
                    validateStartButton();
                }
            }
        });

        // (4) 텍스트 필드의 내용이 변경될 때마다 “게임 시작” 버튼 상태 새로고침
        txtName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateStartButton();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                validateStartButton();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                validateStartButton();
            }
        });

        // scrollPane(스크롤 가능 영역)에 이 rowPanel을 추가
        nameListPanel.add(rowPanel);
        nameListPanel.revalidate();  // 레이아웃 업데이트
        nameListPanel.repaint();

        // "+ 플레이어 추가" 버튼이 켜져 있을지 다시 체크
        updateAddButtonState();
    }

    //──────────────────────────────────────────────────────────────────────────
    // nameListPanel 전체를 재구성하여, "플레이어 N" 번호와 삭제 버튼 상태 등을 업데이트
    //──────────────────────────────────────────────────────────────────────────
    private void rebuildNameListPanel() {
        // 일단 기존 내용 모두 제거
        nameListPanel.removeAll();

        // 현재 nameFields.size()만큼 다시 행을 그려준다
        for (int i = 0; i < nameFields.size(); i++) {
            JTextField txtName = nameFields.get(i);
            JButton btnRemove = removeButtons.get(i);

            // 새로운 행 패널 생성 (GridBagLayout)
            JPanel rowPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 1) 레이블
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel lbl = new JLabel("플레이어 " + (i + 1) + " 이름:");
            rowPanel.add(lbl, gbc);

            // 2) 텍스트 필드
            gbc.gridx = 1;
            rowPanel.add(txtName, gbc);

            // 3) 삭제 버튼 (i >= MIN_PLAYERS 일 때만 보이도록)
            if (i >= MIN_PLAYERS) {
                btnRemove.setVisible(true);
                btnRemove.setEnabled(true);
                gbc.gridx = 2;
                rowPanel.add(btnRemove, gbc);
            } else {
                // 첫 두 개는 삭제 버튼 숨김
                btnRemove.setVisible(false);
                btnRemove.setEnabled(false);
            }

            // 재생성한 행을 다시 nameListPanel에 붙인다
            nameListPanel.add(rowPanel);
        }

        // 레이아웃 갱신
        nameListPanel.revalidate();
        nameListPanel.repaint();
    }

    //──────────────────────────────────────────────────────────────────────────
    // “게임 시작” 버튼 활성/비활성화 상태를 결정하는 메서드
    // 1) 최소 MIN_PLAYERS 개수(2개) 이상의 입력 필드가 존재해야 함
    // 2) 모든 필드에 빈 문자열이 아니어야 함
    //──────────────────────────────────────────────────────────────────────────
    private void validateStartButton() {
        boolean allFilled = true;

        // 필드 수가 MIN_PLAYERS(2) 미만이면 무조건 비활성화
        if (nameFields.size() < MIN_PLAYERS) {
            allFilled = false;
        } else {
            // 각 텍스트 필드를 순회하며 빈 문자열인지 검사
            for (JTextField txt : nameFields) {
                if (txt.getText().trim().isEmpty()) {
                    allFilled = false; // 하나라도 비어 있으면 false
                    break;
                }
            }
        }

        // btnStart를 활성화/비활성화
        btnStart.setEnabled(allFilled);
    }

    //──────────────────────────────────────────────────────────────────────────
    // "+ 플레이어 추가" 버튼을 활성/비활성화하는 메서드
    // - 현재 이름 필드 개수가 MAX_PLAYERS(4) 미만일 때만 활성화
    //──────────────────────────────────────────────────────────────────────────
    private void updateAddButtonState() {
        addPlayerButton.setEnabled(nameFields.size() < MAX_PLAYERS);
    }


    /**
     * Main 메서드: 프로그램 진입점
     * Swing 컴포넌트는 반드시 Event Dispatch Thread(EDT) 위에서 생성해야 안전합니다.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SetupFrame frame = new SetupFrame();
            frame.setVisible(true);
        });
    }
}
