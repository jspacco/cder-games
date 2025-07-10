package cder.rplace;

import java.awt.Color;
import java.awt.image.BufferedImage;
import static java.lang.String.format;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RPlaceService 
{   
    private final RPlaceGrid grid;
    private final AccountManager accountManager;
    private final Map<String, UserQuota> userQuotas = new ConcurrentHashMap<>();
    private final int maxPixelsPerBatch;
    private final long cooldownMillis;
    // How often should we generate a new image from the grid?
    // we don't do this after ever pixel change to avoid performance issues
    private final long imageUpdateFrequency;
    private long lastImageUpdate;
    private BufferedImage cachedImage;


    public RPlaceService(RPlaceGrid grid, AccountManager manager,
        @Value("${rplace.max-pixels-per-batch:20}") int maxPixelsPerBatch,
        @Value("${rplace.cooldown-millis:120000}") long cooldownMillis,
        @Value("${rplace.image-update-frequency:2000}") long imageUpdateFrequency)
    {
        this.grid = grid;
        this.accountManager = manager;
        this.maxPixelsPerBatch = maxPixelsPerBatch;
        this.cooldownMillis = cooldownMillis;
        this.imageUpdateFrequency = imageUpdateFrequency;
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
        long now = System.currentTimeMillis();
        if (cachedImage == null || now - lastImageUpdate > imageUpdateFrequency) {
            // Update the cached image if it's null or cooldown has expired
            cachedImage = grid.getScaledImage();
            lastImageUpdate = now;
        }
        return cachedImage;
    }

    public int getScale() {
        return grid.getScale();
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
            if (now - quota.batchStartTime > cooldownMillis) {
                // Cooldown expired: reset quota
                quota.pixelsUsed = 0;
                quota.batchStartTime = now;
            }

            if (quota.pixelsUsed < maxPixelsPerBatch) {
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


    public int getNextPixelTime(String user) {
        UserQuota quota = userQuotas.get(user);
        if (quota == null) {
            throw new AuthenticationException(format("User %s not found", user));
        }
        long now = System.currentTimeMillis();
        long nextPixelTime = quota.batchStartTime + cooldownMillis;
        long timeToNextPixel = Math.max(0, nextPixelTime - now);
        // return time in seconds
        return (int) timeToNextPixel / 1000; 
    }

}
