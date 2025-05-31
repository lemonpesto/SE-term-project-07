
import controller.GameController;

import model.BoardShape;
import model.Game;

import view.IGameView;
import view.SwingGameView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // (1) 모델 생성
        String[] names = {"Alice", "Bob", "Charlie"};
        Game game = new Game(3, names, 4, BoardShape.SQUARE);

        // (2) 뷰 선택: Swing, 또는 다른 UI 툴킷
        // 예를 들어, 실행 시 파라미터나 환경변수에 따라 결정할 수 있다.
        boolean useSwing = true; // (예시)
        IGameView view;
        view = new SwingGameView(game);
//        if (useSwing) {
//            view = new SwingGameView(game);
//        }
//        else {
//            view = new OtherToolkitGameView(game.getBoard());
//        }

        // (3) 컨트롤러 생성 --> 이벤트 릴레이, 화면 띄우기 시작
        GameController controller = new GameController(game, view);
    }
}

