package model;

import java.util.*;
import config.GameConfig;
import service.YutThrowService;
import service.MoveActionService;
import service.RuleEngine;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final List<Player> finishedPlayers = new ArrayList<>();
    private GameStatus gameStatus;
    private final BoardShape boardShape;

    // 보드 및 서비스
    private final Board board;
    private final YutThrowService yutService;
    private final MoveActionService moveActionService;
    private final RuleEngine ruleEngine;

    private enum GameStatus {
        READY,
        IN_PROGRESS,
        FINISHED
    }

    /**
     * @param config 게임 설정 및 초기값
     * @param playerNames 플레이어 이름 배열
     */
    public Game(GameConfig config, String[] playerNames) {
        // 플레이어 생성
        for (int i = 0; i < config.getNumPlayers(); i++) {
            players.add(new Player(i, playerNames[i], config.getPiecesPerPlayer()));
        }
        this.boardShape = config.getBoardShape();
        this.gameStatus = GameStatus.READY;

        // 보드 및 서비스 초기화
        this.board = new Board(boardShape);
        this.ruleEngine = new RuleEngine();
        this.yutService = new YutThrowService();
        this.moveActionService = new moveActionService(ruleEngine);
    }

    /**
     * 모든 플레이어가 말을 내보낼 때까지 게임을 진행합니다.
     */
    public void startGame() {
        gameStatus = GameStatus.IN_PROGRESS;

        while (gameStatus == GameStatus.IN_PROGRESS) {
            for (Player current : players) {
                if (current.getIsFinished()) continue;
                playTurn(current);
                if (current.getIsFinished()) {
                    finishedPlayers.add(current);
                }
            }
            checkGameOver();
        }
    }

    /**
     * 한 플레이어의 한 턴을 처리합니다.
     */
    public void playTurn(Player player) {
        // 1) 윷 던지기를 모아두기 (Extra Turn 포함)
        List<ThrowResult> results = new ArrayList<>();
        ThrowResult result;
        do {
            result = yutService.throwRandom();
            results.add(result);
        } while (result.isExtraTurn());

        // 2) 각 결과별로 이동할 말을 사용자에게 선택받고 이동
        for (ThrowResult r : results) {
            Piece piece = player.selectPiece(r, board);
            moveActionService.movePiece(piece, r, this);
        }
    }

    /**
     * 모든 플레이어가 끝났는지 검사하여 게임 상태를 FINISHED로 변경합니다.
     */
    private void checkGameOver() {
        for (Player p : players) {
            if (!p.getIsFinished()) return;
        }
        gameStatus = GameStatus.FINISHED;
    }

    /**
     * 게임 종료 순서(플레이어 순위) 반환
     */
    public List<Player> getFinishedPlayers() {
        return Collections.unmodifiableList(finishedPlayers);
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}
