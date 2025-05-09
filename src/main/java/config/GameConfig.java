package config;

import java.util.Scanner;
import model.Player;

public enum BoardShape{
    SQUARE,
    PENTAGON,
    HEXAGON
}

public class GameConfig{
    int playerNum;
    int piecesNum; //플레이어의 말 개수
    BoardShape currentBoardShape = new BoardShape;
    //BoardShape[] boardShape = new BoardShape[]{SQUARE, PENTAGON, HEXAGON};

    Boolean validate(){
        //설정값 검증
        //머 어케 검증;;;?????
    }

    void Setting(){
        Scanner sc = new Scanner(System.in);

        //인원, 말 수, 보드모양 입력
        System.out.println("몇 명할건지 선택(2~4명)");
        playerNum = sc.nextInt(); //이거 자바 스윙에서 버튼 선택하는 머 그렇게??해야되남?

        System.out.println("인당 말 몇개 가질것? (2~5개)");
        piecesNum = sc.nextInt();

        System.out.println("보드판 선택 : 사각형, 오각형, 육각형");
        String temp = sc.next();
        /// /어케적어야하지

        //플레이어 정보 설정
        Player[] players = new Player[playerNum];
        for(int i=0; i<playerNum; i++){ //각 플레이어 생성
            System.out.println((i+1)+"번째 player 이름 : ");    //사용자에게는 1번째, 2번째..지만 우리에겐 0번째, 1번째..
            String name = sc.next();
            players[i] = new Player(i, name, piecesNum);
        }

        //보드판 설정

    }
}