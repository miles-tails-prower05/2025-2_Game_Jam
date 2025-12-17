package stage;

import java.awt.Color;

/**
 * Holds color configuration for different game objects in a stage.
 * Provides default colors that can be overridden per stage.
 */
public class StageColors {
    // Default colors
    private static final Color DEFAULT_PLATFORM_COLOR = new Color(180, 100, 50);
    private static final Color DEFAULT_SPIKE_COLOR = Color.LIGHT_GRAY;
    private static final Color DEFAULT_BREAKABLE_PLATFORM_COLOR = new Color(150, 80, 40);
    private static final Color DEFAULT_SPRINGBOARD_COLOR = new Color(200, 120, 60);
    
    // Stage-specific colors (null means use default)
    private Color platformColor;
    private Color spikeColor;
    private Color breakablePlatformColor;
    private Color springboardColor;
    
    public StageColors() {
        // Initialize with defaults
        this.platformColor = DEFAULT_PLATFORM_COLOR;
        this.spikeColor = DEFAULT_SPIKE_COLOR;
        this.breakablePlatformColor = DEFAULT_BREAKABLE_PLATFORM_COLOR;
        this.springboardColor = DEFAULT_SPRINGBOARD_COLOR;
    }
    
    /**
     * Parses a color string in hex format (#RRGGBB) or RGB format (R,G,B)
     * @param colorStr The color string to parse
     * @return Parsed Color object, or null if parsing fails
     */
    public static Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) {
            return null;
        }
        
        colorStr = colorStr.trim();
        
        try {
            // Hex format: #RRGGBB
            if (colorStr.startsWith("#")) {
                return Color.decode(colorStr);
            }
            
            // RGB format: R,G,B
            if (colorStr.contains(",")) {
                String[] parts = colorStr.split(",");
                if (parts.length == 3) {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    
                    // Validate RGB values are in range 0-255
                    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                        System.out.println("Invalid color format: " + colorStr + ". RGB values must be between 0-255.");
                        return null;
                    }
                    
                    return new Color(r, g, b);
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid color format: " + colorStr + ". Expected formats: #RRGGBB or R,G,B");
        }
        
        return null;
    }
    
    // Setters
    public void setPlatformColor(String colorStr) {
        Color parsed = parseColor(colorStr);
        if (parsed != null) {
            this.platformColor = parsed;
        }
    }
    
    public void setSpikeColor(String colorStr) {
        Color parsed = parseColor(colorStr);
        if (parsed != null) {
            this.spikeColor = parsed;
        }
    }
    
    public void setBreakablePlatformColor(String colorStr) {
        Color parsed = parseColor(colorStr);
        if (parsed != null) {
            this.breakablePlatformColor = parsed;
        }
    }
    
    public void setSpringboardColor(String colorStr) {
        Color parsed = parseColor(colorStr);
        if (parsed != null) {
            this.springboardColor = parsed;
        }
    }
    
    // Getters - always return a valid color
    public Color getPlatformColor() {
        return platformColor;
    }
    
    public Color getSpikeColor() {
        return spikeColor;
    }
    
    public Color getBreakablePlatformColor() {
        return breakablePlatformColor;
    }
    
    public Color getSpringboardColor() {
        return springboardColor;
    }
}
