package cder.connect4;

import java.util.ArrayList;
import java.util.List;

public class Connect4 
{
    private Checker[][] board;
    // Red always goes first
    private Checker currentPlayer = Checker.RED;
    private List<Move> moveList = new ArrayList<>();

    public Connect4() 
    {
        board = new Checker[6][7];
        currentPlayer = Checker.RED;
    }
    public Checker[][] getBoard() 
    {
        return board;
    }
    public Checker getCurrentPlayer() 
    {
        return currentPlayer;
    }
    public void nextPlayer() 
    {
        currentPlayer = (currentPlayer == Checker.RED) ? Checker.YELLOW : Checker.RED;
    }
    public boolean isLegalMove(int col) 
    {
        if (col < 0 || col >= 7) return false;
        return board[0][col] == null; // Check if the top row of the column is empty
    }
    public boolean move(int col) 
    {
        if (!isLegalMove(col)) return false;
        for (int row = 5; row >= 0; row--) {
            if (board[row][col] == null) {
                board[row][col] = currentPlayer;
                addMove(new Move(col, currentPlayer));
                nextPlayer();
                return true;
            }
        }
        return false;
    }
    public boolean isGameOver() 
    {
        // Check for horizontal, vertical, and diagonal wins
        return checkWin() || isBoardFull();
    }
    
    private boolean checkWin()
    {
        // Check horizontal, vertical, and diagonal for a win
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {     
                if (board[row][col] != null) {
                    if (checkDirection(row, col, 1, 0) || // Horizontal
                        checkDirection(row, col, 0, 1) || // Vertical
                        checkDirection(row, col, 1, 1) || // Diagonal /
                        checkDirection(row, col, 1, -1)) { // Diagonal \
                        return true;
                    }
                }
            }
        }
        return false;
    
    }
    private boolean checkDirection(int row, int col, int deltaRow, int deltaCol)
    {
        Checker checker = board[row][col];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            int newRow = row + i * deltaRow;
            int newCol = col + i * deltaCol;
            if (newRow < 0 || newRow >= 6 || newCol < 0 || newCol >= 7 || board[newRow][newCol] != checker) {
                return false;
            }
            count++;
        }
        return count == 4;
    }
    private boolean isBoardFull()
    {
        for (int col = 0; col < 7; col++) {
            if (board[0][col] == null) {
                return false; // If any cell in the top row is empty, the board is not full
            }
        }
        return true; // All cells in the top row are filled, so the board is full
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                sb.append(board[row][col] == null ? "." : board[row][col].toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public boolean isTie() {
        // A tie occurs when the board is full and no player has won
        return isBoardFull() && !checkWin();
    }
    // make a good AI move and also return the column chosen
    public int goodAIMove() {
        //TODO: AI logic for making a good move
        return randomAIMove();
    }
    // make a random AI move and also return the column chosen
    public int randomAIMove() {
        if (isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
        if (currentPlayer == Checker.RED) {
            throw new IllegalStateException("It's not the AI's turn");
        }
        if (isBoardFull()) {
            throw new IllegalStateException("Board is full, no legal moves available");
        }
        // Randomly select a column to drop a checker
        while (true) {
            int col = (int) (Math.random() * 7);
            if (isLegalMove(col)) {
                move(col);
                return col;
            }
        }
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    private void addMove(Move move) {
        moveList.add(move);
    }

    public Checker getWinner() {
        if (!isGameOver()) {
            // No winner if game is not over
            return null; 
        }
        // Check for a winner by checking the last move
        if (moveList.isEmpty()) {
            // No moves made yet
            return null; 
        }
        Move lastMove = moveList.get(moveList.size() - 1);
        Checker lastPlayer = lastMove.player();
        // Return the player who made the last move if they won
        return checkWin() ? lastPlayer : null; 
    }
}