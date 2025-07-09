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
    public String canvas(
        // @RequestParam String user,
        // @RequestParam String password,
        Model model)
    {
        // if (!service.authenticate(user, password))
        // {
        //     model.addAttribute("error", "cannot authenticate "+user+" with given password");
        //     return "error";
        // }
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
            model.addAttribute("error", "cannot authenticate "+user+" with given password");
            System.out.println("ERROR\n\n");
            return "error";
        }

        // bounds check
        if (!service.boundsCheck(row, col)) {
            model.addAttribute("error", 
                "row or column index out of bounds. Max row is "+
                service.getHeight()+", max col is "+service.getWidth());
            return "error";
        }

        // check if color is valid
        if (!service.isValidColor(color)) {
            model.addAttribute("error", "Invalid color: " + color);
            return "error";
        }

        if (!service.canPlacePixel(user)) {
            long nextPixelTime = service.getNextPixelTime(user);
            String message = "Rate limit exceeded, next pixel in "+nextPixelTime+" ms";
            System.out.println(message);
            model.addAttribute("error", message);
            return "error";
        }

        boolean success = service.setColor(row, col, color);
        if (success) {
            model.addAttribute("success", "successful setColor");
        } else {
            model.addAttribute("error", "failed to setColor()");
        }
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