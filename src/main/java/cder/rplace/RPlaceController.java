package cder.rplace;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.String.format;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rplace")
public class RPlaceController 
{
    private RPlaceService service;

    public RPlaceController(RPlaceService service) {
        this.service = service;
    }

    @GetMapping("/canvas")
    public String canvas(Model model)
    {
        model.addAttribute("width", service.getWidth() * service.getScale());
        model.addAttribute("height", service.getHeight() * service.getScale());
        return "canvas";
    }

    @GetMapping("/setColor")
    public ResponseEntity<String> setPixelColor(
        @RequestParam String user,
        @RequestParam String password,
        @RequestParam int row, 
        @RequestParam int col, 
        @RequestParam String color,
        Model model)
    {
        // authenticate user
        if (!service.authenticate(user, password))
        {
            throw new AuthenticationException(format("Cannot authenticate user %s ", user));
        }

        // bounds check
        if (!service.boundsCheck(row, col)) {
            throw new BadPixelRequestException(format(
                "Row %d or col %d out of bounds. "+
                "Max row is %d, max col is %d", row, col, service.getHeight() - 1, service.getWidth() - 1));
        }

        // check if color is valid
        if (!service.isValidColor(color)) {
            throw new BadPixelRequestException(format(
                "Invalid color %s. Valid color are: "+ 
                "red, blue, green, magenta, white, black, yellow, orange, cyan, pink, gray, darkgray, lightgray.", color));
        }

        // rate limit check
        if (!service.canPlacePixel(user)) {
            int timeToNextPixel = service.getTimeToNextPixel(user);
            throw new BadPixelRequestException(format("Rate limit exceeded for user %s. "+
                "Next pixel can be placed in %d ", user, timeToNextPixel));
        }

        boolean success = service.setColor(user, row, col, color);
        if (!success) {
            throw new BadPixelRequestException(format(
                "Failed to set color at (%d, %d). "+ 
                "Unknown error! This should not happen if bounds and color checks passed."+
                " Tell Spacco to check the server logs.", row, col));
        }

        return ResponseEntity.ok("success");
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImage() throws IOException {
        BufferedImage image = service.getCurrentImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStats(
        @RequestParam String user,
        @RequestParam String password)
    {
        // authenticate user
        if (!service.authenticate(user, password)) {
            throw new AuthenticationException(format("Cannot authenticate user %s ", user));
        }

        int timeToNextPixel = service.getTimeToNextPixel(user);
        int totalPixelsPlaced = service.getNumPixelsPlaced(user);
        return ResponseEntity.ok(
            format("Next pixel can be placed in %d seconds\n"+
                "User %s has placed %d pixels so far", 
                    timeToNextPixel, user, totalPixelsPlaced));
    }

    @GetMapping("/countdown")
    public ResponseEntity<String> getCountdown(
        @RequestParam String user,
        @RequestParam String password)
    {
        // authenticate user
        if (!service.authenticate(user, password)) {
            throw new AuthenticationException(format("Cannot authenticate user %s ", user));
        }

        int timeToNextPixel = service.getTimeToNextPixel(user);
        return ResponseEntity.ok(timeToNextPixel+"");
    }
}