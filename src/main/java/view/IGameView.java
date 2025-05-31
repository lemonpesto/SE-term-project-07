package view;

/**
 * UI 툴킷에 무관하게 GameController가 사용할 공통 “View” 기능들만 선언해 둔 인터페이스
 */
public interface IGameView {
    /**
     * 프로그램 창 띄움
     */
    void showWindow();

    /**
     * 보드판 전체를 다시 그려야 할 때 호출 (예: 말이 이동된 직후, repaint처럼)
     */
    void repaintBoard();

    /**
     * Status 영역(“현재 차례: X님”, “윷 결과: 개(2)” 등)을 갱신한다.
     * @param message  - 화면 하단이나 상단에 띄울 문자열
     */
    void updateStatus(String message);

    /** “승리자 이름”을 받아 팝업으로 띄움 */
    void showWinnerDialog(String winnerName);

    /**
     * 컨트롤러가 버튼 클릭을 받을 수 있도록 리스너(또는 콜백)를 등록한다.
     * (각 UI 툴킷마다 버튼 객체 참조가 다르니,
     * 구체 구현체에서 내부 버튼을 붙이고,
     * 이 메서드를 통해 IGameViewListener를 달아 주는 식)
     */
    void setGameViewListener(IGameViewListener listener);

    /**
     * “말 선택” 버튼을 활성화/비활성화 한다. (ExtraTurn이 false일 때는 말 선택이 불가능하도록)
     */
    void setPieceSelectable(boolean enabled);
}
