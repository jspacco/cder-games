package cder.rplace;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "rplace")
public class RPlaceConfig {

    private static final Logger log = LoggerFactory.getLogger(RPlaceConfig.class);

    // Spring binds this if present in YAML
    private Map<String, String> colors;

    // Always populated: hex strings converted to Color objects
    private Map<String, Color> colorMap = new LinkedHashMap<>();

    public void setColors(Map<String, String> c) {
        log.info("Setting colors from YAML: {}", c);
        this.colors = c;
        colorMap.clear();
        for (Map.Entry<String, String> entry : this.colors.entrySet()) {
            log.debug("Setting color {} to {}", entry.getKey(), entry.getValue());
            colorMap.put(entry.getKey(), Color.decode(entry.getValue()));
        }
    }

    @PostConstruct
    public void initDefaultsIfMissing() {
        // defaults if rplace.colors is not set
        if (this.colors == null || this.colors.isEmpty()) {
            log.warn("No colors defined in YAML. Falling back to defaults.");
            colorMap = Map.ofEntries(
                Map.entry("red", Color.RED),
                Map.entry("green", Color.GREEN),
                Map.entry("blue", Color.BLUE),
                Map.entry("black", Color.BLACK),
                Map.entry("white", Color.WHITE),
                Map.entry("magenta", Color.MAGENTA),
                Map.entry("yellow", Color.YELLOW),
                Map.entry("orange", Color.ORANGE),
                Map.entry("cyan", Color.CYAN),
                Map.entry("pink", Color.PINK),
                Map.entry("gray", Color.GRAY),
                Map.entry("darkgray", Color.DARK_GRAY),
                Map.entry("lightgray", Color.LIGHT_GRAY));
            
            for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
                colors.put(entry.getKey(),  String.format("#%06X", (0xFFFFFF & entry.getValue().getRGB())));
            }
        }
    }

    public Map<String, String> getColorsAsHex() {
        return colors;
    }

    public Map<String, Color> getColors() {
        return colorMap;
    }

    public Color getColor(String name) {
        return colorMap.get(name);
    }
}
