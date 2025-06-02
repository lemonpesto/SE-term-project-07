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

        // 뷰를 초기화하고, 뷰에 이 컨트롤러를 이벤트 리스너로 등록
        view.setGameViewListener(this);

        // 초기 화면 상태: 첫 플레이어가 윷 던지기
        Player currPlayer = game.getCurrentPlayer();
        view.updateStatus(currPlayer.getName() + "님, 윷을 던져주세요.");

        // 초기 클릭 상태: “말 선택”은 비활성화
        view.setPieceSelectable(false);

        // 실제 창 띄우기
        view.showWindow();
    }

    /**
     * “윷 던지기” 버튼이 클릭되면 호출됨 (IGameViewListener 인터페이스 메서드)
     */
    @Override
    public void onThrowButtonClicked() {
        Player currPlayer = game.getCurrentPlayer(); // 현재 차례 플레이어

        // 1) 윷 던지기
        ThrowResult throwResult = game.getThrowService().throwRandom();
        throwResults.add(throwResult);

        // 2) 상태 메시지 업데이트
        view.updateStatus(currPlayer.getName() + "님, 윷 결과: " + throwResult.name() + " 입니다.");

        // 3) extraTurn 여부에 따라 윷 연속으로 던질지 결정
        if (throwResult.isExtraTurn()) {
            // extraTurn == true --> 윷 더 던지기
            view.setPieceSelectable(false);
            view.updateStatus(currPlayer.getName() + "님, 결과: " + throwResult.name() + "한 번 더 던져주세요!");
        } else {
            // extraTurn == false --> 윷 던지기 종료, 말 이동 단계로 전환
            isProcessingThrows = true;
            currThrowIndex = 0;

            // 말 선택 활성화
            view.setPieceSelectable(true);
            view.updateStatus(currPlayer.getName() + "님, " + throwResults.get(0).name() + "를 적용시킬 말을 클릭하세요.");
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
        ThrowResult throwResult = throwResults.get(currThrowIndex); // 현재 처리할 ThrowResult

        // 말 이동/룰 적용/종료 체크
        game.playTurn(currPlayer, throwResult, piece);
        // 화면 업데이트
        view.updateBoard();
        // 다음에 처리할 ThrowResult로 인덱스 없데이트
        currThrowIndex++;

        // 다음 ThrowResult가 남아 있는지 확인
        if(currThrowIndex < throwResults.size()) {
            // 남은 결과 있으면
            ThrowResult nextThrowResult = throwResults.get(currThrowIndex);
            view.updateStatus(currPlayer.getName() + "님, 다음 결과: " + nextThrowResult.name() + "를 적용시킬 말을 클릭하세요.");

            // 말 선택 계속 활성화
            view.setPieceSelectable(true);
        } else{
            // 모든 ThrowResult 처리 완료했다면
            isProcessingThrows = false;
            throwResults.clear();   // 윷 던지기 결과 리스트 초기화
            currThrowIndex = 0;     // 인덱스 리셋

            // 게임 종료 여부 확인
            if (game.isGameOver()) {
                String winnerName = game.getWinner().getName();
                view.showWinnerDialog(winnerName);
                return;
            }
        }
        nextTurn();
    }

    // 턴 종료 시 호출
    private void nextTurn() {
        // 다음 플레이어로 차례 넘기기
        game.advanceTurn();
        Player nextPlayer = game.getCurrentPlayer();
        // 다음 플레이어에게 윷 던지기 안내
        view.updateStatus(nextPlayer.getName() + "님, 윷을 던져주세요.");
        // 말 선택 비활성화
        view.setPieceSelectable(false);
    }

}