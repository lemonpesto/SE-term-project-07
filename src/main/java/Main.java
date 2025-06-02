import controller.SetupController;

import javax.swing.*;

// SetupController만 생성해서 사용자로부터 "플레이어, 말 개수, 보드 모양" 정보를 입력받는 창을 띄움.
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // SetupController가 내부에서 SetupFrame을 생성하고
            // 이벤트 리스너를 등록한 뒤 화면을 보여 줍니다.
            new SetupController();
        });
    }
}

