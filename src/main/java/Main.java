//import controller.SetupController;

import controller.SetupController;
import controller.SetupControllerFX;
//import view.javafx.JavaFXGameView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        String ui = "swing";
        if(ui.equals("swing")){
            SwingUtilities.invokeLater(() -> { new SetupController(); });
        } else if (ui.equals("javafx")) {
            SetupControllerFX.main(new String[0]);
        }
        else {
            System.out.println("지원하지 않는 UI입니다.");
        }
    }
}