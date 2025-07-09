package cder.rplace;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
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
        model.addAttribute("width", service.getWidth());
        model.addAttribute("height", service.getHeight());
        return "canvas";
    }

    @GetMapping("/setColor")
    public String setPixelColor(
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
            throw new AuthenticationException("Cannot authenticate user: " + user);
        }

        // bounds check
        if (!service.boundsCheck(row, col)) {
            throw new BadPixelRequestException(
                "Row "+row+" or col "+col+" out of bounds. "+
                "Max row is "+
                service.getHeight()+", max col is "+service.getWidth());
        }

        // check if color is valid
        if (!service.isValidColor(color)) {
            throw new BadPixelRequestException("Invalid color: " + color + 
                ". Valid colors are: red, blue, green, magenta, white, black, yellow, orange, cyan, pink, gray, darkgray, lightgray.");
        }

        // rate limit check
        if (!service.canPlacePixel(user)) {
            int timeToNextPixel = service.getNextPixelTime(user);
            throw new BadPixelRequestException("Rate limit exceeded for user: " + user+
                ". Next pixel can be placed in " + timeToNextPixel +
                " seconds.");
        }

        boolean success = service.setColor(row, col, color);
        if (!success) {
            throw new BadPixelRequestException(
                "Failed to set color at (" + row + ", " + col + 
                "). Unknown error! This should not happen if bounds and color checks passed."+
                " Tell Spacco to check the server logs.");
        }
        model.addAttribute("success", "successfully setColor");
        model.addAttribute("width", service.getWidth());
        model.addAttribute("height", service.getHeight());
        return "canvas";
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImage() throws IOException {
        BufferedImage image = service.getCurrentImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    // @GetMapping("/stats")
    // public @ResponseBody RPlaceStats getStats(@RequestParam String user) {
    //     return service.getStats();
    // }
}