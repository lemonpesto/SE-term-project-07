package controller;

import model.BoardShape;
import model.Game;
import view.IGameView;
import view.ISetupViewListener;
import view.ISetupView;
import view.swing.SwingGameView;
import view.swing.SwingSetupView;

/**
 * SetupController
 *
 * -- 사용자 설정 화면에서 <게임 시작> 버튼을 눌렀을 때 전달받은 정보를 바탕으로 Game 모델 생성 후 실제 게임 화면으로 전환함
 */
public class SetupControllerSwing implements ISetupViewListener {

    private final ISetupView view;

    public SetupControllerSwing() {
        view = new SwingSetupView();
        view.setSetupViewListener(this);
        view.showView();
    }

    // 사용자가 <게임 시작> 버튼을 눌렀을 때 호출됨
    @Override
    public void onStartClicked(String[] playerNames, int piecesPerPlayer, BoardShape boardShape) {
        // Game 모델 객체 생성 및 시작
        Game game = new Game(playerNames.length, playerNames, piecesPerPlayer, boardShape);
        game.startGame();

        // 실제 게임 화면 생성
        IGameView gameView = new SwingGameView(game);

        // GameController 생성 및 연결
        new GameController(game, gameView);

        // 설정 화면 닫기
        view.closeView();
    }
}
