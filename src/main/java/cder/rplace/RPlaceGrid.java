package cder.rplace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
        @Value("${rplace.grid.scale:3}") int scale)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.grid = new Color[height][width];
    }

    public synchronized void setColor(int row, int col, Color color) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            grid[row][col] = color;
        } else {
            throw new BadPixelRequestException("Row or column index out of bounds");
        }
    }

    public synchronized BufferedImage getImage() 
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
