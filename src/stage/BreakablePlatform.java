package stage;

import java.awt.Rectangle;

/**
 * Represents a platform that breaks after the player steps on it.
 * The platform has a timer that starts when the player lands on it,
 * and breaks after a certain duration.
 */
public class BreakablePlatform {
    private Rectangle bounds;
    private boolean isBroken;
    private boolean isTriggered;
    private int breakTimer;
    private final int BREAK_DELAY = 30; // ~0.5 seconds at 60fps
    
    public BreakablePlatform(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isBroken = false;
        this.isTriggered = false;
        this.breakTimer = 0;
    }
    
    /**
     * Triggers the platform to start breaking
     */
    public void trigger() {
        if (!isBroken && !isTriggered) {
            isTriggered = true;
            breakTimer = 0;
        }
    }
    
    /**
     * Updates the platform state
     * Should be called every frame
     */
    public void update() {
        if (isTriggered && !isBroken) {
            breakTimer++;
            if (breakTimer >= BREAK_DELAY) {
                isBroken = true;
            }
        }
    }
    
    /**
     * Resets the platform to its initial state
     */
    public void reset() {
        isBroken = false;
        isTriggered = false;
        breakTimer = 0;
    }
    
    // Getters
    public Rectangle getBounds() {
        return bounds;
    }
    
    public boolean isBroken() {
        return isBroken;
    }
    
    public boolean isTriggered() {
        return isTriggered;
    }
    
    public int getBreakTimer() {
        return breakTimer;
    }
    
    public int getBreakDelay() {
        return BREAK_DELAY;
    }
}
