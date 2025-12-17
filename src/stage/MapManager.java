package stage;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MapManager {
    // 맵 구조 데이터
    private ArrayList<Rectangle> platforms;
    private ArrayList<Rectangle> spikes;
    private ArrayList<BreakablePlatform> breakablePlatforms;
    private ArrayList<Springboard> springboards;
    private int[] bubbleSpawnersX;
    private int levelWidth;
    
    private int spawnX;
    private int spawnY;
    
    // ★ 클리어 오브젝트
    private Rectangle goalObject;

    // ★ 물리 엔진 설정값
    private double gravity;
    private double jumpStrength;
    private double speed;
    private double friction;
    
    // ★ 환경 설정
    private boolean isUnderwater;
    
    // ★ 색상 설정
    private StageColors stageColors;

    public MapManager() {
        platforms = new ArrayList<>();
        spikes = new ArrayList<>();
        breakablePlatforms = new ArrayList<>();
        springboards = new ArrayList<>();
        stageColors = new StageColors();
    }

    // 스테이지 이름을 받아 해당 파일을 로드
    public void loadLevel(String stageName) {
        platforms.clear();
        spikes.clear();
        breakablePlatforms.clear();
        springboards.clear();
        goalObject = null;
        bubbleSpawnersX = new int[0]; // 초기화
        stageColors = new StageColors(); // 색상 초기화

        String fileName = "";
        
        // 스테이지 이름에 따른 파일명 매핑
        if (stageName.equals("Gold Coast")) {
            fileName = "stage1.txt";
        } else if (stageName.equals("Emerald Sea")) {
            fileName = "stage2.txt";
        } else if (stageName.equals("스테이지 3")) {
            fileName = "stage3.txt";
        } else if (stageName.equals("스테이지 4")) {
            fileName = "stage4.txt";
        } else if (stageName.equals("스테이지 5")) {
            fileName = "stage5.txt";
        } else if (stageName.equals("스테이지 6")) {
            fileName = "stage6.txt";
        } else if (stageName.equals("스테이지 7")) {
            fileName = "stage7.txt";
        } else {
            System.out.println("알 수 없는 스테이지: " + stageName);
            return;
        }

        parseStageFile(fileName);
    }

    private void parseStageFile(String fileName) {
        ArrayList<Integer> bubbleList = new ArrayList<>();
        
        try {
            // src/stage/stageData/ 폴더에서 파일 읽기
            InputStream is = getClass().getResourceAsStream("/stage/stageData/" + fileName);
            if (is == null) {
                System.out.println("스테이지 파일을 찾을 수 없습니다: " + fileName);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            String currentSection = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // 공백이나 주석 무시

                // 섹션 헤더 파싱 (예: [MAP_INFO])
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    continue;
                }

                // 섹션별 데이터 처리
                switch (currentSection) {
                    case "MAP_INFO":
                        parseMapInfo(line);
                        break;
                    case "PHYSICS":
                        parsePhysics(line);
                        break;
                    case "COLORS":
                        parseColors(line);
                        break;
                    case "PLATFORMS":
                        platforms.add(parseRectangle(line));
                        break;
                    case "BREAKABLE_PLATFORMS":
                        Rectangle rect = parseRectangle(line);
                        breakablePlatforms.add(new BreakablePlatform(rect.x, rect.y, rect.width, rect.height));
                        break;
                    case "SPRINGBOARDS":
                        parseSpringboard(line);
                        break;
                    case "SPIKES":
                        spikes.add(parseRectangle(line));
                        break;
                    case "BUBBLE_SPAWNERS":
                        // 쉼표로 구분된 x좌표들 파싱
                        String[] parts = line.split(",");
                        for (String s : parts) {
                            bubbleList.add(Integer.parseInt(s.trim()));
                        }
                        break;
                    case "GOAL":
                        goalObject = parseRectangle(line);
                        break;
                }
            }
            br.close();
            
            // 버블 스포너 리스트를 배열로 변환
            bubbleSpawnersX = bubbleList.stream().mapToInt(i -> i).toArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // "key=value" 형태 파싱
    private void parseMapInfo(String line) {
        String[] parts = line.split("=");
        if (parts.length < 2) return;
        String key = parts[0].trim();
        String value = parts[1].trim();

        if (key.equals("levelWidth")) levelWidth = Integer.parseInt(value);
        else if (key.equals("isUnderwater")) isUnderwater = Boolean.parseBoolean(value);
        else if (key.equals("spawnX")) spawnX = Integer.parseInt(value);
        else if (key.equals("spawnY")) spawnY = Integer.parseInt(value);
    }

    private void parsePhysics(String line) {
        String[] parts = line.split("=");
        if (parts.length < 2) return;
        String key = parts[0].trim();
        String value = parts[1].trim();

        if (key.equals("gravity")) gravity = Double.parseDouble(value);
        else if (key.equals("jumpStrength")) jumpStrength = Double.parseDouble(value);
        else if (key.equals("speed")) speed = Double.parseDouble(value);
        else if (key.equals("friction")) friction = Double.parseDouble(value);
    }
    
    private void parseColors(String line) {
        String[] parts = line.split("=");
        if (parts.length < 2) return;
        String key = parts[0].trim();
        String value = parts[1].trim();
        
        if (key.equals("platformColor")) stageColors.setPlatformColor(value);
        else if (key.equals("spikeColor")) stageColors.setSpikeColor(value);
        else if (key.equals("breakablePlatformColor")) stageColors.setBreakablePlatformColor(value);
        else if (key.equals("springboardColor")) stageColors.setSpringboardColor(value);
    }

    // "x, y, w, h" 문자열을 Rectangle로 변환
    private Rectangle parseRectangle(String line) {
        String[] parts = line.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        int w = Integer.parseInt(parts[2].trim());
        int h = Integer.parseInt(parts[3].trim());
        return new Rectangle(x, y, w, h);
    }

    // "x, y, w, h, propelHeight" 문자열을 Springboard로 변환
    private void parseSpringboard(String line) {
        String[] parts = line.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        int w = Integer.parseInt(parts[2].trim());
        int h = Integer.parseInt(parts[3].trim());
        double propelHeight = Double.parseDouble(parts[4].trim());
        springboards.add(new Springboard(x, y, w, h, propelHeight));
    }

    // --- Getters ---
    public ArrayList<Rectangle> getPlatforms() { return platforms; }
    public ArrayList<Rectangle> getSpikes() { return spikes; }
    public ArrayList<BreakablePlatform> getBreakablePlatforms() { return breakablePlatforms; }
    public ArrayList<Springboard> getSpringboards() { return springboards; }
    public Rectangle getGoalObject() { return goalObject; }
    public int[] getBubbleSpawnersX() { return bubbleSpawnersX; }
    public int getLevelWidth() { return levelWidth; }
    
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    
    public double getGravity() { return gravity; }
    public double getJumpStrength() { return jumpStrength; }
    public double getSpeed() { return speed; }
    public double getFriction() { return friction; }
    public boolean isUnderwater() { return isUnderwater; }
    public StageColors getStageColors() { return stageColors; }
    
    /**
     * Updates all breakable platforms
     */
    public void updateBreakablePlatforms() {
        for (BreakablePlatform bp : breakablePlatforms) {
            bp.update();
        }
    }
    
    /**
     * Resets all breakable platforms to their initial state
     */
    public void resetBreakablePlatforms() {
        for (BreakablePlatform bp : breakablePlatforms) {
            bp.reset();
        }
    }
    
    /**
     * Updates all springboards
     */
    public void updateSpringboards() {
        for (Springboard sb : springboards) {
            sb.update();
        }
    }
    
    /**
     * Resets all springboards to their initial state
     */
    public void resetSpringboards() {
        for (Springboard sb : springboards) {
            sb.reset();
        }
    }
}