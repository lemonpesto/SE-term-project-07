package service;

import model.BoardShape;
import model.Cell;
import model.Game;
import model.Piece;
import model.Player;
import model.ThrowResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MoveActionServiceTest
 *
 * MoveActionService의 동작을 검증하는 단위 테스트.
 * - 이동 거리 (ThrowResult에 따른 칸 수 이동) 검증
 * - 업기 (쌓기) 동작 검증
 * - 잡기 (상대 말 되돌리기) 동작 검증
 */
public class MoveActionServiceTest {

    private Game game;
    private MoveActionService moveService;
    private Player playerA;
    private Player playerB;
    private Piece pieceA;
    private Piece pieceA2; // 두 번째 말 (stacking tests)
    private Piece pieceB;

    @BeforeEach
    void setUp() {
        // 2명, 각 2개의 말, 정사각형 보드 사용
        String[] names = {"Alice", "Bob"};
        game = new Game(2, names, 2, BoardShape.SQUARE);
        game.startGame(); // 게임 상태를 IN_PROGRESS로 설정

        moveService = new MoveActionService(game.getRuleEngine());

        playerA = game.getPlayers().get(0);
        playerB = game.getPlayers().get(1);

        // 각 플레이어의 첫 번째 말
        pieceA = playerA.getPieces().get(0);
        pieceA2 = playerA.getPieces().get(1);
        pieceB = playerB.getPieces().get(0);
    }

    /**
     * ThrowResult에 따른 이동 칸 수가 정확한지 테스트
     */
    @Test
    void testMovementDistance() {
        // 시작 위치
        Cell start = game.getBoard().getStartCell();
        assertEquals(start, pieceA.getCurrentCell());

        // DO: 한 칸 이동
        moveService.movePiece(pieceA, ThrowResult.DO, game);

        Cell cell1 = start.getNextCells().size() == 1 ? start.getNextCells().get(0) : start.getNextCells().get(1);
        assertEquals(cell1.getId(), pieceA.getCurrentCell().getId(), "DO는 한 칸 이동해야 한다.");

        // 다시 DO: 두 칸 이동
        moveService.movePiece(pieceA, ThrowResult.DO, game);
        Cell cell2 = cell1.getNextCells().size() == 1 ? cell1.getNextCells().get(0) : cell1.getNextCells().get(1);
        assertEquals(cell2.getId(), pieceA.getCurrentCell().getId(), "두 번째 DO 시 총 두 칸 이동해야 한다.");

        // GAE: 두 칸 이동 (현재 cell2에서 두 칸 이동 → cell4)
        moveService.movePiece(pieceA, ThrowResult.GAE, game);
        Cell cell3 = cell2.getNextCells().size() == 1 ? cell2.getNextCells().get(0) : cell2.getNextCells().get(1);
        Cell cell4 = cell3.getNextCells().get(0);
        assertEquals(cell4.getId(), pieceA.getCurrentCell().getId(), "GAE는 두 칸 이동해야 한다.");

        // GEOL: 세 칸 이동
        moveService.movePiece(pieceA, ThrowResult.GEOL, game);
        Cell c5 = cell4.getNextCells().size() == 1 ? cell4.getNextCells().get(0) : cell4.getNextCells().get(1);
        Cell c6 = c5.getNextCells().get(0);
        Cell c7 = c6.getNextCells().get(0);
        assertEquals(c7.getId(), pieceA.getCurrentCell().getId(), "GEOL은 세 칸 이동해야 한다.");

        // YUT: 네 칸 이동
        moveService.movePiece(pieceA, ThrowResult.YUT, game);
        Cell c8 = c7.getNextCells().size() == 1 ? c7.getNextCells().get(0) : c7.getNextCells().get(1);
        Cell c9 = c8.getNextCells().get(0);
        Cell c10 = c9.getNextCells().get(0);
        Cell c11 = c10.getNextCells().get(0);
        assertEquals(c11.getId(), pieceA.getCurrentCell().getId(), "YUT은 네 칸 이동해야 한다.");

        // MOK: 다섯 칸 이동
        moveService.movePiece(pieceA, ThrowResult.MO, game);
        Cell c12 = c11.getNextCells().size() == 1 ? c11.getNextCells().get(0) : c11.getNextCells().get(1);
        Cell c13 = c12.getNextCells().get(0);
        Cell c14 = c13.getNextCells().get(0);
        Cell c15 = c14.getNextCells().get(0);
        Cell c16 = c15.getNextCells().get(0);
        assertEquals(c16.getId(), pieceA.getCurrentCell().getId(), "MO는 다섯 칸 이동해야 한다.");
    }

    /**
     * 뒤로 이동(Back throw 'BAEK') 기능 테스트: 한 칸 뒤로 이동해야 한다.
     */
    @Test
    void testBackwardMove_Baek() {
        Cell start = game.getBoard().getStartCell();
        // 먼저 두 칸 앞으로 이동: DO -> DO
        moveService.movePiece(pieceA, ThrowResult.DO, game);
        moveService.movePiece(pieceA, ThrowResult.DO, game);
        Cell cell2 = start.getNextCells().get(0).getNextCells().get(0);
        assertEquals(cell2, pieceA.getCurrentCell());

        // BAEK으로 한 칸 뒤로 이동 → cell1
        moveService.movePiece(pieceA, ThrowResult.BACK_DO, game);
        Cell cell1 = start.getNextCells().get(0);
        assertEquals(cell1, pieceA.getCurrentCell(), "BACK_DO는 한 칸 뒤로 이동해야 한다.");
    }

    /**
     * 두 개의 동일 플레이어 말이 같은 칸에 모였을 때, 함께 이동하는 업기(stacking) 기능 테스트
     */
    @Test
    void testStacking() {
        Cell start = game.getBoard().getStartCell();
        // pieceA와 pieceA2를 모두 한 칸 앞으로 옮겨서 같은 위치로 모은다.
        moveService.movePiece(pieceA, ThrowResult.DO, game);
        moveService.movePiece(pieceA2, ThrowResult.DO, game);

        Cell cell1 = start.getNextCells().get(0);
        assertEquals(cell1, pieceA.getCurrentCell());
        assertEquals(cell1, pieceA2.getCurrentCell(), "두 말이 같은 칸에 쌓여야 한다.");

        // stacking된 두 말을 DO 한 번에 두 칸 앞으로 이동해야 한다.
        moveService.movePiece(pieceA.getGroup(), ThrowResult.DO, game);
        Cell cell2 = cell1.getNextCells().get(0);
        assertEquals(cell2.getId(), pieceA.getCurrentCell().getId());
        assertEquals(cell2.getId(), pieceA2.getCurrentCell().getId(), "스택된 말이 함께 이동해야 한다.");
    }

    /**
     * 다른 플레이어 말 위에 착지할 경우, 잡기(capture)되어 상대 말은 시작 위치로 되돌려야 한다.
     */
    @Test
    void testCapture() {
        Cell start = game.getBoard().getStartCell();
        // playerA의 말을 한 칸 이동 → cell1
        moveService.movePiece(pieceA, ThrowResult.DO, game);
        Cell cell1 = start.getNextCells().get(0);
        assertEquals(cell1, pieceA.getCurrentCell());

        // playerB의 말을 한 칸 이동 → 같은 cell1으로 이동
        moveService.movePiece(pieceB, ThrowResult.DO, game);
        assertEquals(cell1, pieceB.getCurrentCell());

        // 이동 결과로 playerA의 말은 잡혀서 시작 위치로 가야 함
        assertEquals(start, pieceA.getCurrentCell(), "상대 말을 잡으면 시작 위치로 돌아가야 한다.");
    }
}