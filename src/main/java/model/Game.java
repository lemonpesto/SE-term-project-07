package model;

import java.util.*;

public class Game {
    private List<Player> players = new ArrayList<>(); // 현재 게임에 참여하는 플레이어들
    private List<Player> finishedPlayers = new ArrayList<>(); // 말을 모두 내보내서 윷을 던질 수 없는 플레이어들 (먼저 끝날수록 먼저 추가되므로 순위 파악에 활용 가능)
    private GameStatus gameStatus; // 현재 게임 상태
    private enum GameStatus{
        READY, // 게임 준비 중인 상태 (플레이어 수, 말 수, 보드판 모양 등 설정)
        IN_PROGRESS, // 플레이어가 윷을 던지고 말을 옮기는 실제 게임 중인 상태
        FINISHED // 모든 플레이어가 말을 내보내서 게임이 종료된 상태
    }
    BoardShape boardShape;

    //생성자 : 플레이어/말/보드 모양을 받아와서 해당 게임을 초기화
    public Game(int playerNum, String[] playerName, int piecesNum, BoardShape boardShape){
        // 플레이어 생성
        for(int i=0; i<playerNum; i++){
            this.players.add(new Player(i, playerName[i], piecesNum));
        }
        // 게임이 처음 만들어졌을 때는 READY 상태
        this.gameStatus = GameStatus.READY;
        // 게임의 보드판 모양 설정
        this.boardShape = boardShape;
    }

    // 현재 게임을 시작하여 모든 플레이어가 모든 말을 내보낼 때까지 실행 (즉, 게임 한 판을 실행)
    public void startGame(){
        gameStatus = GameStatus.IN_PROGRESS; // 게임 중 상태로 변환

        // 게임 중 상태일 때만 반복
        while (this.gameStatus == GameStatus.IN_PROGRESS) {
            for(Player currentPlayer : players) { // currentPlayer : 현재 턴인 플레이어
                // 이번 플레이어가 모든 말을 내보냈다면 이 플레이어는 더 이상 윷놀이 게임에 참여하지 않음 => 다음 플레이어에게 턴을 넘김
                if(currentPlayer.getIsFinished()){
                    continue;
                }
                // 현재 플레이어가 윷을 던지고 말을 옮김
                playTurn(currentPlayer);

                // 이번 턴을 마친 플레이어가 모든 말을 내보냈는지 확인하여 currentPlayer의 상태를 설정
                currentPlayer.isFinished();

                // 이번 턴을 마친 플레이어가 게임을 끝낸 상태라면 finishedPlayers에 넣음 => 순위에 반영
                if(currentPlayer.getIsFinished()){
                    finishedPlayers.add(currentPlayer);
                }
            }
            // 모든 플레이어가 한 턴씩 실행하고 나면 모든 플레이어가 모든 말을 내보냈는지 확인 => 게임을 종료할 조건인지 확인
            isGameOver();
        }
    }

    // 게임을 종료할 상태라면 gameStatus를 FINISHED로 바꿈 (모든 플레이어를 검사하여 모든 말을 내보냈는지 확인)
    public void isGameOver(){
        for(Player player : players){
            // 한 플레이어라도 끝난 상태가 아니라면 gameStatus를 바꾸지 않고 함수를 종료함
            if(!player.getIsFinished()){
                return;
            }
        }
        // 모든 플레이어가 끝났다면 게임을 종료할 조건이므로 gameStatus를 바꿈
        gameStatus = GameStatus.FINISHED;
    }

    // 한 플레이어가 한 턴을 실행 (윷을 던지고, 말을 옮김)
    public void playTurn(Player player){
        do{
            //player.throwYut().; // 윷을 던짐
        } while();
        // 말 선택하고 옮기기
    }

    // 플레이어 순위를 알기 위한 getter
    public List<Player> getFinishedPlayers(){
        return finishedPlayers;
    }
}