package config;

public class GameConfig{
    int numPlayers;
    int piecesPerPlayer; //플레이어의 말 개수
    BoardShape[] boardShape = new BoardShape[]{SQUARE, PENTAGON, HEXAGON};

    Boolean validate(){
        //설정값 검증
    }
}