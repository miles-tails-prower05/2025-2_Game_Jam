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
    
    // Water skiing image constants (for stage 6 - Rival Battle)
    private static final String SKI_IDLE_IMAGE = IMAGE_BASE_PATH + "char_ski_idle.png";
    private static final String SKI_MOVE_IMAGE_PREFIX = IMAGE_BASE_PATH + "char_ski_move_0";
    private static final String SKI_MOVE_IMAGE_SUFFIX = ".png";
    
    // Character state
    private CharacterState currentState;
    private boolean facingRight; // true = right, false = left
    private boolean useWaterSkiingSprites; // true when in stage 6 (Rival Battle)
    
    // Normal images for each state
    private Image idleImage;
    private Image jumpImage;
    private Image[] walkImages; // 4 frames
    
    // Water skiing images for stage 6
    private Image skiIdleImage;
    private Image[] skiMoveImages; // 4 frames
    
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
        this.useWaterSkiingSprites = false;
        this.frameCounter = 0;
        this.currentWalkFrame = 0;
        this.walkImages = new Image[4];
        this.skiMoveImages = new Image[4];
        
        loadImages();
        loadWaterSkiingImages();
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
     * Loads water skiing images for stage 6 (Rival Battle).
     */
    private void loadWaterSkiingImages() {
        boolean allLoaded = true;
        
        try {
            // Load skiing idle image
            ImageIcon skiIdleIcon = new ImageIcon(SKI_IDLE_IMAGE);
            if (skiIdleIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                skiIdleImage = skiIdleIcon.getImage();
            } else {
                System.err.println("Warning: Failed to load water skiing idle image from " + SKI_IDLE_IMAGE);
                allLoaded = false;
            }
        } catch (Exception e) {
            System.err.println("Error loading water skiing idle image: " + e.getMessage());
            allLoaded = false;
        }
        
        // Load skiing movement images
        for (int i = 0; i < 4; i++) {
            try {
                String skiMovePath = SKI_MOVE_IMAGE_PREFIX + (i + 1) + SKI_MOVE_IMAGE_SUFFIX;
                ImageIcon skiMoveIcon = new ImageIcon(skiMovePath);
                if (skiMoveIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                    skiMoveImages[i] = skiMoveIcon.getImage();
                } else {
                    System.err.println("Warning: Failed to load water skiing move image " + (i + 1) + " from " + skiMovePath);
                    allLoaded = false;
                }
            } catch (Exception e) {
                System.err.println("Error loading water skiing move image " + (i + 1) + ": " + e.getMessage());
                allLoaded = false;
            }
        }
        
        if (allLoaded) {
            System.out.println("Water skiing images loaded successfully");
        } else {
            System.err.println("Warning: Some water skiing images failed to load. Stage 6 will use normal sprites.");
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
        // Use water skiing sprites if enabled and available
        if (useWaterSkiingSprites) {
            switch (currentState) {
                case IDLE:
                    return (skiIdleImage != null) ? skiIdleImage : idleImage;
                case JUMPING:
                    // Use skiing idle for jumping in water skiing mode
                    return (skiIdleImage != null) ? skiIdleImage : jumpImage;
                case WALKING:
                    if (skiMoveImages[currentWalkFrame] != null) {
                        return skiMoveImages[currentWalkFrame];
                    }
                    return (walkImages[currentWalkFrame] != null) ? walkImages[currentWalkFrame] : idleImage;
                default:
                    return (skiIdleImage != null) ? skiIdleImage : idleImage;
            }
        }
        
        // Normal sprites
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
     * Sets the current stage to determine which sprite set to use.
     * @param stageName The name of the current stage
     */
    public void setStage(String stageName) {
        // Enable water skiing sprites for stage 6 (Rival Battle)
        useWaterSkiingSprites = "Rival Battle".equals(stageName);
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
