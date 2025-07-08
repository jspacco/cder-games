package cder.connect4;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import cder.OpponentAI;

@Service
public class Connect4Service 
{
    // Red always goes first
    private Map<String, Connect4> boards = new HashMap<>();
    private Map<String, Checker> currentPlayer = new HashMap<>();
    // opponent AI can be "random" or "smart"
    private Map<String, OpponentAI> opponentAI = new HashMap<>();
    

    public Connect4 newGame(String user)
    {
        Connect4 game = new Connect4();
        boards.put(user, game);
        opponentAI.putIfAbsent(user, OpponentAI.RANDOM);
        currentPlayer.put(user, game.getCurrentPlayer());
        return game;
    }

    public void setOpponentAI(String user, OpponentAI opponent)
    {
        opponentAI.put(user, opponent);
    }

    public boolean hasGame(String user)
    {
        return boards.containsKey(user);
    }
    public Connect4 getGame(String user)
    {
        if (!boards.containsKey(user)) {
            throw new IllegalStateException("No current game for "+user);
        }
        return boards.get(user);
    }

    public boolean isLegalMove(String user, int col)
    {
        if (!boards.containsKey(user)) return false;
        Connect4 board = boards.get(user);
        return board.isLegalMove(col);
    }

    public int move(String user, int c)
    {
        if (!boards.containsKey(user))
        {
            throw new IllegalStateException("No current game for "+user);
        }

        Connect4 game = boards.get(user);
        if (!game.isLegalMove(c))
        {
            throw new IllegalArgumentException("Illegal move: "+c);
        }
        
        // OK, your move is now legal
        game.move(c);

        // Now AI makes a counter move
        if (!game.isGameOver()) {
            if (opponentAI.get(user) == OpponentAI.RANDOM) {
                return game.randomAIMove();
            } else if (opponentAI.get(user) == OpponentAI.SMART) {
                return game.goodAIMove();
            } else {
                throw new IllegalArgumentException("Unknown opponent type: " + opponentAI.get(user));
            }
        }

        // -1 indicates no move was made by the AI, because the game is over
        return -1;
    }

}
