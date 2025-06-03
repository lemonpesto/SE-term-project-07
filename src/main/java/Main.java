import controller.SetupControllerFX;

public class Main {
    public static void main(String[] args) {

        String ui = "javafx";
        if(ui.equals("swing")){

        } else if (ui.equals("javafx")) {
            SetupControllerFX.main(args);
        }
        else {
            System.out.println("지원하지 않는 UI입니다.");
        }
    }
}