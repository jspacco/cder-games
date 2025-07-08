package cder.connect4;

public record Move(int column, Checker player) {
    public Move {
        if (column < 0 || column >= 7) {
            throw new IllegalArgumentException("Column must be between 0 and 6");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
    // TODO: turn into JSON
}
