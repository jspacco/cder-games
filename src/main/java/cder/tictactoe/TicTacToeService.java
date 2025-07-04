package cder.tictactoe;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TicTacToeService
{
    // X always goes first
    private Map<String, Board> boards = new HashMap<>();
    private Map<String, Character> currentPlayer = new HashMap<>();
    private Map<String, String> opponents = new HashMap<>();
    
    public Board newGame(String user)
    {
        Board game = new Board();
        boards.put(user, game);
        opponents.putIfAbsent(user, "random");
        currentPlayer.put(user, game.getCurrentPlayer());
        return game;
    }

    public boolean setOpponent(String user, String opponent)
    {
        if (!opponent.equals("random") && !opponent.equals("smart")) {
            return false;
        }
        opponents.put(user, opponent);
        return true;
    }

    public boolean isLegalMove(String user, int row, int col)
    {
        if (!boards.containsKey(user)) return false;
        Board board = boards.get(user);
        return board.isLegalMove(row, col);
    }

    public Board move(String user, int r, int c)
    {
        if (!boards.containsKey(user))
        {
            throw new IllegalStateException("No current game for "+user);
        }

        Board game = boards.get(user);
        if (!game.isLegalMove(r, c))
        {
            throw new IllegalArgumentException("Illegal move: "+r +", "+c);
        }
        
        game.move(r, c);

        if (!game.isGameOver()) {
            if (opponents.get(user).equals("random")) {
                randomAIMove(game);
            } else if (opponents.get(user).equals("goodAI")) {
                int[] aiMove = goodAIMove(game);
                game.move(aiMove[0], aiMove[1]);
            } else {
                throw new IllegalArgumentException("Unknown opponent type: " + opponents.get(user));
            }
        }

        return game;
    }

    private int[] goodAIMove(Board game)
    {
        char ai = game.getCurrentPlayer();
        char player = (ai == 'X') ? 'O' : 'X';

        // 1. Try to win
        int[] win = findWinningMove(game, ai);
        if (win != null) return win;

        // 2. Try to block opponent
        int[] block = findWinningMove(game, player);
        if (block != null) return block;

        // 3. Take center
        if (game.isLegalMove(1, 1)) return new int[] {1, 1};

        // 4. Take a corner
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] move : corners) {
            if (game.isLegalMove(move[0], move[1])) return move;
        }

        // 5. Take any side
        int[][] sides = {{0,1}, {1,0}, {1,2}, {2,1}};
        for (int[] move : sides) {
            if (game.isLegalMove(move[0], move[1])) return move;
        }
        // no legal moves
        return null;
    }

    private int[] findWinningMove(Board game, char player) {
        // AI moves
        // choose a winning move if available
        char[][] grid = game.getGrid();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (game.isLegalMove(r, c)) {
                    grid[r][c] = player;
                    boolean wins = game.getWinner() != null && game.getWinner().equals(String.valueOf(player));
                    grid[r][c] = ' '; // undo
                    if (wins) return new int[] {r, c};
                }
            }
        }
        return null;
    }

    private void randomAIMove(Board game)
    {
        if (!game.isGameOver()) {
            // AI should now move
            // For simplicity, let's just make a random legal move
            while (true) {
                int i = (int) (Math.random() * 3);
                int j = (int) (Math.random() * 3);
                if (game.isLegalMove(i, j)) {
                    game.move(i, j);
                    break;
                }
            }
            // label: for (int i = 0; i < 3; i++) {
            //     for (int j = 0; j < 3; j++) {
            //         if (game.isLegalMove(i, j)) {
            //             game.move(i, j);
            //             break label;
            //         }
            //     }
            // }
        }
    }

    public boolean hasGame(String user) {
        return boards.containsKey(user);
    }

    public Board getGame(String user) {
        return boards.get(user);
    }
    
}
