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
    
    @Value("${rplace.snapshot-dir:snapshots}")
    private String snapshotDir;

    private BufferedImage lastSnapshot;

    public RPlaceImageSnapshotScheduler(RPlaceGrid grid) {
        this.grid = grid;
    }

    // default every 2 minutes
    @Scheduled(fixedRateString = "${rplace.snapshot-frequency:120000}")
    public void saveSnapshot() throws IOException 
    {
        BufferedImage image;
        String json;

        synchronized (grid) {
            // get lock on grid class
            // This is used to avoid concurrency issues when accessing the grid
            // get raw image, which is not scaled up
            image = grid.getRawImage();
            // ownership grid of each pixel in JSON format
            // this is used to track who owns each pixel
            // so that students don't write offensive things
            json = grid.getOwnershipGridJson();
         }
        

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

        Path outputFileImage = dir.resolve("rplace-image-" + timestamp + ".png");
        ImageIO.write(image, "png", outputFileImage.toFile());

        Path outputFileOwnership = dir.resolve("rplace-ownership-" + timestamp + ".json");
        Files.writeString(outputFileOwnership, json);

        System.out.println("Saved snapshot to " + outputFileImage.toAbsolutePath());
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

