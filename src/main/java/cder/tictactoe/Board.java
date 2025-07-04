package cder.tictactoe;


public class Board {
    private final char[][] grid = new char[3][3];
    private char currentPlayer = 'X';
    private String winner = null;
    private boolean gameOver = false;

    public Board() {
        for (char[] row : grid) {
            for (int i = 0; i < row.length; i++) {
                row[i] = ' ';
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public boolean move(int r, int c)
    {
        if (gameOver) return false;
        if (!isLegalMove(r, c)) return false;
        // make the move
        setCell(r, c, currentPlayer);
        nextPlayer();
        // check for winner
        this.winner = getWinner();
        if (this.winner != null) gameOver = true;
        return true;
    }


    private char getCell(int row, int col) {
        return grid[row][col];
    }

    private void setCell(int row, int col, char value) {
        grid[row][col] = value;
    }

    public char[][] getGrid() {
        return grid;
    }

    public String getWinner() {
        for (int i = 0; i < 3; i++) {
            // Rows
            if (grid[i][0] != ' ' &&
                grid[i][0] == grid[i][1] &&
                grid[i][1] == grid[i][2]) return String.valueOf(grid[i][0]);

            // Columns
            if (grid[0][i] != ' ' &&
                grid[0][i] == grid[1][i] &&
                grid[1][i] == grid[2][i]) return String.valueOf(grid[0][i]);
        }

        // Diagonals
        if (grid[0][0] != ' ' &&
            grid[0][0] == grid[1][1] &&
            grid[1][1] == grid[2][2]) return String.valueOf(grid[0][0]);

        if (grid[0][2] != ' ' &&
            grid[0][2] == grid[1][1] &&
            grid[1][1] == grid[2][0]) return String.valueOf(grid[0][2]);

        // Check for tie
        for (char[] row : grid) {
            for (char c : row) {
                if (c == ' ') return null;
            }
        }

        return "Tie";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            for (char cell : row) {
                sb.append(cell).append(" | ");
            }
            sb.setLength(sb.length() - 3); // Remove last " | "
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isLegalMove(int r, int c) {
        return r >= 0 && r <= 2 && c >= 0 && c <= 2 && getCell(r, c) == ' ';
    }
}

