package model;

import java.util.*;

import config.GameConfig;
import service.RuleEngine;

public class Game {
    List<Player> players = new ArrayList<>();
    BoardShape boardShape;
    Player currentPlayer = new Player();
    GameStatus gameStatus;
    RuleEngine checkRule = new RuleEngine();

    //Scanner sc = new Scanner(System.in);

    public enum GameStatus{
        READY, IN_PROGRESS, FINISHED
    }


    //모나 윷 나왓을대 한번더 하는 기능 넣기
    //extra윷

    //생성자. 플레이어 정보(ID,이름,말 수) 설정 및 boardshape 저장.
    public Game(int playerNum, String[] playerName, int piecesNum, BoardShape boardShape){

        gameStatus = GameStatus.READY; // 나중에 생각해볼래말래볼래말래 봐야되긴해

        //플레이어 정보 설정
        Player[] players = new Player[playerNum];
        for(int i=0; i<playerNum; i++){ //각 플레이어 생성
            players[i] = new Player(i, playerName[i], piecesNum);
        }

        //boardshape설정
        this.boardShape = boardShape;

        Start();   //실제 게임 동작
        int select = End();      //게임 끝나고 다시할지말지 선택

        if(select==1) {
            //main함수한테 select 값 전해줘서 다시 실행시키기;; 이거 어케 구현가능함?ㅎ game->gameconfig->main뭐 이렇게 전달해야할 것 같은디
        }
    }

    void Start(){ //게임 시작
        gameStatus = GameStatus.IN_PROGRESS
        //여기에서 첫번째 턴 할당.
        //이 안에서 다 진행하는 게 나을듯?

        //결과 나오면 반환하고 -> Game생성자 쪽에서 다시 실행또 할지 말지 하는게 나을듯
    }

    int End(){
        if(gameStatus != GameStatus.FINISHED) System.exit(1); //그냥 현 상태 finished맞는지 함 더 체크하는 거.
                                                                        //만약....finished도 아닌데 왓다면...걍 강종.
        while(true){
            System.out.println("\n재시작(1) or 종료(0) 선택하세요");
            Scanner sc = new Scanner(System.in);
            int select = sc.nextInt();

            if(select == 0) System.exit(0);
            else if(select==1) {
                return 1;
            }
            else {
                System.out.println("\n다시 입력하세요.");
            }
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

    Player checkVictory(){
        //승리조건 확인 및 승자 반환
        if(checkRule.applyVictoryCheck(currentPlayer)) { //이게 true면.
            currentGameStatus = GameStatus[3];
            return currentPlayer;

        }
        else return null;
    }

}