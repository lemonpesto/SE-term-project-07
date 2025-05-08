package model;

import java.util.Scanner;
import service.RuleEngine;

public class Game {
    Player[] players;
    Board board = new Board();
    Player currentPlayer = new Player();
    String[] GameStatus = new String[]{"READY", "IN_PROGRESS", "FINISHED"};
    String currentGameStatus = GameStatus[0];
    RuleEngine checkRule = new RuleEngine();

    Scanner sc = new Scanner(System.in);



    //모나 윷 나왓을대 한번더 하는 기능 넣기
    //extra윷
    void start(){//게임 초기화

        currentGameStatus = GameStatus[0];

        //config 부르기
        //게임 시작 버튼 누르기

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

    void extraStep(){//모, 윷, 상대말 잡기로 인한 추가 윷던지기

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