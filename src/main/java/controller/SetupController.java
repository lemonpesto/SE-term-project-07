package controller;

import model.BoardShape;
import model.Game;
import view.IGameView;
import view.swing.SwingGameView;
import view.swing.SetupFrame;

// SetupController.java (또는 Main.java 같은 곳에 작성)
public class SetupController implements SetupFrame.SetupListener {
    private final SetupFrame view;

    public SetupController() {
        view = new SetupFrame();
        view.setSetupListener(this);
        view.setVisible(true);
    }

    // 사용자가 “게임 시작” 버튼을 눌렀을 때 호출되는 메서드
    @Override
    public void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape) {
        // 1) 입력된 playerNames, piecesPerPlayer, boardShape를 이용해 Game 객체 생성
        Game game = new Game(playerNames.length, playerNames, piecesPerPlayer, boardShape);
        game.startGame();

        // 2) 실제 게임 화면 생성
        IGameView gameView = new SwingGameView(game);

        // 3) GameController 생성 및 연결
        new GameController(game, gameView);

        // 4) SetupFrame 창 닫기
        view.dispose();
    }
}
