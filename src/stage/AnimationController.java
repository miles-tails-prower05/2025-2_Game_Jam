package stage;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Manages character animations and state transitions.
 * Handles loading images, frame timing, and providing the current frame.
 */
public class AnimationController {
    // Image path constants
    private static final String IMAGE_BASE_PATH = "src/stage/images/";
    private static final String IDLE_IMAGE = IMAGE_BASE_PATH + "char_idle.png";
    private static final String JUMP_IMAGE = IMAGE_BASE_PATH + "char_jump.png";
    private static final String WALK_IMAGE_PREFIX = IMAGE_BASE_PATH + "char_walk_0";
    private static final String WALK_IMAGE_SUFFIX = ".png";
    
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
        boolean allLoaded = true;
        
        try {
            // Load idle image
            ImageIcon idleIcon = new ImageIcon(IDLE_IMAGE);
            if (idleIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                idleImage = idleIcon.getImage();
            } else {
                System.err.println("Warning: Failed to load idle image from " + IDLE_IMAGE);
                allLoaded = false;
            }
        } catch (Exception e) {
            System.err.println("Error loading idle image: " + e.getMessage());
            allLoaded = false;
        }
        
        try {
            // Load jump image
            ImageIcon jumpIcon = new ImageIcon(JUMP_IMAGE);
            if (jumpIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                jumpImage = jumpIcon.getImage();
            } else {
                System.err.println("Warning: Failed to load jump image from " + JUMP_IMAGE);
                allLoaded = false;
            }
        } catch (Exception e) {
            System.err.println("Error loading jump image: " + e.getMessage());
            allLoaded = false;
        }
        
        // Load walk images
        for (int i = 0; i < 4; i++) {
            try {
                String walkPath = WALK_IMAGE_PREFIX + (i + 1) + WALK_IMAGE_SUFFIX;
                ImageIcon walkIcon = new ImageIcon(walkPath);
                if (walkIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                    walkImages[i] = walkIcon.getImage();
                } else {
                    System.err.println("Warning: Failed to load walk image " + (i + 1) + " from " + walkPath);
                    allLoaded = false;
                }
            } catch (Exception e) {
                System.err.println("Error loading walk image " + (i + 1) + ": " + e.getMessage());
                allLoaded = false;
            }
        }
        
        if (allLoaded) {
            System.out.println("Character images loaded successfully");
        } else {
            System.err.println("Warning: Some character images failed to load. Game may use fallback rendering.");
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
