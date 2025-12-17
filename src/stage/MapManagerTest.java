package stage;

/**
 * Integration test for MapManager breakable platform loading
 */
public class MapManagerTest {
    
    public static void main(String[] args) {
        System.out.println("=== MapManager Integration Tests ===\n");
        
        boolean allTestsPassed = true;
        
        allTestsPassed &= testStage1Loading();
        allTestsPassed &= testStage2Loading();
        allTestsPassed &= testBreakablePlatformReset();
        
        System.out.println("\n=== Test Summary ===");
        if (allTestsPassed) {
            System.out.println("✓ All tests passed!");
        } else {
            System.out.println("✗ Some tests failed!");
            System.exit(1);
        }
    }
    
    private static boolean testStage1Loading() {
        System.out.println("Test: Stage 1 Loading");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        int breakableCount = mm.getBreakablePlatforms().size();
        boolean hasBreakable = breakableCount > 0;
        boolean hasRegularPlatforms = mm.getPlatforms().size() > 0;
        
        boolean passed = hasBreakable && hasRegularPlatforms;
        
        if (passed) {
            System.out.println("  ✓ Stage 1 loaded with " + breakableCount + " breakable platforms");
        } else {
            System.out.println("  ✗ Stage 1 loading failed");
        }
        
        return passed;
    }
    
    private static boolean testStage2Loading() {
        System.out.println("Test: Stage 2 Loading");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 2");
        
        int breakableCount = mm.getBreakablePlatforms().size();
        boolean hasBreakable = breakableCount > 0;
        boolean hasRegularPlatforms = mm.getPlatforms().size() > 0;
        
        boolean passed = hasBreakable && hasRegularPlatforms;
        
        if (passed) {
            System.out.println("  ✓ Stage 2 loaded with " + breakableCount + " breakable platforms");
        } else {
            System.out.println("  ✗ Stage 2 loading failed");
        }
        
        return passed;
    }
    
    private static boolean testBreakablePlatformReset() {
        System.out.println("Test: Breakable Platform Reset");
        MapManager mm = new MapManager();
        mm.loadLevel("스테이지 1");
        
        // Trigger and break all platforms
        for (BreakablePlatform bp : mm.getBreakablePlatforms()) {
            bp.trigger();
            for (int i = 0; i < bp.getBreakDelay(); i++) {
                bp.update();
            }
        }
        
        // Verify all are broken
        boolean allBroken = true;
        for (BreakablePlatform bp : mm.getBreakablePlatforms()) {
            if (!bp.isBroken()) {
                allBroken = false;
                break;
            }
        }
        
        // Reset all
        mm.resetBreakablePlatforms();
        
        // Verify all are reset
        boolean allReset = true;
        for (BreakablePlatform bp : mm.getBreakablePlatforms()) {
            if (bp.isBroken() || bp.isTriggered()) {
                allReset = false;
                break;
            }
        }
        
        boolean passed = allBroken && allReset;
        
        if (passed) {
            System.out.println("  ✓ Breakable platforms reset correctly");
        } else {
            System.out.println("  ✗ Breakable platform reset failed");
        }
        
        return passed;
    }
}
