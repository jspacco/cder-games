# RESTful Games server

All GET requests, very simple, designed for intro students to build a GUI with Greenfoot, if given sufficient scaffolding

## TicTacToe
### API
Only 4 commands: `newgame`, `move`, `state`, `ai`

Normally, `newgame`, `move`, and `ai` would be POST requests instead of GET requests, but everything is simplified to make it easier for intro students to test out the server.

#### newgame: starts a new game for `username`
`/tictactoe/newgame?user=username`

JSON response looks like this:

```json
{
  "grid": [
    "   ",
    "   ",
    "   "
  ],
  "currentPlayer": "X",
  "winner": null,
  "gameOver": false
}
```

#### move (GET request instead of POST)
`/tictactoe/move?user=username&row=0&col=0`

Make a move by putting an X at row 0, col 0 (top left)

If the move is illegal (out of bounds or something is already there) returns HTTP status code 400 (BAD REQUEST) with an error message.

The server responds with JSON that includes the move the player just made, plus a move by the AI opponent.

The JSON response might be:

```json
{
  "grid": [
    "X  ",
    " O ",
    "   "
  ],
  "currentPlayer": "X",
  "winner": null,
  "gameOver": false
}
``` 

#### state
`/tictactoe/state?user=username`

Give the current game state for `username`. Responds with JSON, for example:
```json
{
  "grid": [
    "XOX",
    "XOO",
    "X  "
  ],
  "currentPlayer": "O",
  "winner": "X",
  "gameOver": true
}
```

or

```json
{
  "grid": [
    "XOX",
    "XOO",
    "OXO"
  ],
  "currentPlayer": "O",
  "winner": "Tie",
  "gameOver": true
}
```

#### ai
`/tictactoe/ai/[random|smart]?user=username`

Sets the AI player that player `username` will play against to either `smart` (always makes the best move) or `random` (plays randomly).

The default is random.

Responds with 400 BAD REQUEST and an error if the `opponent` parameter is anything else.