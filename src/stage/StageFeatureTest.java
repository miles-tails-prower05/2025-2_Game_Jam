package stage;

public class StageFeatureTest {
    public static void main(String[] args) {
        System.out.println("=== Stage Feature Validation Test ===\n");
        
        MapManager mapManager = new MapManager();
        int passedTests = 0;
        int totalTests = 7;
        
        // Stage 1: 모래사장 - Underwater environment (기본 스테이지)
        System.out.println("Test: Stage 1 (모래사장 - Underwater Beach)");
        mapManager.loadLevel("스테이지 1");
        if (mapManager.isUnderwater() == true && 
            mapManager.getLevelWidth() == 3000 &&
            mapManager.getPlatforms().size() >= 10) {
            System.out.println("  ✓ Stage 1 - Underwater beach map validated");
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 1 validation failed");
        }
        System.out.println();
        
        // Stage 2: 얕은 바다 - Ground level with spikes
        System.out.println("Test: Stage 2 (얕은 바다 - Ground Level with Spikes)");
        mapManager.loadLevel("스테이지 2");
        if (mapManager.isUnderwater() == false && 
            mapManager.getSpikes().size() >= 1 &&
            mapManager.getLevelWidth() == 2000) {
            System.out.println("  ✓ Stage 2 - Ground level with spikes validated");
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 2 validation failed");
        }
        System.out.println();
        
        // Stage 3: 난파선 - 밟으면 부서지는 바닥이 존재
        System.out.println("Test: Stage 3 (난파선 - Shipwreck with Breakable Platforms)");
        mapManager.loadLevel("스테이지 3");
        if (mapManager.isUnderwater() == true &&
            mapManager.getBreakablePlatforms().size() >= 10 &&
            mapManager.getLevelWidth() == 3500) {
            System.out.println("  ✓ Stage 3 - Shipwreck with " + mapManager.getBreakablePlatforms().size() + " breakable platforms");
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 3 validation failed");
        }
        System.out.println();
        
        // Stage 4: 심해 - 밑이 안 보이는 구덩이가 존재
        System.out.println("Test: Stage 4 (심해 - Deep Sea with Pits)");
        mapManager.loadLevel("스테이지 4");
        if (mapManager.isUnderwater() == true &&
            mapManager.getLevelWidth() == 4000 &&
            mapManager.getGravity() < 0.3 && // Lower gravity for deep sea
            mapManager.getPlatforms().size() >= 30) {
            System.out.println("  ✓ Stage 4 - Deep sea environment validated");
            System.out.println("    - Lower gravity: " + mapManager.getGravity());
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 4 validation failed");
        }
        System.out.println();
        
        // Stage 5: 수중 미로 - 길이 복잡함
        System.out.println("Test: Stage 5 (수중 미로 - Underwater Maze)");
        mapManager.loadLevel("스테이지 5");
        if (mapManager.isUnderwater() == true &&
            mapManager.getLevelWidth() == 4500 &&
            mapManager.getPlatforms().size() >= 50) { // Complex maze structure
            System.out.println("  ✓ Stage 5 - Complex maze with " + mapManager.getPlatforms().size() + " platforms");
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 5 validation failed");
        }
        System.out.println();
        
        // Stage 6: 평행 세계의 '나'와의 수상 스키 레이싱
        System.out.println("Test: Stage 6 (평행 세계 - Racing Stage)");
        mapManager.loadLevel("스테이지 6");
        if (mapManager.isUnderwater() == false && // Water ski racing is above water
            mapManager.getLevelWidth() == 5000 &&
            mapManager.getSpeed() >= 9.0) { // High speed for racing
            System.out.println("  ✓ Stage 6 - Racing track validated");
            System.out.println("    - High speed setting: " + mapManager.getSpeed());
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 6 validation failed");
        }
        System.out.println();
        
        // Stage 7: 외딴 섬 - 엔딩 스테이지
        System.out.println("Test: Stage 7 (외딴 섬 - Remote Island Ending)");
        mapManager.loadLevel("스테이지 7");
        if (mapManager.isUnderwater() == false && // Island is above water
            mapManager.getLevelWidth() == 3000 &&
            mapManager.getGoalObject() != null &&
            mapManager.getPlatforms().size() >= 20) {
            System.out.println("  ✓ Stage 7 - Ending island stage validated");
            System.out.println("    - Goal position: " + mapManager.getGoalObject());
            passedTests++;
        } else {
            System.out.println("  ✗ Stage 7 validation failed");
        }
        System.out.println();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Feature Tests Passed: " + passedTests + "/" + totalTests);
        
        if (passedTests == totalTests) {
            System.out.println("✓ All stage features validated successfully!");
            System.out.println("\n=== Stage Summary ===");
            System.out.println("Stage 1: Sandy Beach (Underwater) - Basic starting stage");
            System.out.println("Stage 2: Shallow Sea (Ground Level) - With spike hazards");
            System.out.println("Stage 3: Shipwreck (Underwater) - 11 breakable platforms");
            System.out.println("Stage 4: Deep Sea (Underwater) - Dark environment with lower gravity");
            System.out.println("Stage 5: Underwater Maze (Underwater) - Complex maze with 53+ platforms");
            System.out.println("Stage 6: Parallel World (Ground) - High-speed racing track");
            System.out.println("Stage 7: Remote Island (Ground) - Final exploration stage");
        } else {
            System.out.println("✗ Some stage features failed validation");
            System.exit(1);
        }
    }
}
