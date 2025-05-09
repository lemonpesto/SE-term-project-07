package config;

import model.BoardShape;

public class GameConfig{
    private final int numPlayers;
    private final int numPieces; //플레이어의 말 개수
    private final BoardShape boardShape;

    /**
     * @param numPlayers 참가자 수 (2~4)
     * @param piecesPerPlayer 말 개수 (2~5)
     * @param boardShape 보드 형태
     */
    public GameConfig(int numPlayers, int piecesPerPlayer, BoardShape boardShape) {
        this.numPlayers = numPlayers;
        this.numPieces = piecesPerPlayer;
        this.boardShape = boardShape;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getPiecesPerPlayer() {
        return numPieces;
    }

    public BoardShape getBoardShape() {
        return boardShape;
    }

    @Override
    public String toString() {
        return String.format("GameConfig[players=%d, pieces=%d, shape=%s]",
                numPlayers, numPieces, boardShape);
    }

}