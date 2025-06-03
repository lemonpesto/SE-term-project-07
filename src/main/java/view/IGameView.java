package view;

/**
 * UI 툴킷에 무관하게 GameController가 사용할 공통 “View” 기능들만 선언해 둔 인터페이스
 * 화면 갱신/이벤트 등록용 메서드
 */
public interface IGameView {
    // 이벤트(마우스 클릭, ...) 발생 시 View가 Controller에게 알릴 수 있도록 IGameViewListener 등록함
    void setGameViewListener(IGameViewListener listener);

    // 프로그램 창 띄움
    void showWindow();

    // model(game)의 상태(말 위치, 남은 위치, ...)가 바뀔 때마다 전체 판 갱신해달라라고 요청
    void updateBoard();

    // 상태 메시지를 화면 하단의 상태 표시줄에 보여달라고 요청
    void updateStatus(String message);

    // 말 선택 기능을 활성화/비활성화 함 (ExtraTurn이 false일 때는 말 선택이 불가능하도록)
    void setPieceSelectable(boolean enabled);

    // 윷 던지기 버튼을 활성화/비활성화 함
    void setThrowEnabled(boolean enabled);

    // 게임이 끝나서 순위가 결정되었을 때 팝업창 띄워 달라고 요청
    void showWinnerDialog(String winnerName); // !! winner가 아니라 등수로 보여지도록 수정 !!
}
