package stage;

/**
 * Regression test to ensure existing platform functionality still works
 */
public class RegressionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Regression Tests ===\n");
        
        boolean allTestsPassed = true;
        
        allTestsPassed &= testRegularPlatformsStillLoad();
        allTestsPassed &= testSpikesStillLoad();
        allTestsPassed &= testGoalStillLoads();
        allTestsPassed &= testPhysicsSettingsPreserved();
        allTestsPassed &= testUnderwaterSettingPreserved();
        
        System.out.println("\n=== Test Summary ===");
        if (allTestsPassed) {
            System.out.println("✓ All regression tests passed!");
            System.out.println("✓ No breaking changes detected!");
        } else {
            System.out.println("✗ Some regression tests failed!");
            System.exit(1);
        }
    }
    
    private static boolean testRegularPlatformsStillLoad() {
        System.out.println("Test: Regular Platforms Still Load");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        // Stage 1 should have multiple regular platforms
        boolean hasRegularPlatforms = mm.getPlatforms().size() >= 10;
        
        if (hasRegularPlatforms) {
            System.out.println("  ✓ Regular platforms loaded: " + mm.getPlatforms().size());
        } else {
            System.out.println("  ✗ Regular platforms not loading correctly");
        }
        
        return hasRegularPlatforms;
    }
    
    private static boolean testSpikesStillLoad() {
        System.out.println("Test: Spikes Still Load");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        // Stage 1 should have spikes
        boolean hasSpikes = mm.getSpikes().size() > 0;
        
        if (hasSpikes) {
            System.out.println("  ✓ Spikes loaded: " + mm.getSpikes().size());
        } else {
            System.out.println("  ✗ Spikes not loading correctly");
        }
        
        return hasSpikes;
    }
    
    private static boolean testGoalStillLoads() {
        System.out.println("Test: Goal Object Still Loads");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        // Stage 1 should have a goal
        boolean hasGoal = mm.getGoalObject() != null;
        
        if (hasGoal) {
            System.out.println("  ✓ Goal object loaded");
        } else {
            System.out.println("  ✗ Goal object not loading correctly");
        }
        
        return hasGoal;
    }
    
    private static boolean testPhysicsSettingsPreserved() {
        System.out.println("Test: Physics Settings Preserved");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        // Stage 1 underwater physics should be present
        boolean gravitySet = mm.getGravity() > 0;
        boolean jumpStrengthSet = mm.getJumpStrength() < 0;
        boolean speedSet = mm.getSpeed() > 0;
        boolean frictionSet = mm.getFriction() > 0;
        
        boolean passed = gravitySet && jumpStrengthSet && speedSet && frictionSet;
        
        if (passed) {
            System.out.println("  ✓ Physics settings preserved");
            System.out.println("    Gravity: " + mm.getGravity());
            System.out.println("    Jump Strength: " + mm.getJumpStrength());
            System.out.println("    Speed: " + mm.getSpeed());
            System.out.println("    Friction: " + mm.getFriction());
        } else {
            System.out.println("  ✗ Physics settings not preserved correctly");
        }
        
        return passed;
    }
    
    private static boolean testUnderwaterSettingPreserved() {
        System.out.println("Test: Underwater Setting Preserved");
        MapManager mm1 = new MapManager();
        mm1.loadLevel("스테이지 1");
        
        MapManager mm2 = new MapManager();
        mm2.loadLevel("스테이지 2");
        
        // Stage 1 is underwater, Stage 2 is not
        boolean stage1Underwater = mm1.isUnderwater();
        boolean stage2NotUnderwater = !mm2.isUnderwater();
        
        boolean passed = stage1Underwater && stage2NotUnderwater;
        
        if (passed) {
            System.out.println("  ✓ Underwater settings preserved");
            System.out.println("    Stage 1: underwater = " + stage1Underwater);
            System.out.println("    Stage 2: underwater = " + mm2.isUnderwater());
        } else {
            System.out.println("  ✗ Underwater settings not preserved correctly");
        }
        
        return passed;
    }
}
