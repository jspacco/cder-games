package cder.rplace;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RPlaceGrid 
{
    private Color[][] grid;
    private int width;
    private int height;

    public RPlaceGrid(
        @Value("${rplace.grid.width}") int width,
        @Value("${rplace.grid.height}") int height)
    {
        this.width = width;
        this.height = height;
        this.grid = new Color[height][width];
    }

    public synchronized void setColor(int row, int col, Color color) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            grid[row][col] = color;
        } else {
            throw new BadPixelRequestException("Row or column index out of bounds");
        }
    }

    public synchronized BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = grid[row][col];
                if (color != null) {
                    image.setRGB(col, row, color.getRGB());
                } else {
                    // Default to white if no color set
                    image.setRGB(col, row, Color.WHITE.getRGB()); 
                }
            }
        }
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
}
