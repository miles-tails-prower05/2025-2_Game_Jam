package stage;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Represents a rival character that automatically races toward the goal.
 * Only active in Stage 6 (Rival Battle).
 */
public class RivalCharacter {
    // Position and size
    private double x;
    private double y;
    private final int width = 40;
    private final int height = 40;
    
    // Physics
    private double velocityX;
    private double velocityY;
    private boolean onGround;
    
    // AI parameters
    private final double moveSpeed = 10.0; // Slightly slower than player's 15.0
    private final int detectionRange = 150; // Pixels ahead to detect obstacles
    private final int detectionHeight = 100; // Height to check for obstacles
    private final int groundCheckDistance = 50; // Distance ahead to check for ground
    private final int groundCheckDepth = 100; // Depth to check below rival
    
    // AI state
    private int jumpCooldown = 0;
    private final int jumpCooldownMax = 30; // Frames between jumps
    
    /**
     * Creates a new rival character at the specified spawn position.
     */
    public RivalCharacter(int spawnX, int spawnY) {
        this.x = spawnX;
        this.y = spawnY;
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
    }
    
    /**
     * Updates the rival's position and AI logic.
     * @param mapManager The map manager to check collisions
     */
    public void update(MapManager mapManager) {
        // AI: Always move forward
        velocityX = moveSpeed;
        
        // AI: Detect obstacles ahead and jump if necessary
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        
        if (onGround && jumpCooldown == 0) {
            if (shouldJump(mapManager)) {
                velocityY = mapManager.getJumpStrength();
                onGround = false;
                jumpCooldown = jumpCooldownMax;
            }
        }
        
        // Apply horizontal movement
        x += velocityX;
        
        // Apply gravity
        velocityY += mapManager.getGravity();
        y += velocityY;
        
        // Check collisions
        checkCollisions(mapManager);
    }
    
    /**
     * Determines if the rival should jump based on obstacle detection.
     */
    private boolean shouldJump(MapManager mapManager) {
        Rectangle detectionBox = new Rectangle(
            (int)x + width,
            (int)y,
            detectionRange,
            detectionHeight
        );
        
        // Check for spikes ahead
        for (Rectangle spike : mapManager.getSpikes()) {
            if (detectionBox.intersects(spike)) {
                return true;
            }
        }
        
        // Check for holes (lack of platform ahead at ground level)
        Rectangle groundCheck = new Rectangle(
            (int)x + width + groundCheckDistance,
            (int)y + height,
            groundCheckDistance,
            groundCheckDepth
        );
        
        boolean hasGroundAhead = false;
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (groundCheck.intersects(platform)) {
                hasGroundAhead = true;
                break;
            }
        }
        
        // Check springboards as ground
        for (Springboard sb : mapManager.getSpringboards()) {
            if (groundCheck.intersects(sb.getBounds())) {
                hasGroundAhead = true;
                break;
            }
        }
        
        // Check breakable platforms as ground (if not broken)
        for (BreakablePlatform bp : mapManager.getBreakablePlatforms()) {
            if (!bp.isBroken() && groundCheck.intersects(bp.getBounds())) {
                hasGroundAhead = true;
                break;
            }
        }
        
        // If no ground ahead and we're near an edge, jump
        if (!hasGroundAhead) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks and resolves collisions with platforms and objects.
     */
    private void checkCollisions(MapManager mapManager) {
        Rectangle rivalRect = new Rectangle((int)x, (int)y, width, height);
        onGround = false;
        
        // Check regular platforms
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (rivalRect.intersects(platform)) {
                if (velocityY > 0) { // Falling down
                    y = platform.y - height;
                    velocityY = 0;
                    onGround = true;
                } else if (velocityY < 0) { // Moving up
                    y = platform.y + platform.height;
                    velocityY = 0;
                }
            }
        }
        
        // Check breakable platforms (if not broken)
        for (BreakablePlatform bp : mapManager.getBreakablePlatforms()) {
            if (!bp.isBroken()) {
                Rectangle platform = bp.getBounds();
                if (rivalRect.intersects(platform)) {
                    if (velocityY > 0) {
                        y = platform.y - height;
                        velocityY = 0;
                        onGround = true;
                        bp.trigger(); // Rival also triggers breakable platforms
                    } else if (velocityY < 0) {
                        y = platform.y + platform.height;
                        velocityY = 0;
                    }
                }
            }
        }
        
        // Check springboards
        for (Springboard sb : mapManager.getSpringboards()) {
            Rectangle springboard = sb.getBounds();
            if (rivalRect.intersects(springboard)) {
                if (velocityY > 0) {
                    y = springboard.y - height;
                    velocityY = -sb.getPropelHeight();
                    onGround = false;
                    sb.trigger();
                } else if (velocityY < 0) {
                    y = springboard.y + springboard.height;
                    velocityY = 0;
                }
            }
        }
    }
    
    /**
     * Resets the rival to the spawn position.
     */
    public void reset(int spawnX, int spawnY) {
        this.x = spawnX;
        this.y = spawnY;
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
        this.jumpCooldown = 0;
    }
    
    /**
     * Returns the rival's bounding box for collision detection.
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    // Getters
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
