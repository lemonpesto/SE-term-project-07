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

        // 게임 시작
        game.startGame();

        // 뷰에 리스너 등록
        this.view.setGameViewListener(this);

        // 첫 번째 플레이어 안내
        Player firstPlayer = game.getCurrentPlayer();
        view.updateStatus(firstPlayer.getName() + "님, 윷을 던져주세요.");

        // 말 클릭 비활성화, 윷 던지기 활성화
        view.setPieceSelectable(false);
        view.setThrowEnabled(true);

        // 창 띄우기
        view.showWindow();
    }

    @Override
    public void onRandomThrowClicked() {
        Player currPlayer = game.getCurrentPlayer();

        // 랜덤 윷 던지기
        ThrowResult result = game.getThrowService().throwRandom();
        throwResults.add(result);

        if (result.isExtraTurn()) {
            // 추가 던지기
            view.setThrowEnabled(true);
            view.setPieceSelectable(false);
            view.updateStatus(currPlayer.getName() + "님, 결과: " + result.name() + ". 추가 던지기 기회입니다!");
        } else {
            // 말 선택 단계로 전환
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
        Player currPlayer = game.getCurrentPlayer();

        // 1) 클릭 무시 조건
        if (!isProcessingThrows || piece == null) return;
        if (piece.getOwner() != currPlayer) return;  // 다른 사람 말 클릭 방지

        // 2) 현재 처리할 ThrowResult 꺼내서 playTurn 호출
        ThrowResult tr = throwResults.get(currThrowIndex++);
        game.playTurn(currPlayer, tr, piece);
        view.updateBoard();

        // PlayTurn 후 해당 플레이어가 이 이동으로 모든 피스를 다 내보냈는지 검사
        if (currPlayer.checkAllPiecesFinished()) {
            isProcessingThrows = false;
            throwResults.clear();
            currThrowIndex = 0;

            if (game.isGameOver()) {
                // 이제 모든 플레이어가 등수 기록 완료 --> 최종 등수 다이얼로그 띄우기
                view.showRankingDialog(game.getRanking());
                return;
            }
            // 남은 throwResults는 의미가 없으므로 버리고 바로 다음 턴 처리
            nextTurn();
            return;
        }

        if (currThrowIndex < throwResults.size()) {
            // 남은 결과 있으면 --> 말 선택
            ThrowResult next = throwResults.get(currThrowIndex);
            view.updateStatus(currPlayer.getName() + "님, 다음 결과: " + next.name() + " → 이동할 말을 클릭하세요.");
            view.setPieceSelectable(true);
            view.setThrowEnabled(false);
        } else {
            // 해당 턴의 모든 ThrowResult 처리 완료 시
            if (game.isGameOver()) {
                // 이제 모든 플레이어가 등수 기록 완료 --> 최종 등수 다이얼로그 띄우기
                view.showRankingDialog(game.getRanking());
                return;
            }
            nextTurn();
        }
    }

    // 다음 플레이어로 턴 넘김
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