package view;

import model.Piece;

/**
 * View(UI 컴포넌트)에서 이벤트가 발생했을 때 GameController에게 알려주는 용도
 * --> Controller는 이 콜백을 받아서 모델(Game)에게 기능 요청함
 */
public interface IGameViewListener {
    // “윷 던지기” 버튼이 눌렸을 때 호출
    void onThrowButtonClicked();
    // 말이 클릭됐을 때 호출 */
    void onPieceClicked(Piece piece);
}