package stage;

import java.awt.Rectangle;

/**
 * Represents a springboard that propels the character upwards when stepped on.
 * The springboard has configurable position and propel height.
 */
public class Springboard {
    private Rectangle bounds;
    private double propelHeight;
    private boolean isCompressed;
    private int compressionTimer;
    private final int COMPRESSION_DURATION = 10; // ~0.17 seconds at 60fps
    
    public Springboard(int x, int y, int width, int height, double propelHeight) {
        this.bounds = new Rectangle(x, y, width, height);
        this.propelHeight = propelHeight;
        this.isCompressed = false;
        this.compressionTimer = 0;
    }
    
    /**
     * Triggers the springboard compression animation
     */
    public void trigger() {
        isCompressed = true;
        compressionTimer = 0;
    }
    
    /**
     * Updates the springboard state
     * Should be called every frame
     */
    public void update() {
        if (isCompressed) {
            compressionTimer++;
            if (compressionTimer >= COMPRESSION_DURATION) {
                isCompressed = false;
                compressionTimer = 0;
            }
        }
    }
    
    /**
     * Resets the springboard to its initial state
     */
    public void reset() {
        isCompressed = false;
        compressionTimer = 0;
    }
    
    // Getters
    public Rectangle getBounds() {
        return bounds;
    }
    
    public double getPropelHeight() {
        return propelHeight;
    }
    
    public boolean isCompressed() {
        return isCompressed;
    }
    
    public int getCompressionTimer() {
        return compressionTimer;
    }
    
    public int getCompressionDuration() {
        return COMPRESSION_DURATION;
    }
}
