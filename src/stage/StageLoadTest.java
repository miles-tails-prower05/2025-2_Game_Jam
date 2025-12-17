package stage;

public class StageLoadTest {
    public static void main(String[] args) {
        System.out.println("=== Stage Loading Test ===\n");
        
        MapManager mapManager = new MapManager();
        String[] stages = {
            "스테이지 1", "스테이지 2", "스테이지 3", "스테이지 4",
            "스테이지 5", "스테이지 6", "스테이지 7"
        };
        
        int passedTests = 0;
        int totalTests = stages.length;
        
        for (String stage : stages) {
            System.out.println("Testing: " + stage);
            try {
                mapManager.loadLevel(stage);
                
                // Verify basic properties
                boolean hasLevelWidth = mapManager.getLevelWidth() > 0;
                boolean hasPlatforms = mapManager.getPlatforms().size() > 0;
                boolean hasGoal = mapManager.getGoalObject() != null;
                
                if (hasLevelWidth && hasPlatforms && hasGoal) {
                    System.out.println("  ✓ " + stage + " loaded successfully");
                    System.out.println("    - Level Width: " + mapManager.getLevelWidth());
                    System.out.println("    - Platforms: " + mapManager.getPlatforms().size());
                    System.out.println("    - Breakable Platforms: " + mapManager.getBreakablePlatforms().size());
                    System.out.println("    - Spikes: " + mapManager.getSpikes().size());
                    System.out.println("    - Underwater: " + mapManager.isUnderwater());
                    System.out.println("    - Goal: " + mapManager.getGoalObject());
                    passedTests++;
                } else {
                    System.out.println("  ✗ " + stage + " failed validation");
                    if (!hasLevelWidth) System.out.println("    - Missing level width");
                    if (!hasPlatforms) System.out.println("    - Missing platforms");
                    if (!hasGoal) System.out.println("    - Missing goal");
                }
            } catch (Exception e) {
                System.out.println("  ✗ " + stage + " failed to load");
                System.out.println("    - Error: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
        
        if (passedTests == totalTests) {
            System.out.println("✓ All stages loaded successfully!");
        } else {
            System.out.println("✗ Some stages failed to load");
            System.exit(1);
        }
    }
}
