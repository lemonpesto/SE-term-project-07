import config.GameConfig;

public class Main {
    public static void main(String[] args) {

        //config setting
        while(true){
            GameConfig gameConfig = new GameConfig();
            if(!gameConfig.restart) break;
        }
        System.out.println("겜 종료.");
    }
}