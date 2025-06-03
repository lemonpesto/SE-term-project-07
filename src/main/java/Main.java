import controller.SetupController;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        String ui = "javafx";
        if(ui.equals("swing")){
            SwingUtilities.invokeLater(() -> { new SetupController(); });
        } else if (ui.equals("javafx")) {
            javafx.application.Application.launch(controller.SetupControllerFX.class, args);
        }
        else {
            System.out.println("지원하지 않는 UI입니다.");
        }
    }
}