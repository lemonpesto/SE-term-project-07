import config.GameConfig;

public class Main {
    public static void main(String[] args) {

        /*이걸 main에서 해줘야함.*/
        //config setting
        boolean status = true;
        while(status){
            GameConfig gameConfig = new GameConfig();
            if(!gameConfig.restart) status = false;
        }
        System.out.println("겜 종료.");
    }
}