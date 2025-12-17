package stage;

/**
 * Enum representing the different states a character can be in.
 * Each state corresponds to different animations or visual representations.
 */
public enum CharacterState {
    /**
     * Character is standing still (1 static image)
     */
    IDLE,
    
    /**
     * Character is moving left or right (4-frame animation loop)
     */
    WALKING,
    
    /**
     * Character is in the air (1 static image)
     */
    JUMPING
}
