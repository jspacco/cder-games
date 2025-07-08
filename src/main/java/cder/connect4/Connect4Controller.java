package cder.connect4;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cder.OpponentAI;

@RestController
@RequestMapping("/connect4")
public class Connect4Controller 
{
    private final Connect4Service service;

    public Connect4Controller(Connect4Service service) {
        this.service = service;
    }

    @GetMapping("/newgame")
    public ResponseEntity<C4GameState> newGame(@RequestParam String user) {
        Connect4 game = service.newGame(user);
        return ResponseEntity.ok(new C4GameState(game, -1));
    }

    @GetMapping("/move")
    public ResponseEntity<?> makeMove(@RequestParam String user, @RequestParam int col)
    {
        if (!service.hasGame(user)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Game not found for user: " + user));
        }

        Connect4 game = service.getGame(user);

        if (!game.isLegalMove(col)) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Illegal move: " + col));
        }

        // make the user move and get the AI counter move
        int aiMove = service.move(user, col);

        // return C4GameState
        return ResponseEntity.ok(new C4GameState(game, aiMove));
    }

    @GetMapping("/ai/{opponent}")
    public ResponseEntity<?> setAI(@RequestParam String user, @PathVariable String opponent)
    {
        try {
            OpponentAI opponentAI = OpponentAI.fromString(opponent);
            service.setOpponentAI(user, opponentAI);
            return ResponseEntity.ok(Map.of("status", "Opponent set to " + opponent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid opponent type, must be 'random' or 'smart'"));
        }
    }

    @GetMapping("/state")
    public ResponseEntity<?> state(@RequestParam String user)
    {
        if (!service.hasGame(user)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Game not found for user: " + user));
        }
        return ResponseEntity.ok(service.getGame(user));
    }
}
