package view;

import model.Piece;
import model.ThrowResult;

/**
 * View(UI 컴포넌트)에서 이벤트가 발생했을 때 GameController에게 알려주는 용도
 */
public interface IGameViewListener {
    // <랜덤 윷 던지기> 버튼이 눌렸을 때 호출
    void onRandomThrowClicked();
    // <지정 윷 던지기> 버튼이 눌렸을 때 호출
    void onFixedThrowClicked(ThrowResult result);
    // 말이 클릭됐을 때 호출
    void onPieceClicked(Piece piece);
}