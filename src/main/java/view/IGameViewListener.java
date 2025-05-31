package view;

import model.Piece;

/**
 * IGameView와 짝을 이루는 Listener 인터페이스.
 * --> 실제 이벤트가 발생했을 때 GameController에 알려 주는 용도.
 */
public interface IGameViewListener {
    /** “윷 던지기” 버튼이 눌렸을 때 호출 */
    void onThrowButtonClicked();

    /** “말 선택” 버튼이 눌렸을 때 호출 */
    void onPieceClicked(Piece oiece);
}