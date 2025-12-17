package stage;

import java.awt.Rectangle;
import java.util.ArrayList;

public class MapManager {
    // 맵 구조 데이터
    private ArrayList<Rectangle> platforms;
    private ArrayList<Rectangle> spikes;
    private int[] bubbleSpawnersX;
    private int levelWidth;
    
    // ★ 클리어 오브젝트 (조개 조각)
    private Rectangle goalObject;

    // ★ 물리 엔진 설정값
    private double gravity;
    private double jumpStrength;
    private double speed;
    private double friction;
    
    // ★ 환경 설정
    private boolean isUnderwater;

    public MapManager() {
        platforms = new ArrayList<>();
        spikes = new ArrayList<>();
    }

    public void loadLevel(String stage) {
        platforms.clear();
        spikes.clear();
        goalObject = null; // 초기화
        
        if (stage == "스테이지 1") {
            initStage1_Labyrinth();
        } else if (stage == "스테이지 2") {
            initStage2_GreenHill();
        }
    }

    // 스테이지 1: 레버린스 존
    private void initStage1_Labyrinth() {
        levelWidth = 3000;
        
        gravity = 0.3;
        jumpStrength = -9.0;
        speed = 5.0;
        friction = 0.95;
        isUnderwater = true;

        platforms.add(new Rectangle(0, 550, levelWidth, 200)); 
        platforms.add(new Rectangle(-50, 0, 50, 720)); 
        platforms.add(new Rectangle(levelWidth, 0, 50, 720)); 

        platforms.add(new Rectangle(200, 450, 100, 20)); 
        platforms.add(new Rectangle(400, 350, 100, 20)); 
        platforms.add(new Rectangle(700, 300, 100, 20));
        platforms.add(new Rectangle(900, 400, 100, 20));
        platforms.add(new Rectangle(1100, 250, 150, 20));
        platforms.add(new Rectangle(1500, 400, 80, 20));
        platforms.add(new Rectangle(2300, 450, 200, 20)); 

        spikes.add(new Rectangle(300, 520, 100, 30)); 
        spikes.add(new Rectangle(1000, 520, 100, 30)); 
        spikes.add(new Rectangle(1600, 520, 300, 30)); 

        bubbleSpawnersX = new int[]{250, 650, 1200, 1800, 2400};
        
        // ★ 조개 조각 배치 (스테이지 끝부분)
        goalObject = new Rectangle(2850, 500, 40, 40);
    }

    // 스테이지 2: 그린 힐
    private void initStage2_GreenHill() {
        levelWidth = 2000;

        gravity = 0.6;
        jumpStrength = -12.0;
        speed = 8.0;
        friction = 0.8;
        isUnderwater = false;

        platforms.add(new Rectangle(0, 600, levelWidth, 200));
        platforms.add(new Rectangle(-50, 0, 50, 720));
        platforms.add(new Rectangle(levelWidth, 0, 50, 720));
        
        platforms.add(new Rectangle(300, 500, 100, 20));
        platforms.add(new Rectangle(500, 400, 100, 20));
        platforms.add(new Rectangle(700, 300, 100, 20));
        
        spikes.add(new Rectangle(600, 570, 100, 30));

        bubbleSpawnersX = new int[]{}; 
        
        // ★ 조개 조각 배치
        goalObject = new Rectangle(1900, 550, 40, 40);
    }

    // --- Getters ---
    public ArrayList<Rectangle> getPlatforms() { return platforms; }
    public ArrayList<Rectangle> getSpikes() { return spikes; }
    public Rectangle getGoalObject() { return goalObject; } // 추가됨
    public int[] getBubbleSpawnersX() { return bubbleSpawnersX; }
    public int getLevelWidth() { return levelWidth; }
    
    public double getGravity() { return gravity; }
    public double getJumpStrength() { return jumpStrength; }
    public double getSpeed() { return speed; }
    public double getFriction() { return friction; }
    public boolean isUnderwater() { return isUnderwater; }
}