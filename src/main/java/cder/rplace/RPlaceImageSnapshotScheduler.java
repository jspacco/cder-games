package cder.rplace;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RPlaceImageSnapshotScheduler {

    private final RPlaceGrid grid;
    
    @Value("${rplace.snapshot-dir:images/snapshots}")
    private String snapshotDir;

    private BufferedImage lastSnapshot;

    public RPlaceImageSnapshotScheduler(RPlaceGrid grid) {
        this.grid = grid;
    }

    // every 2 minutes
    //@Scheduled(fixedRate = 120000)
    @Scheduled(fixedRate = 5000)
    public void saveSnapshot() throws IOException 
    {
        // get the raw image of the grid
        // this is the one that is not scaled up
        BufferedImage image = grid.getRawImage();

        if (lastSnapshot != null && imagesAreEqual(lastSnapshot, image)) {
            // No change in the image, skip saving
            System.out.println("No changes detected in the image, skipping snapshot.");
            return;
        }
        lastSnapshot = image;
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path dir = Paths.get(snapshotDir);
        Files.createDirectories(dir);

        Path outputFile = dir.resolve("rplace-" + timestamp + ".png");
        ImageIO.write(image, "png", outputFile.toFile());
        System.out.println("Saved snapshot to " + outputFile.toAbsolutePath());
    }

    public boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

}

