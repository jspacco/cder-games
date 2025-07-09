package cder.rplace;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RPlaceService 
{   
    private final RPlaceGrid grid;
    private final AccountManager accountManager;
    private final Map<String, UserQuota> userQuotas = new ConcurrentHashMap<>();
    private static final int MAX_PIXELS_PER_BATCH = 20;
    private static final long COOLDOWN_MILLIS = 2 * 60 * 1000; // 2 minutes


    public RPlaceService(RPlaceGrid grid, AccountManager manager) {
        this.grid = grid;
        this.accountManager = manager;
    }

    public boolean authenticate(String user, String password) {
        return accountManager.isValid(user, password);
    }

    public boolean setColor(int row, int col, String color)
    {
        if (!boundsCheck(row, col)) return false;
        if (!isValidColor(color)) return false;
        
        Color c = toColor(color);
        grid.setColor(row, col, c);
        return true;
    }
    
    public boolean isValidColor(String color) {
        return toColor(color) != null;
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
            return null; // invalid color
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

    public boolean boundsCheck(int row, int col) {
        return row >= 0 && row < grid.getHeight() && col >= 0 && col < grid.getWidth();
    }

    public boolean canPlacePixel(String user) {
        long now = System.currentTimeMillis();
        UserQuota quota = userQuotas.computeIfAbsent(user, u -> new UserQuota(0, now));

        synchronized (quota) {
            if (now - quota.batchStartTime > COOLDOWN_MILLIS) {
                // Cooldown expired: reset quota
                quota.pixelsUsed = 0;
                quota.batchStartTime = now;
            }

            if (quota.pixelsUsed < MAX_PIXELS_PER_BATCH) {
                quota.pixelsUsed++;
                return true;
            } else {
                return false; // quota exceeded
            }
        }
    }

    
    class UserQuota {
        int pixelsUsed;
        long batchStartTime;
        UserQuota(int pixelsUsed, long batchStartTime) {
            this.pixelsUsed = pixelsUsed;
            this.batchStartTime = batchStartTime;
        }
    }


    public long getNextPixelTime(String user) {
        UserQuota quota = userQuotas.get(user);
        if (quota == null) {
            throw new IllegalArgumentException("User not found");
        }
        long now = System.currentTimeMillis();
        long nextPixelTime = quota.batchStartTime + COOLDOWN_MILLIS;
        return Math.max(0, nextPixelTime - now);
    }

}
