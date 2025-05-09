package model;

import java.util.*;

import config.GameConfig;
import service.RuleEngine;

public class Game {
    List<Player> players = new ArrayList<>();
    BoardShape boardShape;
    Player currentPlayer = new Player();
    GameStatus gameStatus;
    GameConfig config;

    public Board getBoard() {

    }

    //Scanner sc = new Scanner(System.in);

    public enum GameStatus{
        READY, IN_PROGRESS, FINISHED
    }


    //모나 윷 나왓을대 한번더 하는 기능 넣기
    //extra윷

    //생성자. 플레이어 정보(ID,이름,말 수) 설정 및 boardshape 저장.
    public Game(GameConfig config, int playerNum, String[] playerName, int piecesNum, BoardShape boardShape){
        this.config = config;

        gameStatus = GameStatus.READY; // 나중에 생각해볼래말래볼래말래 봐야되긴해

        //플레이어 정보 설정
        Player[] players = new Player[playerNum];
        for(int i=0; i<playerNum; i++){ //각 플레이어 생성
            players[i] = new Player(i, playerName[i], piecesNum);
        }

        //boardshape설정
        this.boardShape = boardShape;

        Execute();   //실제 게임 동작
    }

    void Execute(){ //게임 시작
        gameStatus = GameStatus.IN_PROGRESS
        //여기에서 첫번째 턴 할당.
        //이 안에서 다 진행하는 게 나을듯?

        while(gameStatus!=GameStatus.FINISHED) {
            currentPlayer = players.get(0);
            playTurn();
        }
        /*
            --playTurn--
            윷던지기 버튼 클릭
            윷 랜덤 결과 생성
            결과 표시하고
                한번 더 해야되면 한번더 하고
            말 선택 활성화 하고

            *여러 번 던졌으면 윷 결과 가지고 이거 여러번 반복.
            말 선택 하고
            목표 칸 계산하고
            말 이동시키고 (그리고 보드 갱신하고?)

            --TurnCheck--
                이동시킨 후에, 말 여러개가 같은 칸인지 검사하고
                여러개가 같은 칸 맞으면 그룹으로 만들고(업고)
                그룹이 된 말들을 담 턴부터 같이 이동하고

                이동시킨 후에, 상대방 말이랑 같은 칸인지 검사하고
                맞으면 상대 말 제거하고
                상대 말 시작위치로 돌려보내고
                보드 갱신해서 변화 반영하고

                플레이어의 모든 말이 도착했는지 검사하고
                다 도착한 거 있으면 그 사람을 승자로 선언하고
                게임 종료 화면 표시하고, 재시작|종료 옵션 제공하기.

        */

    }

    void End(){
        if(gameStatus != GameStatus.FINISHED) System.exit(1); //그냥 현 상태 finished맞는지 함 더 체크하는 거.
        //만약....finished도 아닌데 왓다면...걍 강종.
        while(true){
            System.out.println("\n재시작(1) or 종료(0) 선택하세요");
            Scanner sc = new Scanner(System.in);
            int select = sc.nextInt();

            if(select == 0) config.exitGame();
            else if(select==1) config.restartGame();
            else {
                System.out.println("\n다시 입력하세요.");
            }
        }
    }

    void playTurn(){
        //turn실행.
        //말이 도착한 cell필요.
        TurnCheck(cell);
        nextTurn();
    }
    void TurnCheck(){   //각 턴마다 업기, 잡기, 승리가능한지 조건을 체크하고 -> 가능하면 수행.
        if(RuleEngine.applyGrouping(cell)){ //업기 조건 체크
            //업기
        }
        if(RuleEngine.applyCapture(cell)){  //잡기 조건 체크
            //잡기
        }
        if(RuleEngine.applyVictoryCheck(currentPlayer)){    //현 플레이어가 승자인지 확인 -> 맞으면 현 플레이어 반환하고?, 게임상태 finished로 바꾸고, end()호출.
            Player victoryPlayer = currentPlayer;   //승자 플레이어를 화면에 표시해야 해서 일단 쓰일까 싶어서 만들어둠
            gameStatus = GameStatus.FINISHED;
            End();
        }
    }

    void nextTurn(){
        //다음 플레이어에게 턴 전환
        if(currentPlayer == players[players.length -1]){
            currentPlayer = players[0];
        }
        else{
            int currentIndex = 0;   //초기값은 그냥 아무거나 설정한거..
            for(int i=0;i< players.length;i++){     //플레이어 배열 중 현 플레이어의 인덱스값. (더 간단하게 할 수 있지 않을까?)
                if(players[i]==currentPlayer) currentIndex = i;
            }

            currentPlayer = players[currentIndex+1];
        }
        //지금까지 currentPlayer를 다음 사람으로 바꿧고...(이 때 플레이어 배열의 순서가 플레이 순서로 생각함)


    }

    void applyExtraTurn(){//모, 윷, 상대말 잡기로 인한 추가 윷던지기

    }


}