package cder.rplace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RPlaceGrid 
{
    private Color[][] grid;
    private final int width;
    private final int height;
    private final int scale;

    public RPlaceGrid(
        @Value("${rplace.grid.width:200}") int width,
        @Value("${rplace.grid.height:200}") int height,
        @Value("${rplace.grid.default-color:white}") String defaultColor,
        @Value("${rplace.grid.scale:3}") int scale,
        @Value("${rplace.reload-snapshot:false}") boolean reloadSnapshot,
        @Value("${rplace.snapshot-dir:images/snapshots}") String snapshotDir)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.grid = new Color[height][width];
        if (reloadSnapshot) {
            // reload most recent image from snapshotDir
            try {
                // list all files in snapshotDir
                Path dir = Paths.get(snapshotDir);
                DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.png");
                Path latestFile = null;
                for (Path path : stream) {
                    if (latestFile == null || Files.getLastModifiedTime(path).toMillis() > Files.getLastModifiedTime(latestFile).toMillis()) {
                        latestFile = path;
                    }
                }
                stream.close();
                if (latestFile == null) {
                    // no image found to load
                    System.out.println("No snapshot image found in " + snapshotDir);
                    return;
                }

                BufferedImage img = ImageIO.read(latestFile.toFile());
                if (img != null) {
                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            if (row < img.getHeight() && col < img.getWidth()) {
                                // the saved image was scaled
                                grid[row][col] = new Color(img.getRGB(col, row));
                            } else {
                                // Default color
                                grid[row][col] = Color.WHITE; 
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading snapshot image: " + e.getMessage());
                throw new RuntimeException("Failed to load snapshot image", e);
            }
        }
    }

    public synchronized void setColor(int row, int col, Color color) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            grid[row][col] = color;
        } else {
            throw new BadPixelRequestException("Row or column index out of bounds");
        }
    }

    public synchronized BufferedImage getRawImage()
    {
        // Create a new BufferedImage that is the size of the grid
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == null) {
                    // Default color if no color is set
                    g.setColor(Color.WHITE);
                } else {
                    // Use the color set in the grid
                    g.setColor(grid[row][col]);
                }
                g.fillRect(col, row, 1, 1);
            }
        }
        g.dispose();
        return img;
    }

    public synchronized BufferedImage getScaledImage() 
    {
        BufferedImage img = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == null) {
                    // Default color if no color is set
                    g.setColor(Color.WHITE);
                } else {
                    // Use the color set in the grid
                    g.setColor(grid[row][col]);
                }
                g.fillRect(col * scale, row * scale, scale, scale);
            }
        }

        g.dispose();
        return img;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScale() {
        return scale;
    }
    
}
