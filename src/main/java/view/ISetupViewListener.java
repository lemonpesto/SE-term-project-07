package view;

import model.BoardShape;

/**
 * ISetupViewListener
 * -- 사용자가 설정 화면에서 '게임 시작' 버튼을 눌렀을 때 호출되는 콜백 인터페이스
 * -- SwingSetupView는 이 리스너를 통해 컨트롤러에게 플레이어 이름 리스트, 말 개수, 보드 모양 정보를 전달함
 */
public interface ISetupViewListener {
    // 사용자가 '게임 시작' 버튼을 눌렀을 때 호출됨
    void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape);
}