# Character Animation System

This document describes the character animation system implemented for the 2D platformer game.

## Overview

The animation system provides dynamic character visuals that respond to player input and game state. It replaces the static blue rectangle with animated character sprites.

## Architecture

### Components

1. **CharacterState (Enum)**
   - Defines the three possible character states:
     - `IDLE`: Character standing still
     - `WALKING`: Character moving left or right
     - `JUMPING`: Character in the air

2. **AnimationController (Class)**
   - Manages all animation logic
   - Loads and caches character images
   - Handles frame timing for animations
   - Provides current frame based on state

3. **GamePanel Integration**
   - Updates animation state based on player input
   - Renders current animation frame
   - Handles character direction (left/right flipping)

## Image Resources

The system uses the following images located in `src/stage/images/`:
- `char_idle.png` - Idle state (1 frame)
- `char_walk_01.png` through `char_walk_04.png` - Walking animation (4 frames)
- `char_jump.png` - Jump state (1 frame)

## State Transitions

The character state is determined by:
- **Not on ground** → JUMPING state
- **On ground + moving (left/right pressed)** → WALKING state
- **On ground + not moving** → IDLE state

## Animation Timing

- Walking animation cycles through 4 frames
- Frame delay: 8 game ticks (~128ms at 60fps)
- Smooth transitions between states

## Direction Handling

- Character facing direction is tracked independently
- Images are horizontally flipped when facing left
- Direction updates based on last movement input

## Usage

The animation system is automatically integrated into the game. No additional setup required.

### Key Methods

**AnimationController:**
- `setState(CharacterState)` - Change character state
- `setFacingRight(boolean)` - Set character direction
- `update()` - Update animation frame (call every game tick)
- `getCurrentFrame()` - Get current frame image to render

**GamePanel:**
- `updateAnimationState()` - Determine state from player input
- Animation updates happen in main `update()` method
- Rendering in `drawWorld()` method

## Extension

To add new animations:
1. Add new state to `CharacterState` enum
2. Load corresponding images in `AnimationController.loadImages()`
3. Handle new state in `getCurrentFrame()` method
4. Update state logic in `GamePanel.updateAnimationState()`

## Performance

- Images are loaded once at initialization and cached
- Minimal overhead per frame (simple state checks)
- No dynamic image creation or transformation

## Error Handling

- Graceful fallback if images fail to load
- Warning messages logged to console
- Game continues with fallback rendering (blue rectangle) if needed
