package cder.rplace;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

@Service
public class RPlaceService 
{   
    private final RPlaceGrid grid;
    private final AccountManager accountManager;

    public RPlaceService(RPlaceGrid grid, AccountManager manager) {
        this.grid = grid;
        this.accountManager = manager;
    }

    public boolean authenticate(String user, String password) {
        return accountManager.isValid(user, password);
    }

    public boolean setColor(int row, int col, String color) {

        // TODO: rate limit for users
        // TODO: check illegal row/col
        // TODO: check illegal color
        // TODO: eventually return an enum

        Color c = toColor(color);
        grid.setColor(row, col, c);
        return true;
    }
    
    private Color toColor(String color)
    {
        color = color.toLowerCase();
        switch(color) {
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "green": return Color.GREEN;
            case "magenta": return Color.MAGENTA;
            case "white": return Color.WHITE;
            case "black": return Color.BLACK;
            case "yellow": return Color.YELLOW;
            case "orange": return Color.ORANGE;
            case "cyan": return Color.CYAN;
            case "pink": return Color.PINK;
            case "gray": return Color.GRAY;
            case "darkgray": return Color.DARK_GRAY;
            case "lightgray": return Color.LIGHT_GRAY;
            default:
            throw new IllegalArgumentException(color+" is not a recognized color");
        }
    }

    public BufferedImage getCurrentImage() {
        return grid.getImage();
    }

    public int getWidth() {
        return grid.getWidth();
    }

    public int getHeight() {
        return grid.getHeight();
    }
    
}
