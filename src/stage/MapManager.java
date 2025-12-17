package stage;

import java.awt.Rectangle;
import java.util.ArrayList;

public class MapManager {
    // 맵 구조 데이터
    private ArrayList<Rectangle> platforms;
    private ArrayList<Rectangle> spikes;
    private int[] bubbleSpawnersX;
    private int levelWidth;

    // ★ 물리 엔진 설정값 (상수가 아닌 변수로 변경)
    private double gravity;
    private double jumpStrength;
    private double speed;
    private double friction;
    
    // ★ 환경 설정
    private boolean isUnderwater; // 물 속인지 여부 (산소 시스템 작동 여부 결정)

    public MapManager() {
        platforms = new ArrayList<>();
        spikes = new ArrayList<>();
    }

    public void loadLevel(int stage) {
        platforms.clear();
        spikes.clear();
        
        // 스테이지별 분기
        if (stage == 1) {
            initStage1_Labyrinth(); // 수중 스테이지
        } else if (stage == 2) {
            initStage2_GreenHill(); // 지상 스테이지 (예시)
        }
    }

    // 스테이지 1: 레버린스 존 (수중 물리)
    private void initStage1_Labyrinth() {
        levelWidth = 3000;
        
        // ★ 수중 물리 값 설정
        gravity = 0.3;          // 둥둥 뜨는 느낌
        jumpStrength = -9.0;    // 낮은 점프
        speed = 5.0;            // 물의 저항
        friction = 0.95;        // 잘 미끄러짐
        isUnderwater = true;    // 산소 시스템 ON

        // 맵 배치 (기존과 동일)
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
    }

    // 스테이지 2: 그린 힐 (지상 물리 예시)
    private void initStage2_GreenHill() {
        levelWidth = 2000;

        // ★ 지상 물리 값 설정 (빠르고 묵직함)
        gravity = 0.6;          // 묵직한 중력
        jumpStrength = -12.0;   // 높고 빠른 점프
        speed = 8.0;            // 빠른 이동 속도
        friction = 0.8;         // 덜 미끄러짐 (멈출 때 팍 멈춤)
        isUnderwater = false;   // 산소 시스템 OFF

        // 지상 맵 배치 (간단하게)
        platforms.add(new Rectangle(0, 600, levelWidth, 200)); // 바닥이 좀 더 낮음
        platforms.add(new Rectangle(-50, 0, 50, 720));
        platforms.add(new Rectangle(levelWidth, 0, 50, 720));
        
        platforms.add(new Rectangle(300, 500, 100, 20));
        platforms.add(new Rectangle(500, 400, 100, 20));
        platforms.add(new Rectangle(700, 300, 100, 20));
        
        spikes.add(new Rectangle(600, 570, 100, 30));

        // 지상이라 공기방울 필요 없음
        bubbleSpawnersX = new int[]{}; 
    }

    // --- Getters ---
    public ArrayList<Rectangle> getPlatforms() { return platforms; }
    public ArrayList<Rectangle> getSpikes() { return spikes; }
    public int[] getBubbleSpawnersX() { return bubbleSpawnersX; }
    public int getLevelWidth() { return levelWidth; }
    
    // 물리 값 Getter 추가
    public double getGravity() { return gravity; }
    public double getJumpStrength() { return jumpStrength; }
    public double getSpeed() { return speed; }
    public double getFriction() { return friction; }
    public boolean isUnderwater() { return isUnderwater; }
}