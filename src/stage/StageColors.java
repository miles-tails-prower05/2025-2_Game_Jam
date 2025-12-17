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
                    return new Color(r, g, b);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse color: " + colorStr);
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
    
    // Getters - always return a valid color (default if not set)
    public Color getPlatformColor() {
        return platformColor != null ? platformColor : DEFAULT_PLATFORM_COLOR;
    }
    
    public Color getSpikeColor() {
        return spikeColor != null ? spikeColor : DEFAULT_SPIKE_COLOR;
    }
    
    public Color getBreakablePlatformColor() {
        return breakablePlatformColor != null ? breakablePlatformColor : DEFAULT_BREAKABLE_PLATFORM_COLOR;
    }
    
    public Color getSpringboardColor() {
        return springboardColor != null ? springboardColor : DEFAULT_SPRINGBOARD_COLOR;
    }
}
