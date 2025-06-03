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
 * 인터페이스 기반 View(IGameView)에 의존하는 GameController.
 *
 * - IGameViewListener를 구현하여 View에서 발생한 이벤트(onThrowButtonClicked,
 *   onPieceClicked)를 받아 처리한다.
 * - Game 모델의 playOneTurn, advanceTurn, isFinished, getWinner 등을 호출하고,
 *   그 결과를 IGameView를 통해 화면에 반영한다.
 */
public class GameController implements IGameViewListener {

    private final Game game;        // 모델
    private final IGameView view;   // 뷰

    // 윷 던지기 결과들 모아둘 리스트: onThrowButtonClicked() 호출 시 윷 던지기 결과를 add 해놓다가 onPieceClicked() 때 그 턴의 윷 던지기 결과들을 사용
    private final List<ThrowResult> throwResults = new ArrayList<>();

    // 말 이동 단계에서 현재 처리 중인 ThrowResults 인덱스
    private int currThrowIndex = 0;

    // 윷 던지기 결과들을 처리하는 단계(말 이동 시작 단계) 여부 플래그: true --> 말 클릭 시 말 이동 로직 수행
    private boolean isProcessingThrows = false;

    /**
     * 생성자: 모델과 뷰를 주입받아서 컨트롤러를 초기화함
     */
    public GameController(Game game, IGameView view) {
        this.game = game;
        this.view = view;

        // 게임 상태를 IN_PROGRESS로 전환
        game.startGame();

        // 뷰에 이 컨트롤러를 이벤트 리스너로 등록
        this.view.setGameViewListener(this);

        // 초기 화면 상태: 첫 플레이어가 윷 던지기
        Player firstPlayer = game.getCurrentPlayer();
        view.updateStatus(firstPlayer.getName() + "님, 윷을 던져주세요.");

        // 초기 클릭 상태: 말 선택 비활성화, 윷 던지기 버튼 활성화
        view.setPieceSelectable(false);
        view.setThrowEnabled(true);

        // 실제 창 띄우기
        view.showWindow();
    }

    /**
     * “윷 던지기” 버튼이 클릭되면 호출됨 (IGameViewListener 인터페이스 메서드)
     */
    @Override
    public void onRandomThrowClicked() {
        Player currPlayer = game.getCurrentPlayer(); // 현재 차례 플레이어

        // 1) 랜덤하게 윷 던지기
        ThrowResult throwResult = game.getThrowService().throwRandom();
        throwResults.add(throwResult);

        // 2) extraTurn이 있으면 계속 던지기
        if (throwResult.isExtraTurn()) {
            // extraTurn == true(윷/모) --> 윷 더 던지기
            view.setThrowEnabled(true);     // 윷 던지기 버튼 활성화
            view.setPieceSelectable(false); // 아직 말 선택 X
            view.updateStatus(currPlayer.getName() + "님, 결과: " + throwResult.name() + " 입니다. 한 번 더 던져주세요!");
        } else {
            // extraTurn == false --> 윷 던지기 종료, 말 선택 및 이동 단계로 전환
            isProcessingThrows = true;
            currThrowIndex = 0;
            view.setThrowEnabled(false);    // 윷 던지기 멈추고
            view.setPieceSelectable(true);  // 말 선택
            // 윷 던지기 결과 개수에 따른 상태 메시지 결정
            if(throwResults.size() == 1) {
                // 윷 한 번 던진 경우
                if(throwResult == ThrowResult.BACK_DO && currPlayer.checkAllPiecesNotStarted()){
                    // 이동 가능한 말이 없다면 다음 턴
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + throwResult.name() + " 입니다. 이동시킬 말이 없습니다.");
                    nextTurn();
                }else{
                    view.updateStatus(currPlayer.getName() + "님, 결과: " + throwResult.name() + " 입니다. 이동시킬 말을 클릭하세요.");
                }
            }
            else{
                // 윷 여러 번 던진 경우
                view.updateStatus(currPlayer.getName() + "님, 결과: " + throwResults.get(throwResults.size() - 1).name() + " 입니다." // 현재 결과
                        + "\n첫 번째 결과: " + throwResults.get(0).name() + " 를 적용시킬 말을 클릭하세요."); // 첫 번째 결과
            }
        }
    }

    /**
     * <지정 윷 던지기> 버튼이 클릭되어
     * 사용자가 직접 선택한 ThrowResult가 전달될 때 호출됩니다.
     * extraTurn 여부 판단, 말 선택 단계 전환 등은 onRandomThrowClicked와 동일합니다.
     */
    @Override
    public void onFixedThrowClicked(ThrowResult fixedResult) {
        Player currPlayer = game.getCurrentPlayer();

        // 1) 사용자가 지정한 윷 결과 저장
        throwResults.add(fixedResult);

        // 2) extraTurn 여부
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

    /**
     * “말 클릭” 이벤트가 발생했을 때 호출됨
     */
    @Override
    public void onPieceClicked(Piece piece) {
        // 클릭된 말이 없거나 '말 이동 단계'가 아닌 상태라면 무시
        if (piece == null || !isProcessingThrows) {
            return;
        }

        Player currPlayer = game.getCurrentPlayer(); // 현재 차례 플레이어
        ThrowResult throwResult = throwResults.get(currThrowIndex++); // 현재 처리할 ThrowResult

        // 윷 결과들 말에 적용 (이동/룰 적용/종료 체크)
        game.playTurn(currPlayer, throwResult, piece);
        view.updateBoard(); // 화면 업데이트

        // 다음 ThrowResult가 남아 있는지 확인
        if(currThrowIndex < throwResults.size()) {
            // 남은 결과 있으면
            ThrowResult nextThrowResult = throwResults.get(currThrowIndex);
            view.updateStatus(currPlayer.getName() + "님, 다음 결과: " + nextThrowResult.name() + " 를 적용시킬 말을 클릭하세요.");

            // 말 선택 계속 활성화
            view.setPieceSelectable(true);
            view.setThrowEnabled(false);
        } else{
            // 모든 결과 처리 완료 시
            // 게임 종료 여부 판단
            if (game.isGameOver()) {
                String winnerName = game.getWinner().getName();
                view.showWinnerDialog(winnerName);
                return;
            }
            nextTurn();
        }
    }

    private void nextTurn(){
        isProcessingThrows = false;
        throwResults.clear();   // 윷 던지기 결과 리스트 초기화
        currThrowIndex = 0;     // 인덱스 리셋

        // 다음 플레이어로 차례 넘기기
        game.advanceTurn();
        Player nextPlayer = game.getCurrentPlayer();
        // 다음 플레이어에게 윷 던지기 안내
        view.updateStatus(nextPlayer.getName() + "님, 윷을 던져주세요.");
        // 말 선택 비활성화
        view.setPieceSelectable(false);
        view.setThrowEnabled(true);
    }
}