import controller.SetupControllerFX;
import controller.SetupControllerSwing;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        String ui = "swing";
        if(ui.equals("swing")){
            SwingUtilities.invokeLater(() -> {
                // SetupController가 내부에서 SetupFrame을 생성하고
                // 이벤트 리스너를 등록한 뒤 화면을 보여 줍니다.
                new SetupControllerSwing();
            });
        } else if (ui.equals("javafx")) {
            SetupControllerFX.main(args);
        }
        else {
            System.out.println("지원하지 않는 UI입니다.");
        }
    }
}