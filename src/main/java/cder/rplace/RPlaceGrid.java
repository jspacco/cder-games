package cder.rplace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RPlaceGrid 
{
    private static final Logger log = LoggerFactory.getLogger(RPlaceGrid.class);

    private Color[][] grid;
    private String[][] ownershipGrid;
    private final int width;
    private final int height;
    private final int scale;

    public RPlaceGrid(
        @Value("${rplace.grid.width:200}") int width,
        @Value("${rplace.grid.height:200}") int height,
        @Value("${rplace.grid.default-color:white}") String defaultColor,
        @Value("${rplace.grid.scale:3}") int scale,
        @Value("${rplace.reload-snapshot:false}") boolean reloadSnapshot,
        @Value("${rplace.snapshot-dir:snapshots}") String snapshotDir)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.grid = new Color[height][width];
        this.ownershipGrid = new String[height][width];
        if (reloadSnapshot) {
            // reload most recent image from snapshotDir
            try {
                // list all files in snapshotDir
                Path dir = Paths.get(snapshotDir);
                if (!Files.exists(dir)) {
                    log.warn("Snapshot directory does not exist: " + snapshotDir);
                    return;
                }
                Path latestFile = getMostRecentFileOfType(dir, "*.png");
                if (latestFile == null) {
                    // no image found to load
                    log.warn("No snapshot image found in " + snapshotDir);
                    return;
                }

                String ownershipFileName = latestFile.getFileName().toString().replaceAll("image", "ownership");
                ownershipFileName = ownershipFileName.replaceAll("\\.png", ".json");
                Path ownershipFile = dir.resolve(ownershipFileName);
                log.trace("will try to load " + latestFile.toAbsolutePath() + " and " + ownershipFile.toAbsolutePath());
                if (!Files.exists(ownershipFile)) {
                    log.warn("No ownership file found for " + latestFile.getFileName());
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
                log.info("Loaded snapshot image from " + latestFile.toAbsolutePath());

                // Load ownership grid from JSON file
                String json = Files.readString(ownershipFile);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> ownershipMap = mapper.readValue(json, Map.class);
                if (!ownershipMap.containsKey("ownership")) {
                    //TODO: better excpetion?
                    throw new RuntimeException("Pixel Ownership data not found in JSON");
                }
                // json deserialized into List<List<String>> and not Object[][]
                List<List<String>> ownershipList = (List<List<String>>) ownershipMap.get("ownership");
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) { 
                        if (row < ownershipList.size() && col < ownershipList.get(0).size()) {
                            // Cast to String, assuming ownership is stored as String
                            ownershipGrid[row][col] = (String) ownershipList.get(row).get(col);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error loading snapshot image: " + e.getMessage());
                throw new RuntimeException("Failed to load snapshot image", e);
            }
        }
    }

    private Path getMostRecentFileOfType(Path dir, String glob) throws IOException {
        Path latestFile = null;
        long latestTime = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, glob)) {
            for (Path entry : stream) {
                long lastModified = Files.getLastModifiedTime(entry).toMillis();
                if (lastModified > latestTime) {
                    latestTime = lastModified;
                    latestFile = entry;
                }
            }
        }
        return latestFile;
    }

    public synchronized void setColor(String user, int row, int col, Color color) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            grid[row][col] = color;
            ownershipGrid[row][col] = user;
        } else {
            throw new BadPixelRequestException("Row or column index out of bounds");
        }
    }

    public synchronized String getOwnershipGridJson() 
    {
        // create json representation of the ownership grid
        // use Jackson since it comes wiht Spring Boot
        Map<String, Object> ownershipMap = new HashMap<>();
        ownershipMap.put("width", width);
        ownershipMap.put("height", height);
        ownershipMap.put("ownership", ownershipGrid);
        try {
            return new ObjectMapper().writeValueAsString(ownershipMap);
        } catch (IOException e) {
            log.error("Error converting ownership grid to JSON: " + e.getMessage());
            throw new RuntimeException("Failed to convert ownership grid to JSON", e);
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
