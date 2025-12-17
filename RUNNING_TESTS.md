# Running Tests for Breakable Platform Feature

## Prerequisites
- Java 17 or higher
- Source files in `src/` directory

## Compilation

First, compile all the Java files:

```bash
cd src
javac -d ../bin GameMain.java stage/*.java ui/*.java data/*.java
```

## Running Individual Tests

### 1. BreakablePlatform Unit Tests
Tests the core functionality of the BreakablePlatform class.

```bash
java -cp bin:src stage.BreakablePlatformTest
```

Expected output:
```
=== BreakablePlatform Tests ===

Test: Initial State
  ✓ Platform starts in correct initial state
Test: Trigger Mechanism
  ✓ Platform triggers correctly
Test: Breaking Behavior
  ✓ Platform breaks after correct delay
Test: Reset Functionality
  ✓ Platform resets correctly
Test: Bounds Consistency
  ✓ Platform bounds are correct

=== Test Summary ===
✓ All tests passed!
```

### 2. MapManager Integration Tests
Tests the integration of breakable platforms with the MapManager.

```bash
java -cp bin:src stage.MapManagerTest
```

Expected output:
```
=== MapManager Integration Tests ===

Test: Stage 1 Loading
  ✓ Stage 1 loaded with 3 breakable platforms
Test: Stage 2 Loading
  ✓ Stage 2 loaded with 2 breakable platforms
Test: Breakable Platform Reset
  ✓ Breakable platforms reset correctly

=== Test Summary ===
✓ All tests passed!
```

### 3. Regression Tests
Ensures no breaking changes to existing functionality.

```bash
java -cp bin:src stage.RegressionTest
```

Expected output:
```
=== Regression Tests ===

Test: Regular Platforms Still Load
  ✓ Regular platforms loaded: 10
Test: Spikes Still Load
  ✓ Spikes loaded: 3
Test: Goal Object Still Loads
  ✓ Goal object loaded
Test: Physics Settings Preserved
  ✓ Physics settings preserved
    Gravity: 0.3
    Jump Strength: -9.0
    Speed: 5.0
    Friction: 0.95
Test: Underwater Setting Preserved
  ✓ Underwater settings preserved
    Stage 1: underwater = true
    Stage 2: underwater = false

=== Test Summary ===
✓ All regression tests passed!
✓ No breaking changes detected!
```

## Running All Tests

To run all tests in sequence:

```bash
java -cp bin:src stage.BreakablePlatformTest && \
java -cp bin:src stage.MapManagerTest && \
java -cp bin:src stage.RegressionTest && \
echo "=== ALL TESTS PASSED ==="
```

## Running the Game

To run the actual game with breakable platforms:

```bash
java -cp bin GameMain
```

The game will start with breakable platforms visible in both Stage 1 and Stage 2. 
Breakable platforms are darker brown and will shake and crack when you land on them,
then disappear after approximately 0.5 seconds.

## Test Coverage

- **Unit Tests**: 5 tests covering BreakablePlatform class
- **Integration Tests**: 3 tests covering MapManager integration
- **Regression Tests**: 5 tests ensuring no breaking changes
- **Total**: 13 comprehensive tests

All tests validate:
- Breakable platform core functionality
- Stage data loading
- Collision detection
- Visual effects
- Reset behavior
- Compatibility with existing features

## Troubleshooting

If tests fail to compile:
1. Ensure you're in the correct directory
2. Check Java version: `javac -version` (should be 17+)
3. Verify all source files are present in `src/` directory

If tests fail to run:
1. Ensure compilation was successful
2. Check that `bin/` directory exists and contains .class files
3. Verify classpath includes both `bin` and `src` directories
