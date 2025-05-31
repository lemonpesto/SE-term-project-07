package controller;

import model.Game;
import model.Player;
import model.Piece;
import model.ThrowResult;
import service.YutThrowService;
import view.IGameView;
import view.IGameViewListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameController의 “연속 던지기 --> 모아둔 결과만큼 말 선택-->이동” 흐름을 검증하는 JUnit 5 테스트
 */
class GameControllerTest {

    private Game game;
    private DummyView view;
    private GameController controller;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Game 인스턴스 생성 (2명, 각자 말 1개, 사각형 보드)
        String[] names = {"Alice", "Bob"};
        game = new Game(2, names, 1, model.BoardShape.SQUARE);

        // 2) Game 내부의 YutThrowService를 StubYutThrowService로 교체
        StubYutThrowService stubService = new StubYutThrowService(
                Arrays.asList(ThrowResult.YUT, ThrowResult.DO)
        );
        Field field = Game.class.getDeclaredField("yutThrowService");
        field.setAccessible(true);
        field.set(game, stubService);

        // 3) DummyView 생성 --> GameController에 주입
        view = new DummyView();
        controller = new GameController(game, view);
    }

    @Test
    void testExtraTurnSequenceAndPieceMovement() {
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);

        // 초기 상태 확인
        // - 첫 호출 시 "Alice님, 윷을 던져주세요." 메시지가 view에 기록되어야 함
        assertEquals(1, view.statusLog.size());
        assertEquals("게임 준비 완료. Alice님, 윷을 던져주세요.", view.statusLog.get(0));
        // - 말 선택 비활성
        assertFalse(view.lastPieceSelectable);

        // --- 1) Alice: 첫 번째 던지기 (StubYutThrowService --> YUT, extra=true) ---
        controller.onThrowButtonClicked();

        // throwResults 에 YUT 추가, extraTurn==true 이므로
        // - “추가 던지기” 메시지가 남음
        assertEquals("Alice님, 윷 결과: YUT 입니다.", view.statusLog.get(1));
        assertEquals("Alice님, 한 번 더 윷을 던져주세요!", view.statusLog.get(2));
        // - 말 선택은 비활성
        assertFalse(view.lastPieceSelectable);

        // --- 2) Alice: 두 번째 던지기 (StubYutThrowService --> DO, extra=false) ---
        controller.onThrowButtonClicked();

        // throwResults 에 DO 추가, extraTurn==false 이므로 “이동할 말 선택” 단계로 진입
        // 순서:
        //   view.updateStatus("Alice님, 윷 결과: DO 입니다.");
        //   view.updateStatus("Alice님, 첫 번째 결과: DO --> 이동할 말을 클릭하세요.");
        int sizeAfter = view.statusLog.size();
        assertEquals("Alice님, 윷 결과: DO 입니다.", view.statusLog.get(sizeAfter - 2));
        assertEquals("Alice님, 첫 번째 결과: DO --> 이동할 말을 클릭하세요.", view.statusLog.get(sizeAfter - 1));
        // - 말 선택만 활성화
        assertTrue(view.lastPieceSelectable);

        // --- 3) Alice: 첫 번째 이동 처리 ---
        //    (throwResults = [YUT, DO], 현재 currentThrowIndex = 0)
        Piece alicePiece = alice.getPieces().get(0);
        controller.onPieceClicked(alicePiece);

        // 3-1) playOneTurn(Alice, ThrowResult.YUT, alicePiece) 적용
        //     --> 보드 최종 상태를 직접 검증하기 어려우므로, 최소한 repaintBoard가 호출되었는지 확인
        assertTrue(view.repaintCalled);
        //     --> “다음 결과: DO --> 이동할 말을 클릭하세요.” 메시지가 남아 있어야 함
        assertEquals("Alice님, 다음 결과: DO --> 이동할 말을 클릭하세요.",
                view.statusLog.get(view.statusLog.size() - 1));
        //     --> 말 선택은 여전히 활성, 던지기는 비활성
        assertTrue(view.lastPieceSelectable);

        // --- 4) Alice: 두 번째 이동 처리 ---
        //    (currentThrowIndex = 1)
        view.resetRepaintFlag(); // repaintBoard 호출 횟수 초기화
        controller.onPieceClicked(alicePiece);

        // 4-1) playOneTurn(Alice, ThrowResult.DO, alicePiece) 적용
        assertTrue(view.repaintCalled);
        // 4-2) 모든 ThrowResult 처리 완료 --> 다음 플레이어 Bob 차례
        //     --> view.updateStatus("Bob님, 윷을 던져주세요.") 메시지가 기록되어야 함
        String lastStatus = view.statusLog.get(view.statusLog.size() - 1);
        assertEquals("Bob님, 윷을 던져주세요.", lastStatus);
        // 4-3) 말 선택 비활성
        assertFalse(view.lastPieceSelectable);
    }

    /**
     * StubYutThrowService: 미리 지정된 ThrowResult 목록을 순차적으로 반환
     */
    static class StubYutThrowService extends YutThrowService {
        private final List<ThrowResult> sequence;
        private int idx = 0;

        StubYutThrowService(List<ThrowResult> sequence) {
            this.sequence = new ArrayList<>(sequence);
        }

        // @Override 제거! (부모 메서드가 static이므로 @Override를 붙이면 안됩니다)
        public ThrowResult throwRandom() {
            if (idx < sequence.size()) {
                return sequence.get(idx++);
            }
            // 시퀀스가 다 소진되면 기본값 DO 반환
            return ThrowResult.DO;
        }
    }


    /**
     * DummyView: IGameView 인터페이스를 구현하여
     * 호출된 메서드들(status, enable/disable, repaint, showWinnerDialog)을 기록만 한다.
     */
    static class DummyView implements IGameView {
        List<String> statusLog = new ArrayList<>();
        boolean lastPieceSelectable;
        boolean repaintCalled;
        String winnerNameShown;

        @Override
        public void showWindow() {
            // 실제 창을 띄우지 않음
        }

        @Override
        public void repaintBoard() {
            repaintCalled = true;
        }

        @Override
        public void updateStatus(String message) {
            statusLog.add(message);
        }

        @Override
        public void showWinnerDialog(String winnerName) {
            winnerNameShown = winnerName;
        }

        @Override
        public void setGameViewListener(IGameViewListener listener) {
            // 이 메서드는 호출되지 않음 (GameController에서만 set함)
        }

        @Override
        public void setPieceSelectable(boolean enabled) {
            lastPieceSelectable = enabled;
        }

        void resetRepaintFlag() {
            repaintCalled = false;
        }
    }
}
