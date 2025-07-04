package cder.tictactoe;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tictactoe")
public class TicTacToeController {

    private final TicTacToeService service;

    public TicTacToeController(TicTacToeService service) {
        this.service = service;
    }

    @GetMapping("/move")
    public ResponseEntity<?> makeMove(@RequestParam String user, @RequestParam int row, @RequestParam int col) 
    {
        if (!service.hasGame(user)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Game not started. Call /newgame first."));
        }

        if (!service.isLegalMove(user, row, col)){
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Invalid move: cell occupied or out of bounds."));
        }

        try {
            Board nextState = service.move(user, row, col);
            return ResponseEntity.ok(nextState);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Something went wrong"));
        }
    }

    @GetMapping("/newgame")
    public ResponseEntity<?> reset(@RequestParam String user) {
        Board board = service.newGame(user);
        return ResponseEntity.ok(board);
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
