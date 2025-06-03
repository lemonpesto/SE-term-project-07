// src/controller/GameController.java
package controller;

import model.Game;
import model.Piece;
import model.Player;
import model.ThrowResult;
import view.IGameViewListener;
import view.IGameView;

import java.util.ArrayList;
import java.util.List;

/**
 * GameController
 *
 * - IGameViewListener를 구현하여 View에서 발생한 이벤트(onRandomThrowClicked,
 *   onFixedThrowClicked, onPieceClicked)를 처리합니다.
 * - Game 모델의 playTurn, advanceTurn, isGameOver, getWinner 등을 호출하고,
 *   그 결과를 IGameView를 통해 화면에 반영합니다.
 * - 본인 차례에만 본인 소유의 말 선택을 허용하도록 소유주 검사를 추가하였습니다.
 */
public class GameController implements IGameViewListener {

    private final Game game;
    private final IGameView view;

    private final List<ThrowResult> throwResults = new ArrayList<>();
    private int currThrowIndex = 0;
    private boolean isProcessingThrows = false;

    public GameController(Game game, IGameView view) {
        this.game = game;
        this.view = view;

        game.startGame();
        this.view.setGameViewListener(this);

        Player firstPlayer = game.getCurrentPlayer();
        view.updateStatus(firstPlayer.getName() + "님, 윷을 던져주세요.");

        view.setPieceSelectable(false);
        view.setThrowEnabled(true);

        view.showWindow();
    }

    @Override
    public void onRandomThrowClicked() {
        Player currPlayer = game.getCurrentPlayer();

        ThrowResult result = game.getThrowService().throwRandom();
        throwResults.add(result);

        if (result.isExtraTurn()) {
            view.setThrowEnabled(true);
            view.setPieceSelectable(false);
            view.updateStatus(currPlayer.getName() + "님, 결과: " + result.name() + ". 추가 던지기 기회입니다!");
        } else {
            isProcessingThrows = true;
            currThrowIndex = 0;
            view.setThrowEnabled(false);
            view.setPieceSelectable(true);

            if (throwResults.size() == 1) {
                if (result == ThrowResult.BACK_DO && currPlayer.checkAllPiecesNotStarted()) {
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + result.name() + ". 이동할 말이 없습니다.");
                    nextTurn();
                } else {
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + result.name() + ". 이동할 말을 클릭하세요.");
                }
            } else {
                ThrowResult first = throwResults.get(0);
                ThrowResult last = throwResults.get(throwResults.size() - 1);
                view.updateStatus(currPlayer.getName() + "님, 현재 결과: " + last.name()
                        + " (첫 결과: " + first.name() + ") → 이동할 말을 클릭하세요.");
            }
        }
    }

    @Override
    public void onFixedThrowClicked(ThrowResult fixedResult) {
        Player currPlayer = game.getCurrentPlayer();

        throwResults.add(fixedResult);

        if (fixedResult.isExtraTurn()) {
            view.setThrowEnabled(true);
            view.setPieceSelectable(false);
            view.updateStatus(currPlayer.getName() + "님, 결과: " + fixedResult.name() + ". 추가 던지기 기회입니다!");
        } else {
            isProcessingThrows = true;
            currThrowIndex = 0;
            view.setThrowEnabled(false);
            view.setPieceSelectable(true);

            if (throwResults.size() == 1) {
                if (fixedResult == ThrowResult.BACK_DO && currPlayer.checkAllPiecesNotStarted()) {
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + fixedResult.name() + ". 이동할 말이 없습니다.");
                    nextTurn();
                } else {
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + fixedResult.name() + ". 이동할 말을 클릭하세요.");
                }
            } else {
                ThrowResult first = throwResults.get(0);
                ThrowResult last = throwResults.get(throwResults.size() - 1);
                view.updateStatus(currPlayer.getName() + "님, 현재 결과: " + last.name()
                        + " (첫 결과: " + first.name() + ") → 이동할 말을 클릭하세요.");
            }
        }
    }

    @Override
    public void onPieceClicked(Piece piece) {
        if (piece == null || !isProcessingThrows) {
            return;
        }

        Player currPlayer = game.getCurrentPlayer();
        // 소유주 검사: 본인 차례에만 본인 소유 말 이동 가능
        if (!piece.getOwner().equals(currPlayer)) {
            // 타인의 말 클릭 시 무시
            return;
        }

        ThrowResult tr = throwResults.get(currThrowIndex++);
        game.playTurn(currPlayer, tr, piece);
        view.updateBoard();

        if (currThrowIndex < throwResults.size()) {
            ThrowResult next = throwResults.get(currThrowIndex);
            view.updateStatus(currPlayer.getName() + "님, 다음 결과: " + next.name() + " → 이동할 말을 클릭하세요.");
            view.setPieceSelectable(true);
            view.setThrowEnabled(false);
        } else {
            if (game.isGameOver()) {
                List<Player> ranking = game.getRanking();
                view.showRankingDialog(ranking);
                return;
            }
            nextTurn();
        }
    }

    private void nextTurn() {
        isProcessingThrows = false;
        throwResults.clear();
        currThrowIndex = 0;

        game.advanceTurn();
        Player nextPlayer = game.getCurrentPlayer();
        view.updateStatus(nextPlayer.getName() + "님, 윷을 던져주세요.");
        view.setPieceSelectable(false);
        view.setThrowEnabled(true);
    }
}