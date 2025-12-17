package stage;

/**
 * Simple test class for BreakablePlatform functionality
 * This test validates the core behavior of breakable platforms
 */
public class BreakablePlatformTest {
    
    public static void main(String[] args) {
        System.out.println("=== BreakablePlatform Tests ===\n");
        
        boolean allTestsPassed = true;
        
        allTestsPassed &= testInitialState();
        allTestsPassed &= testTrigger();
        allTestsPassed &= testBreaking();
        allTestsPassed &= testReset();
        allTestsPassed &= testBounds();
        
        System.out.println("\n=== Test Summary ===");
        if (allTestsPassed) {
            System.out.println("✓ All tests passed!");
        } else {
            System.out.println("✗ Some tests failed!");
            System.exit(1);
        }
    }
    
    private static boolean testInitialState() {
        System.out.println("Test: Initial State");
        BreakablePlatform bp = new BreakablePlatform(100, 200, 80, 20);
        
        boolean passed = !bp.isBroken() && !bp.isTriggered() && bp.getBreakTimer() == 0;
        
        if (passed) {
            System.out.println("  ✓ Platform starts in correct initial state");
        } else {
            System.out.println("  ✗ Platform initial state is incorrect");
        }
        
        return passed;
    }
    
    private static boolean testTrigger() {
        System.out.println("Test: Trigger Mechanism");
        BreakablePlatform bp = new BreakablePlatform(100, 200, 80, 20);
        
        bp.trigger();
        
        boolean passed = bp.isTriggered() && !bp.isBroken();
        
        if (passed) {
            System.out.println("  ✓ Platform triggers correctly");
        } else {
            System.out.println("  ✗ Platform trigger failed");
        }
        
        return passed;
    }
    
    private static boolean testBreaking() {
        System.out.println("Test: Breaking Behavior");
        BreakablePlatform bp = new BreakablePlatform(100, 200, 80, 20);
        
        bp.trigger();
        
        // Update until it should break
        for (int i = 0; i < bp.getBreakDelay(); i++) {
            bp.update();
        }
        
        boolean passed = bp.isBroken();
        
        if (passed) {
            System.out.println("  ✓ Platform breaks after correct delay");
        } else {
            System.out.println("  ✗ Platform did not break when expected");
        }
        
        return passed;
    }
    
    private static boolean testReset() {
        System.out.println("Test: Reset Functionality");
        BreakablePlatform bp = new BreakablePlatform(100, 200, 80, 20);
        
        bp.trigger();
        for (int i = 0; i < bp.getBreakDelay(); i++) {
            bp.update();
        }
        
        bp.reset();
        
        boolean passed = !bp.isBroken() && !bp.isTriggered() && bp.getBreakTimer() == 0;
        
        if (passed) {
            System.out.println("  ✓ Platform resets correctly");
        } else {
            System.out.println("  ✗ Platform reset failed");
        }
        
        return passed;
    }
    
    private static boolean testBounds() {
        System.out.println("Test: Bounds Consistency");
        int x = 100, y = 200, w = 80, h = 20;
        BreakablePlatform bp = new BreakablePlatform(x, y, w, h);
        
        boolean passed = bp.getBounds().x == x && 
                        bp.getBounds().y == y && 
                        bp.getBounds().width == w && 
                        bp.getBounds().height == h;
        
        if (passed) {
            System.out.println("  ✓ Platform bounds are correct");
        } else {
            System.out.println("  ✗ Platform bounds are incorrect");
        }
        
        return passed;
    }
}
