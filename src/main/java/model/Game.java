package model;

import service.MoveActionService;
import service.RuleEngine;
import service.YutThrowService;

import java.util.*;

/**
 * 게임 한 판을 실행하는 클래스
 */
public class Game {
    private final Board board;
    private List<Player> players; // 현재 게임에 참여하는 플레이어들
    private List<Player> finishedPlayers; // 말을 모두 내보내서 윷을 던질 수 없는 플레이어들 (순위 파악)
    private int currentPlayerIndex = 0; // 지금 차례인 플레이어의 인덱스

    // 보드 및 서비스
    private final YutThrowService yutThrowService;
    private final MoveActionService moveActionService;
    private final RuleEngine ruleEngine;

    // 게임 상태: READY --> IN_PROGRESS --> FINISHED
    private GameStatus gameStatus; // 현재 게임 상태

    private enum GameStatus{
        READY, // 게임 준비 중인 상태 (플레이어 수, 말 수, 보드판 모양 등 설정)
        IN_PROGRESS, // 플레이어가 윷을 던지고 말을 옮기는 실제 게임 중인 상태
        FINISHED // 모든 플레이어가 말을 내보내서 게임이 종료된 상태
    }

    //생성자 : 플레이어/말/보드 모양을 받아와서 해당 게임을 초기화
    public Game(int playersNum, String[] playerNames, int piecesNum, BoardShape boardShape){
        this.players = new ArrayList<>();
        this.finishedPlayers = new ArrayList<>();

        // 보드 생성
        this.board = new Board(boardShape);

        // 서비스 초기화
        this.ruleEngine = new RuleEngine();
        this.yutThrowService = new YutThrowService();
        this.moveActionService = new MoveActionService(ruleEngine);

        // 각 플레이어 객체 생성
        for(int i=0; i<playersNum; i++){
            Player p = new Player(i, playerNames[i], piecesNum, board.getStartCell());
            this.players.add(p);
        }

        // 초기 게임 상태
        this.gameStatus = GameStatus.READY; // 게임이 처음 만들어졌을 때는 READY 상태
    }

    // 게임을 종료할 상태라면 gameStatus를 FINISHED로 바꿈 (모든 플레이어를 검사하여 모든 말을 내보냈는지 확인)
    public void checkGameOver(){
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
    public void playTurn(Player player, ThrowResult throwResult, Piece selected) {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            return;
        }

        // 1) 윷 던지기를 모아두기 (Extra Turn 포함)
        List<ThrowResult> results = new ArrayList<>();
        ThrowResult result;
        do {
            YutThrowService yt = new YutThrowService();
            result = yt.throwRandom();
            results.add(result);
        } while (result.isExtraTurn());

        // Piece(s) 이동
        moveActionService.movePiece(selected, throwResult, this);

        // 이동 후 해당 플레이어가 모든 피스를 내보냈는지 검사
        if (player.getIsFinished() && !finishedPlayers.contains(player)) {
            // 아직 기록되지 않은 플레이어라면 finishedPlayers에 추가
            finishedPlayers.add(player);
        }

        // 만약 이 턴에서 마지막 플레이어까지 끝났다면, gameStatus 변경
        if (isGameOver()) {
            gameStatus = GameStatus.FINISHED;
        }
    }


    /**
     * 다음 턴의 플레이어로 인덱스를 전환함
     */
    public void advanceTurn() {
        if (gameStatus != GameStatus.IN_PROGRESS) return;

        // 1) finished 상태인 플레이어는 건너뛰기
        int n = players.size();
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % n;
        } while (players.get(currentPlayerIndex).getIsFinished());

        // 결과: new currentPlayerIndex에 해당하는 플레이어가 차례가 됨.
    }

    // —— Getter / Helper 메서드 —— //

    // 게임을 실제로 시작하여 IN_PROGRESS로 전환하는 메서드
    public void startGame() {
        if (this.gameStatus == GameStatus.READY) {
            this.gameStatus = GameStatus.IN_PROGRESS;
        }
    }

    // 현재 차례인 Player 객체 반환
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    //players getter
    public List<Player> getPlayers(){
        return players;
    }

    // 게임이 FINISHED 상태인지 (즉, 모든 플레이어가 말을 다 끝냈는지) 여부 판단
    public Boolean isGameOver(){
        return players.size()==finishedPlayers.size();
    }

    // 등수(순서대로 finishOrder에 저장된 Player 리스트) 반환
    public List<Player> getRanking() {
        return new ArrayList<>(finishedPlayers);
    }

    // 플레이어 순위를 알기 위한 getter
    public List<Player> getFinishedPlayers(){
        return finishedPlayers;
    }

    public YutThrowService getThrowService() {
        return yutThrowService;
    }

    //board getter
    public Board getBoard(){
        return board;
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

}