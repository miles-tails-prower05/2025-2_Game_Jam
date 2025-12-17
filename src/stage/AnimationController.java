package stage;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Manages character animations and state transitions.
 * Handles loading images, frame timing, and providing the current frame.
 */
public class AnimationController {
    // Character state
    private CharacterState currentState;
    private boolean facingRight; // true = right, false = left
    
    // Images for each state
    private Image idleImage;
    private Image jumpImage;
    private Image[] walkImages; // 4 frames
    
    // Animation timing
    private int frameCounter;
    private final int FRAME_DELAY = 8; // Frames to wait before switching (approximately 128ms at 60fps)
    private int currentWalkFrame;
    
    /**
     * Creates a new AnimationController and loads all character images.
     */
    public AnimationController() {
        this.currentState = CharacterState.IDLE;
        this.facingRight = true;
        this.frameCounter = 0;
        this.currentWalkFrame = 0;
        this.walkImages = new Image[4];
        
        loadImages();
    }
    
    /**
     * Loads all character images from the images directory.
     */
    private void loadImages() {
        try {
            // Load idle image
            ImageIcon idleIcon = new ImageIcon("src/stage/images/char_idle.png");
            idleImage = idleIcon.getImage();
            
            // Load jump image
            ImageIcon jumpIcon = new ImageIcon("src/stage/images/char_jump.png");
            jumpImage = jumpIcon.getImage();
            
            // Load walk images
            for (int i = 0; i < 4; i++) {
                ImageIcon walkIcon = new ImageIcon("src/stage/images/char_walk_0" + (i + 1) + ".png");
                walkImages[i] = walkIcon.getImage();
            }
            
            System.out.println("Character images loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load character images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the animation state. Should be called every frame.
     */
    public void update() {
        // Only update frame counter for walking animation
        if (currentState == CharacterState.WALKING) {
            frameCounter++;
            if (frameCounter >= FRAME_DELAY) {
                frameCounter = 0;
                currentWalkFrame = (currentWalkFrame + 1) % 4; // Cycle through 0-3
            }
        } else {
            // Reset walk animation when not walking
            frameCounter = 0;
            currentWalkFrame = 0;
        }
    }
    
    /**
     * Sets the current character state.
     * @param newState The new state to transition to
     */
    public void setState(CharacterState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            // Reset animation when state changes
            if (newState != CharacterState.WALKING) {
                frameCounter = 0;
                currentWalkFrame = 0;
            }
        }
    }
    
    /**
     * Sets the direction the character is facing.
     * @param facingRight true if facing right, false if facing left
     */
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
    /**
     * Gets the current character state.
     * @return The current CharacterState
     */
    public CharacterState getState() {
        return currentState;
    }
    
    /**
     * Gets the current frame image based on the character's state.
     * @return The Image to display for the current state and frame
     */
    public Image getCurrentFrame() {
        switch (currentState) {
            case IDLE:
                return idleImage;
            case JUMPING:
                return jumpImage;
            case WALKING:
                return walkImages[currentWalkFrame];
            default:
                return idleImage;
        }
    }
    
    /**
     * Gets whether the character is facing right.
     * @return true if facing right, false if facing left
     */
    public boolean isFacingRight() {
        return facingRight;
    }
    
    /**
     * Resets the animation controller to initial state.
     */
    public void reset() {
        currentState = CharacterState.IDLE;
        facingRight = true;
        frameCounter = 0;
        currentWalkFrame = 0;
    }
}
