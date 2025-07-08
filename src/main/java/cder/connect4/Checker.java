package cder.connect4;

public enum Checker 
{
    RED,
    YELLOW;

    public String toString() {
        return this.name().charAt(0) + "";
    }
}
