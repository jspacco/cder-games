package cder.rplace;

import java.awt.Color;
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
        @RequestParam String user,
        @RequestParam String password,
        Model model)
    {
        if (!service.authenticate(user, password))
        {
            model.addAttribute("error", "cannot authenticate "+user+" with given password");
            return "error";
        }
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
        if (!service.authenticate(user, password))
        {
            model.addAttribute("error", "cannot authenticate "+user+" with given password");
            System.out.println("ERROR\n\n");
            return "error";
        }
        // do all of these by calling methods in service
        // TODO: rate limit for users
        // TODO: check illegal row/col
        // TODO: check illegal color
        // TODO: eventually return an enum

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