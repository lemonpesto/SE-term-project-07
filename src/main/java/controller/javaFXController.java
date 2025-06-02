package controller;

import javafx.scene.control.Button;
import model.Game;
import model.Piece;
import model.Player;
import model.ThrowResult;

import view.IGameViewListener;
import view.IGameView;

import java.util.ArrayList;
import java.util.List;

//javafx관련
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class javaFXController {

    @FXML



    private final Game game;        // 모델


    // 윷 던지기 결과들 모아둘 리스트: onThrowButtonClicked() 호출 시 윷 던지기 결과를 add 해놓다가 onPieceClicked() 때 그 턴의 윷 던지기 결과들을 사용
    private final List<ThrowResult> throwResults = new ArrayList<>();
    // 말 이동 단계에서 현재 처리 중인 ThrowResults 인덱스
    private int currThrowIndex = 0;
    // 윷 던지기 결과들을 처리하는 단계(말 이동 시작 단계) 여부 플래그: true --> 말 클릭 시 말 이동 로직 수행
    private boolean isProcessingThrows = false;




    public javaFXController(Game game){
        this.game = game;

        // 게임 상태를 IN_PROGRESS로 전환
        game.startGame();

        // 초기 화면 상태: 첫 플레이어가 윷 던지기
        Player first = game.getCurrentPlayer();

        notice.setText("게임 준비 완료" + first.getName() + "님, 윷을 던져주세요.");

        // 초기 클릭 상태: “말 선택”은 비활성화
        view.setPieceSelectable(false);

    }
    @Override
    public void onThrowButtonClicked() {
        Player current = game.getCurrentPlayer(); // 현재 차례 플레이어

        // 1) 윷 던지기
        ThrowResult throwResult = game.getThrowService().throwRandom();
        throwResults.add(throwResult);

        // 2) 상태 메시지 업데이트
        view.updateStatus(current.getName() + "님, 윷 결과: " + throwResult.name() + " 입니다.");

        // 3) extraTurn 여부에 따라 윷 던지기 종료 결정
        if (throwResult.isExtraTurn()) {
            // extraTurn == true --> 윷 더 던지기
            view.setPieceSelectable(false);
            view.updateStatus(current.getName() + "님, 한 번 더 윷을 던져주세요!");
        } else {
            // extraTurn == false --> 윷 던지기 종료, 말 이동 단계로 전환
            isProcessingThrows = true;
            currThrowIndex = 0;

            // 첫 번째 던진 결과에 대한 말 선택 활성화
            view.setPieceSelectable(true);
            view.updateStatus(current.getName() + "님, 첫 번째 결과: " + throwResults.get(throwResults.size() - 1).name() + " --> 이동할 말을 클릭하세요.");
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
        view.repaintBoard();
        // 다음에 처리할 ThrowResult로 인덱스 없데이트
        currThrowIndex++;

        // 다음 ThrowResult가 남아 있는지 확인
        if(currThrowIndex < throwResults.size()) {
            // 남은 결과 있으면
            ThrowResult nextThrowResult = throwResults.get(currThrowIndex);
            view.updateStatus(currPlayer.getName() + "님, 다음 결과: " + nextThrowResult.name() + " --> 이동할 말을 클릭하세요.");

            // 말 선택 계속 활성화
            view.setPieceSelectable(true);
        } else{
            // 모든 ThrowResult 처리 완료했다면
            isProcessingThrows = false;
            throwResults.clear();   // 윷 던지기 결과 리스트 초기화
            currThrowIndex = 0;     // 인덱스 리셋

            // 게임 종료 여부 확인
            if (game.isFinished()) {
                String winnerName = game.getWinner().getName();
                view.showWinnerDialog(winnerName);
                return;
            }
            // 다음 플레이어로 차례 넘기기
            game.advanceTurn();
            Player nextPlayer = game.getCurrentPlayer();
            // 다음 플레이어에게 윷 던지기 안내
            view.updateStatus(nextPlayer.getName() + "님, 윷을 던져주세요.");
            // 말 선택 비활성화
            view.setPieceSelectable(false);
        }
    }

}