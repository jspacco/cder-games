package cder;

public enum OpponentAI 
{
    RANDOM,
    SMART,
    ;

    public static OpponentAI fromString(String opponent) {
        switch (opponent.toLowerCase()) {
            case "random":
                return RANDOM;
            case "smart":
                return SMART;
            default:
                throw new IllegalArgumentException("Unknown opponent type: " + opponent);
        }
    }

}
