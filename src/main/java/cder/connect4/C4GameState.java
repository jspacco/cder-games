package cder.connect4;

import java.util.List;

public class C4GameState 
{
    // current player
    // most recent move
    // move list
    // is game over?
    // winner?
    // we aren't sending the board state because we want students to do some work

    private List<Move> moves;
    private Checker currentPlayer;
    private int lastMoveColumn;
    private boolean gameOver;
    private Checker winner;
    
    public C4GameState(Connect4 game, int col)
    {
        this.moves = game.getMoveList();
        this.currentPlayer = game.getCurrentPlayer();
        this.lastMoveColumn = col;
        this.gameOver = game.isGameOver();
        this.winner = game.getWinner();
    }

    public List<Move> getMoves() {
        return moves;
    }
    public Checker getCurrentPlayer() {
        return currentPlayer;
    }
    public int getLastMoveColumn() {
        return lastMoveColumn;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public Checker getWinner() {
        return winner;
    }
    
}
