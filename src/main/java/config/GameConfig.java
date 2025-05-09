package config;

import java.util.Scanner;
import model.BoardShape;
import model.Game;

public class GameConfig{
    int playerNum;
    String[] playerName;
    int piecesNum; //플레이어의 말 개수
    BoardShape boardShape = null;

    public boolean restart;

    Game game;

    public GameConfig(){
        Setting();
        //입력값 검증 -> 잘못되었으면 다시 config setting
        while(!validate()){
            System.out.println("입력값이 잘못되었습니다. 다시 입력해주세요.");
            Setting();
        }

        //게임 시작 버튼
        System.out.println("게임 하시겠습니까?");   //스윙에서 '게임시작'버튼으로 대체.
        Scanner sc = new Scanner(System.in);    //스윙에서 '게임시작'버튼을 누름.
        if(sc.nextInt()==1) makeGame();         //버튼을 눌렀으면 makeGame을 실행.

        //게임 재시작 : game객체 삭제 및 재생성

    }



    public void Setting(){
        Scanner sc = new Scanner(System.in);

        //인원, 말 수, 보드모양 입력
        System.out.println("몇 명할건지 선택(2~4명)");
        playerNum = sc.nextInt(); //이거 자바 스윙에서 버튼 선택하는 머 그렇게??해야되남?

        playerName = new String[playerNum];
        for(int i=0;i<playerNum;i++){
            System.out.println((i+1)+"번째 player의 이름을 입력 : ");
            playerName[i] = sc.next();
        }

        System.out.println("인당 말 몇개 가질것? (2~5개)");
        piecesNum = sc.nextInt();

        System.out.println("보드판 선택 : 사각형(1), 오각형(2), 육각형(3)");
        int temp = sc.nextInt();
        switch (temp){
            case 1 : boardShape = BoardShape.SQUARE; break;
            case 2 : boardShape = BoardShape.PENTAGON; break;
            case 3 : boardShape = BoardShape.HEXAGON; break;
            default : break;
        }

        //보드판 설정

    }

    public Boolean validate(){
        //설정값 검증. 플레이어 수, 말 수, 보드모양 모두 값이 올바르면 true.
        if(2<=playerNum<=4 && 2<=piecesNum<=5 && boardShape!=null) return true;
        else return false;
    }

    public void makeGame(){
        game = new Game(playerNum, playerName, piecesNum,boardShape);
    }

    public void restartGame(){
        restart = true;
    }

    public void exitGame(){
        restart = false;
    }
}