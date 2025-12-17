# Breakable Platform Feature Documentation

## Overview
This document describes the breakable platform feature added to the 2D platformer game.

## Feature Description
Breakable platforms are platforms that break after a player lands on them. When triggered, the platform shakes and displays visual cracks before breaking and becoming non-solid.

## Implementation Details

### 1. BreakablePlatform Class
- **Location**: `src/stage/BreakablePlatform.java`
- **Purpose**: Manages the state and behavior of a single breakable platform
- **Key Methods**:
  - `trigger()`: Starts the breaking process
  - `update()`: Updates the platform state each frame
  - `reset()`: Resets the platform to its initial state
  - `isBroken()`: Returns whether the platform is broken
  - `isTriggered()`: Returns whether the breaking process has started

### 2. MapManager Updates
- **Location**: `src/stage/MapManager.java`
- **Changes**:
  - Added `breakablePlatforms` ArrayList to store breakable platforms
  - Added parsing for `[BREAKABLE_PLATFORMS]` section in stage data files
  - Added `getBreakablePlatforms()` getter method
  - Added `updateBreakablePlatforms()` to update all platforms
  - Added `resetBreakablePlatforms()` to reset all platforms

### 3. GamePanel Updates
- **Location**: `src/stage/GamePanel.java`
- **Changes**:
  - Added rendering logic for breakable platforms with visual effects:
    - Different color (darker brown) to distinguish from regular platforms
    - Shake effect when triggered
    - Crack pattern that becomes more visible as breaking approaches
  - Updated collision detection to handle breakable platforms
  - Trigger platforms when player lands on them
  - Skip collision with broken platforms
  - Call `mapManager.updateBreakablePlatforms()` in the game loop
  - Reset breakable platforms on respawn and stage change

### 4. Stage Data Format
- **Location**: `src/stage/stageData/*.txt`
- **Format**: Add a new section `[BREAKABLE_PLATFORMS]` with format:
  ```
  [BREAKABLE_PLATFORMS]
  # x, y, width, height
  600, 250, 80, 20
  1300, 350, 100, 20
  ```

### 5. Breaking Mechanics
- **Trigger Condition**: Player lands on platform (from above)
- **Break Delay**: 30 frames (~0.5 seconds at 60fps)
- **Visual Feedback**:
  - Platform shakes with increasing intensity
  - Crack pattern appears and becomes more visible
  - Platform disappears after breaking
- **Reset**: Platforms reset when player respawns or stage changes

## Testing
Three test classes were created to validate the implementation:

1. **BreakablePlatformTest.java**: Unit tests for the BreakablePlatform class
   - Tests initial state
   - Tests trigger mechanism
   - Tests breaking behavior
   - Tests reset functionality
   - Tests bounds consistency

2. **MapManagerTest.java**: Integration tests for MapManager
   - Tests stage loading with breakable platforms
   - Tests platform reset functionality

3. **RegressionTest.java**: Ensures no breaking changes
   - Tests regular platforms still load
   - Tests spikes still load
   - Tests goal objects still load
   - Tests physics settings preserved
   - Tests underwater settings preserved

All tests pass successfully, confirming:
- ✓ Breakable platforms work as intended
- ✓ No regressions in existing functionality
- ✓ Stage data loads correctly

## Usage Example
Add breakable platforms to any stage by adding a `[BREAKABLE_PLATFORMS]` section in the stage data file:

```
[BREAKABLE_PLATFORMS]
# x, y, width, height
600, 250, 80, 20
1300, 350, 100, 20
2000, 400, 80, 20
```

## Compatibility
- The feature is fully compatible with existing platform types
- Regular platforms, spikes, goals, and other game objects continue to work as before
- No changes required to existing stage data files (breakable platforms are optional)

## Future Enhancements (Optional)
- Adjustable break delay per platform
- Different breaking animations
- Sound effects for breaking
- Particle effects when breaking
- Platform respawn after a delay
