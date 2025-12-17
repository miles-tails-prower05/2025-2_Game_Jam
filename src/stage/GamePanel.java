package stage;

import javax.swing.*;

import data.SaveManager;
import ui.eventScenePanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // --- 카드 레이아웃 관련 ---
    private Container frame;
    private CardLayout cards;
    private eventScenePanel eventPanel;
    
    // 현재 스테이지 이름 저장용 변수
    private String currentStageName; 
    
    // --- 스테이지 이름 애니메이션 관련 ---
    private boolean isShowingStageName = false;
    private int stageNameAnimTimer = 0;
    private final int ANIM_DURATION = 80; // 약 1.5초 (60fps 기준)
    
    // --- UI 컴포넌트 ---
    private JButton menuButton;
    private JPanel pausePanel;
    private JButton btnRestart, btnStageSelect, btnExitTitle, btnResume;
    private ImageIcon charIcon;
    private Image charIconScaled;
    
    private boolean isStoryMode = false;
    
    // --- 설정 변수 ---
    private Timer timer;
    private final int DELAY = 16;
    private final int WINDOW_WIDTH = 1280;  
    private final int WINDOW_HEIGHT = 720;
    
    // --- MapManager ---
    private MapManager mapManager;
    
    private SaveManager saveManager;

    // --- 카메라 ---
    private int cameraX = 0;

    // --- 플레이어 ---
    private int playerX = 50;
    private int playerY = 300;
    private int playerWidth = 40;
    private int playerHeight = 40;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean onGround = false; 
    
    // --- 입력 상태 ---
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false; 
    private boolean jumpLocked = false;
    
    // --- Animation Controller ---
    private AnimationController animationController;  
    
    // --- 게임 상태 ---
    private int maxOxygen = 1000;
    private int currentOxygen = maxOxygen;
    private boolean isDead = false;
    private int maxLives = 3;
    private int currentLives = 3;
    private long currentPlayTime = 0;

    // --- 클리어 상태 ---
    private boolean isCleared = false;
    private int clearDelayTimer = 0;

    // --- 동적 오브젝트 (공기 방울) ---
    private ArrayList<Bubble> activeBubbles; 
    private int bubbleSpawnTimer = 0;
    
    // --- 라이벌 캐릭터 (스테이지 6 전용) ---
    private RivalCharacter rivalCharacter;
    private boolean isRivalDefeated = false;
    private int defeatMessageTimer = 0;
    private final int DEFEAT_MESSAGE_DURATION = 120; // 2초 정도 

    class Bubble {
        int x, y, size, speed;
        public Bubble(int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.size = 20 + new Random().nextInt(15); 
            this.speed = 2 + new Random().nextInt(2);  
        }
        public void move() {
            y -= speed; 
            x += (int)(Math.sin(y * 0.05) * 2); 
        }
        public Rectangle getBounds() { return new Rectangle(x, y, size, size); }
    }

    public GamePanel(Container frame, CardLayout cards, eventScenePanel eventPanel, SaveManager saveManager) {
        setLayout(null);
        
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(new Color(0, 100, 0)); 
        setFocusable(true);
        addKeyListener(this);
        
        // 이미지 로드
        try {
            charIcon = new ImageIcon("src/stage/images/char_icon.png");
            charIconScaled = charIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.out.println("이미지 로드 실패: " + e.getMessage());
        }

        mapManager = new MapManager();
        this.frame = frame;
        this.cards = cards;
        this.eventPanel = eventPanel;
        this.saveManager = saveManager;
        
        // Initialize animation controller
        animationController = new AnimationController();

        menuButton = new JButton("MENU");
        menuButton.setBounds(WINDOW_WIDTH - 100, 20, 80, 40);
        menuButton.setFocusable(false); 
        menuButton.addActionListener(e -> showPauseMenu());
        add(menuButton);

        initPausePanel();

        changeStage("Gold Coast");
        activeBubbles = new ArrayList<>();
        
        timer = new Timer(DELAY, this);
        timer.start();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (timer != null && !timer.isRunning()) timer.start();
                pausePanel.setVisible(false); 
                menuButton.setVisible(true);
                GamePanel.this.requestFocusInWindow();
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                if (timer != null) timer.stop();
            }
        });
    }
    
    private void initPausePanel() {
        pausePanel = new JPanel();
        pausePanel.setBounds(WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 - 150, 300, 300);
        pausePanel.setBackground(new Color(0, 0, 0, 200)); 
        pausePanel.setLayout(new GridLayout(4, 1, 10, 10)); 
        pausePanel.setVisible(false); 

        Font font = new Font("Malgun Gothic", Font.BOLD, 18);

        btnResume = new JButton("계속하기");
        btnResume.setFont(font);
        btnResume.setFocusable(false);
        btnResume.addActionListener(e -> resumeGame());

        btnRestart = new JButton("스테이지 재시작");
        btnRestart.setFont(font);
        btnRestart.setFocusable(false);
        btnRestart.addActionListener(e -> {
            resumeGame();
            handleDeath(); 
        });

        btnStageSelect = new JButton("스테이지 선택");
        btnStageSelect.setFont(font);
        btnStageSelect.setFocusable(false);
        btnStageSelect.setEnabled(false); 
        btnStageSelect.addActionListener(e -> {
            cards.show(frame, "SELECT");
        });

        btnExitTitle = new JButton("타이틀로 나가기");
        btnExitTitle.setFont(font);
        btnExitTitle.setFocusable(false);
        btnExitTitle.addActionListener(e -> {
            cards.show(frame, "TITLE");
        });

        pausePanel.add(btnResume);
        pausePanel.add(btnRestart);
        pausePanel.add(btnStageSelect);
        pausePanel.add(btnExitTitle);

        add(pausePanel);
    }

    private void showPauseMenu() {
        if (timer.isRunning()) {
            timer.stop(); 
        }
        
        btnStageSelect.setEnabled(!isStoryMode);
        
        if (currentLives <= 1) {
            btnRestart.setEnabled(false);
        } else {
            btnRestart.setEnabled(true);
        }
        
        menuButton.setVisible(false);
        pausePanel.setVisible(true); 
    }

    private void resumeGame() {
        pausePanel.setVisible(false);
        menuButton.setVisible(true);
        if (!timer.isRunning()) {
            timer.start();
        }
        this.requestFocus(); 
    }

    public void setStoryMode(boolean isStoryMode) {
        this.isStoryMode = isStoryMode;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (mapManager != null && mapManager.getStageColors() != null) {
            g.setColor(mapManager.getStageColors().getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(Color.BLACK); // 기본값
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        AffineTransform originalTransform = g2d.getTransform(); 
        g2d.translate(-cameraX, 0); 
        drawWorld(g2d); 

        g2d.setTransform(originalTransform); 
        drawUI(g2d); 
    }

    private void drawWorld(Graphics2D g) {
        if (mapManager.isUnderwater()) {
             g.setColor(new Color(0, 80, 0)); 
        } else {
             g.setColor(new Color(100, 180, 210)); 
        }
        
        for(int i=0; i < mapManager.getLevelWidth(); i+=50) {
            g.drawLine(i, 0, i, WINDOW_HEIGHT);
        }

        //if (mapManager.isUnderwater()) {
            //g.setColor(Color.BLACK);
            //for(int x : mapManager.getBubbleSpawnersX()) {
                //g.fillOval(x, 550, 30, 10);
            //}
        //}

        // 플랫폼
        Color platformColor = mapManager.getStageColors().getPlatformColor();
        g.setColor(platformColor);
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (platform.x + platform.width > cameraX && platform.x < cameraX + WINDOW_WIDTH) {
                g.fillRect(platform.x, platform.y, platform.width, platform.height);
                g.setColor(Color.BLACK);
                g.drawRect(platform.x, platform.y, platform.width, platform.height);
                g.setColor(platformColor); 
            }
        }
        
        // 부서지는 플랫폼
        Color breakableColor = mapManager.getStageColors().getBreakablePlatformColor();
        for (BreakablePlatform bp : mapManager.getBreakablePlatforms()) {
            Rectangle platform = bp.getBounds();
            if (platform.x + platform.width > cameraX && platform.x < cameraX + WINDOW_WIDTH) {
                if (!bp.isBroken()) {
                    // Calculate shake effect when triggered
                    int shakeOffset = 0;
                    if (bp.isTriggered()) {
                        // Shake more as it gets closer to breaking
                        int intensity = (bp.getBreakTimer() * 2) / bp.getBreakDelay();
                        shakeOffset = (bp.getBreakTimer() % 4 < 2) ? intensity : -intensity;
                    }
                    
                    // Draw platform with stage-specific color
                    g.setColor(breakableColor);
                    g.fillRect(platform.x, platform.y + shakeOffset, platform.width, platform.height);
                    
                    // Draw cracks pattern when triggered
                    if (bp.isTriggered()) {
                        g.setColor(Color.BLACK);
                        // Draw cracks that become more visible as timer increases
                        int alpha = Math.min(255, (bp.getBreakTimer() * 255) / bp.getBreakDelay());
                        g.setColor(new Color(0, 0, 0, alpha));
                        int midX = platform.x + platform.width / 2;
                        int midY = platform.y + platform.height / 2;
                        g.drawLine(midX, platform.y + shakeOffset, midX - 10, platform.y + platform.height + shakeOffset);
                        g.drawLine(midX, platform.y + shakeOffset, midX + 10, platform.y + platform.height + shakeOffset);
                        g.drawLine(platform.x + 10, midY + shakeOffset, platform.x + platform.width - 10, midY + shakeOffset);
                    }
                    
                    g.setColor(Color.BLACK);
                    g.drawRect(platform.x, platform.y + shakeOffset, platform.width, platform.height);
                }
            }
        }

        // 스프링보드
        Color springboardColor = mapManager.getStageColors().getSpringboardColor();
        for (Springboard sb : mapManager.getSpringboards()) {
            Rectangle springboard = sb.getBounds();
            if (springboard.x + springboard.width > cameraX && springboard.x < cameraX + WINDOW_WIDTH) {
                // Calculate compression offset
                int compressionOffset = 0;
                //if (sb.isCompressed()) {
                    // Compress the springboard visually (reduce height from top)
                    //compressionOffset = (sb.getCompressionTimer() * 5) / sb.getCompressionDuration();
                //}
                
                // Draw springboard with stage-specific color
                g.setColor(springboardColor);
                g.fillRect(springboard.x, springboard.y + compressionOffset, springboard.width, springboard.height - compressionOffset);
                
                // Add spring coil pattern to make it identifiable
                g.setColor(Color.DARK_GRAY);
                int coilCount = 3;
                for (int i = 0; i < coilCount; i++) {
                    int coilY = springboard.y + compressionOffset + (springboard.height - compressionOffset) / 2;
                    int coilX = springboard.x + (i + 1) * springboard.width / (coilCount + 1);
                    g.fillOval(coilX - 3, coilY - 3, 6, 6);
                }
                
                g.setColor(Color.BLACK);
                g.drawRect(springboard.x, springboard.y + compressionOffset, springboard.width, springboard.height - compressionOffset);
            }
        }

        // 가시
        Color spikeColor = mapManager.getStageColors().getSpikeColor();
        g.setColor(spikeColor);
        for (Rectangle spike : mapManager.getSpikes()) {
            if (spike.x + spike.width > cameraX && spike.x < cameraX + WINDOW_WIDTH) {
                int[] xPoints = {spike.x, spike.x + spike.width/2, spike.x + spike.width};
                int[] yPoints = {spike.y + spike.height, spike.y, spike.y + spike.height};
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }

        // 클리어 오브젝트
        Rectangle goal = mapManager.getGoalObject();
        if (goal != null && goal.x + goal.width > cameraX && goal.x < cameraX + WINDOW_WIDTH) {
            g.setColor(new Color(255, 215, 0)); 
            g.fillOval(goal.x, goal.y, goal.width, goal.height);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawOval(goal.x, goal.y, goal.width, goal.height);
            g.setStroke(new BasicStroke(1));
        }

        // 공기 방울
        if (mapManager.isUnderwater()) {
            g.setColor(new Color(173, 216, 230, 180)); 
            for (Bubble b : activeBubbles) {
                g.fillOval(b.x, b.y, b.size, b.size);
                g.setColor(Color.WHITE);
                g.drawOval(b.x, b.y, b.size, b.size);
            }
        }

        // 플레이어
        Image currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            // Draw the character image
            int imgWidth = playerWidth;
            int imgHeight = playerHeight;
            
            // Handle horizontal flipping for direction
            if (!animationController.isFacingRight()) {
                // Flip horizontally for left-facing
                g.drawImage(currentFrame, 
                    playerX + imgWidth, playerY, 
                    playerX, playerY + imgHeight,
                    0, 0, 
                    currentFrame.getWidth(null), currentFrame.getHeight(null),
                    null);
            } else {
                // Normal rendering for right-facing
                g.drawImage(currentFrame, playerX, playerY, imgWidth, imgHeight, null);
            }
        } else {
            // Fallback to blue rectangle if image not loaded
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, playerWidth, playerHeight);
        }
        
        if (mapManager.isUnderwater() && currentOxygen < 300 && (currentOxygen / 20) % 2 == 0) {
             g.setFont(new Font("Arial", Font.BOLD, 40));
             g.setColor(Color.RED);
             g.drawString(String.valueOf(currentOxygen/60), playerX, playerY - 20);
        }
        
        // 라이벌 캐릭터 (스테이지 6에서만)
        if (rivalCharacter != null && currentStageName.equals("Rival Battle")) {
            int rivalX = rivalCharacter.getX();
            int rivalY = rivalCharacter.getY();
            int rivalWidth = rivalCharacter.getWidth();
            int rivalHeight = rivalCharacter.getHeight();
            
            // 라이벌을 빨간색 사각형으로 렌더링 (플레이어와 구분)
            g.setColor(new Color(200, 0, 0)); // 어두운 빨간색
            g.fillRect(rivalX, rivalY, rivalWidth, rivalHeight);
            g.setColor(Color.BLACK);
            g.drawRect(rivalX, rivalY, rivalWidth, rivalHeight);
            
            // 라이벌 위에 "RIVAL" 텍스트 표시
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("RIVAL", rivalX, rivalY - 5);
        }
    }

    private void drawUI(Graphics2D g) {
    	// 1. 경과 시간 표시 (좌측 상단 최상단)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String timeStr = formatTime(currentPlayTime);
        g.drawString("TIME: " + timeStr, 20, 40); // Y좌표를 40으로 설정
    	
        if (isDead) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", WINDOW_WIDTH/2 - 100, WINDOW_HEIGHT/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE or W to Restart", WINDOW_WIDTH/2 - 120, WINDOW_HEIGHT/2 + 50);
            return;
        }
        
        // 라이벌에게 패배 메시지 표시
        if (isRivalDefeated && defeatMessageTimer > 0) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            g.setColor(Color.RED);
            g.setFont(new Font("Malgun Gothic", Font.BOLD, 50));
            String msg = "라이벌에게 패배했습니다!";
            FontMetrics fm = g.getFontMetrics();
            int msgX = (WINDOW_WIDTH - fm.stringWidth(msg)) / 2;
            g.drawString(msg, msgX, WINDOW_HEIGHT / 2);
        }

        if (isCleared) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            g.setColor(Color.YELLOW);
            
            // ★ 모드에 따라 텍스트 크기와 내용 변경
            String msg;
            if (isStoryMode) {
                g.setFont(new Font("Malgun Gothic", Font.BOLD, 60));
                msg = "조각을 찾았습니다!";
            } else {
                g.setFont(new Font("Malgun Gothic", Font.BOLD, 50)); // 텍스트가 기므로 폰트 조금 줄임
                msg = "스테이지를 클리어 했습니다!";
            }

            FontMetrics fm = g.getFontMetrics();
            int msgX = (WINDOW_WIDTH - fm.stringWidth(msg)) / 2;
            int msgY = WINDOW_HEIGHT / 2;
            g.drawString(msg, msgX, msgY);

            // ★ 추가: 클리어 화면에 기록 표시
            String clearTimeStr = "TIME: " + formatTime(currentPlayTime);
            g.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
            FontMetrics fm1 = g.getFontMetrics();
            int timeX = (WINDOW_WIDTH - fm1.stringWidth(clearTimeStr)) / 2;
            g.drawString(clearTimeStr, timeX, WINDOW_HEIGHT / 2 + 60); // "클리어 했습니다" 아래에 표시
            return;
        }

        // 2. 목숨 표시 (시간 표시 아래로 이동)
        int lifeIconX = 20;
        int lifeIconY = 55; // 시간 텍스트 아래에 위치하도록 Y좌표를 55로 조정
        
        if (charIconScaled != null) {
            g.drawImage(charIconScaled, lifeIconX, lifeIconY, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("x " + (currentLives-1), lifeIconX + 40, lifeIconY + 25);
        
        // 3. 스테이지 이름 애니메이션 (기존 로직 유지)
        if (isShowingStageName) {
            int alpha = 255;
            // 끝날 때쯤 서서히 투명해지는 효과
            if (stageNameAnimTimer > ANIM_DURATION - 20) {
                alpha = Math.max(0, (ANIM_DURATION - stageNameAnimTimer) * 12);
            }

            // 1. 파란색 장식 띠 (오른쪽에서 왼쪽으로 이동)
            int targetBarX = WINDOW_WIDTH - 600;
            int barX = Math.max(targetBarX, WINDOW_WIDTH - (stageNameAnimTimer * 80));
            
            g.setColor(new Color(0, 50, 200, alpha));
            g.fillRect(barX, 250, 700, 100);

            // 2. 스테이지 이름 텍스트 (왼쪽에서 오른쪽으로 이동)
            int targetTextX = 200;
            int textX = Math.min(targetTextX, -300 + (stageNameAnimTimer * 60));

            g.setFont(new Font("Malgun Gothic", Font.ITALIC | Font.BOLD, 50));
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(currentStageName, textX, 320);
            
            // "ZONE" 텍스트 추가 (소닉 특유의 감성)
            //g.setFont(new Font("Arial", Font.BOLD, 30));
            //g.drawString("ZONE", textX + 250, 320);
        }
    }
    
    // ★ 추가: 시간을 "분:초.밀리초" 형식으로 변환하는 헬퍼 메서드
    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long hund = (millis / 10) % 100; // 10ms 단위
        return String.format("%02d:%02d.%02d", minutes, seconds, hund);
    }

    private void update() {
    	// 스테이지 이름 연출 중 로직 처리
        if (isShowingStageName) {
            stageNameAnimTimer++;
            if (stageNameAnimTimer > ANIM_DURATION) {
                isShowingStageName = false;
            }
            // 연출 중에는 플레이어의 물리 업데이트를 건너뛰어 키 입력을 무효화함
            velocityX = 0;
            velocityY = 0;
            return; 
        }
    	
    	if (isDead) return;
        
        if (isCleared) {
            clearDelayTimer++;
            if (clearDelayTimer > 180) {
                if (isStoryMode) {
                    if ("Gold Coast".equals(currentStageName)) {
                        changeStage("Emerald Sea");
                    } else if ("Emerald Sea".equals(currentStageName)) {
                        changeStage("Shattered Wreckage");
                    } else if ("Shattered Wreckage".equals(currentStageName)) {
                        changeStage("Deep Water");
                    } else if ("Deep Water".equals(currentStageName)) {
                        changeStage("Labyrinth");
                    } else if ("Labyrinth".equals(currentStageName)) {
                        changeStage("Rival Battle");
                    } else if ("Rival Battle".equals(currentStageName)) {
                        changeStage("Remote Island");
                    } else {
                        cards.show(frame, "TITLE");
                    }
                } else {
                    cards.show(frame, "SELECT");
                }
            }
            return; 
        }
        
        currentPlayTime += DELAY;

        if (mapManager.isUnderwater()) {
            currentOxygen--; 
            
            if (currentOxygen <= 0) {
                handleDeath(); 
            }

            bubbleSpawnTimer++;
            if (bubbleSpawnTimer > 60) {
                int[] spawners = mapManager.getBubbleSpawnersX();
                if (spawners != null && spawners.length > 0) {
                    int randomSpawnerIndex = new Random().nextInt(spawners.length);
                    activeBubbles.add(new Bubble(spawners[randomSpawnerIndex], 550));
                }
                bubbleSpawnTimer = 0;
            }

            Iterator<Bubble> it = activeBubbles.iterator();
            Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
            
            while(it.hasNext()) {
                Bubble b = it.next();
                b.move();
                if (playerRect.intersects(b.getBounds())) {
                    currentOxygen = maxOxygen;
                    it.remove(); 
                } else if (b.y < -50) it.remove();
            }
        }

        if (leftPressed) velocityX = -mapManager.getSpeed();
        else if (rightPressed) velocityX = mapManager.getSpeed();
        else velocityX *= mapManager.getFriction(); 

        if (jumpPressed && !jumpLocked && onGround) {
            velocityY = mapManager.getJumpStrength(); 
            onGround = false; 
            jumpLocked = true; 
        }

        playerX += velocityX;
        checkCollisionX(); 

        onGround = false; 
        velocityY += mapManager.getGravity(); 
        playerY += velocityY;
        checkCollisionY(); 
        
        // Update breakable platforms
        mapManager.updateBreakablePlatforms();
        
        // Update springboards
        mapManager.updateSpringboards();

        checkInteractions();
        
        // Update rival character (Stage 6 only)
        if (rivalCharacter != null && currentStageName.equals("Rival Battle")) {
            // Handle defeat message timer
            if (isRivalDefeated && defeatMessageTimer > 0) {
                defeatMessageTimer--;
                if (defeatMessageTimer <= 0) {
                    // Respawn after showing defeat message
                    handleDeath();
                    isRivalDefeated = false;
                }
                return; // Don't update anything while showing defeat message
            }
            
            rivalCharacter.update(mapManager);
            
            // Check if rival reached the goal
            Rectangle goal = mapManager.getGoalObject();
            if (goal != null && rivalCharacter.getBounds().intersects(goal)) {
                if (!isRivalDefeated) {
                    isRivalDefeated = true;
                    defeatMessageTimer = DEFEAT_MESSAGE_DURATION;
                }
            }
        }
        
        // Update animation state based on player movement
        updateAnimationState();
        
        // Update animation controller
        animationController.update();

        cameraX = playerX - WINDOW_WIDTH / 2;
        if (cameraX < 0) cameraX = 0; 
        if (cameraX > mapManager.getLevelWidth() - WINDOW_WIDTH) 
            cameraX = mapManager.getLevelWidth() - WINDOW_WIDTH;
    }
    
    /**
     * Updates the character animation state based on player movement and input.
     */
    private void updateAnimationState() {
        // Determine character state based on movement
        if (!onGround) {
            // Player is in the air
            animationController.setState(CharacterState.JUMPING);
        } else if (leftPressed || rightPressed) {
            // Player is moving on ground
            animationController.setState(CharacterState.WALKING);
        } else {
            // Player is idle
            animationController.setState(CharacterState.IDLE);
        }
        
        // Update facing direction based on movement
        if (leftPressed) {
            animationController.setFacingRight(false);
        } else if (rightPressed) {
            animationController.setFacingRight(true);
        }
        // If neither is pressed, keep the current direction
    }
        
    private void handleDeath() {
        currentLives--; 
        if (currentLives > 0) {
            respawn(); 
        } else {
            isDead = true; 
        }
    }

    private void respawn() {
    	playerX = mapManager.getSpawnX();   
        playerY = mapManager.getSpawnY();
        velocityX = 0;
        velocityY = 0;
        cameraX = 0; // 스테이지 시작 시 카메라 위치도 즉시 초기화
        currentOxygen = maxOxygen; 
        
        // Reset breakable platforms on respawn
        mapManager.resetBreakablePlatforms();
        
        // Reset springboards on respawn
        mapManager.resetSpringboards();
        
        // Reset rival character if in Stage 6
        if (rivalCharacter != null && currentStageName.equals("Rival Battle")) {
            rivalCharacter.reset(mapManager.getSpawnX(), mapManager.getSpawnY());
        }
    }

    private void checkCollisionX() {
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (playerRect.intersects(platform)) {
                if (velocityX > 0) playerX = platform.x - playerWidth; 
                else if (velocityX < 0) playerX = platform.x + platform.width; 
                velocityX = 0;
            }
        }
    }

    private void checkCollisionY() {
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        
        // Check regular platforms
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (playerRect.intersects(platform)) {
                if (velocityY > 0) { 
                    playerY = platform.y - playerHeight;
                    velocityY = 0;
                    onGround = true; 
                } else if (velocityY < 0) { 
                    playerY = platform.y + platform.height;
                    velocityY = 0;
                }
            }
        }
        
        // Check breakable platforms
        for (BreakablePlatform bp : mapManager.getBreakablePlatforms()) {
            if (!bp.isBroken()) {
                Rectangle platform = bp.getBounds();
                if (playerRect.intersects(platform)) {
                    if (velocityY > 0) { 
                        playerY = platform.y - playerHeight;
                        velocityY = 0;
                        onGround = true;
                        bp.trigger(); // Trigger the platform to break
                    } else if (velocityY < 0) { 
                        playerY = platform.y + platform.height;
                        velocityY = 0;
                    }
                }
            }
        }
        
        // Check springboards
        for (Springboard sb : mapManager.getSpringboards()) {
            Rectangle springboard = sb.getBounds();
            if (playerRect.intersects(springboard)) {
                if (velocityY > 0) { 
                    playerY = springboard.y - playerHeight;
                    velocityY = -sb.getPropelHeight(); // Apply upward velocity based on propel height
                    onGround = false; // Player is launched, not on ground
                    sb.trigger(); // Trigger compression animation
                } else if (velocityY < 0) {
                    playerY = springboard.y + springboard.height;
                    velocityY = 0;
                }
            }
        }
        
        if (playerY > WINDOW_HEIGHT) handleDeath(); 
    }

    private void checkInteractions() {
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        
        for (Rectangle spike : mapManager.getSpikes()) {
            if (playerRect.intersects(spike)) {
                handleDeath(); 
                return; 
            }
        }
        
        Rectangle goal = mapManager.getGoalObject();
        if (goal != null && playerRect.intersects(goal)) {
            if (!isCleared) {
                isCleared = true;
                clearDelayTimer = 0;
                
                // ★ 추가: 클리어 시 기록 저장
                // 스토리 모드가 아닐 때만 기록하거나, 둘 다 기록하거나 선택 가능. 여기선 모두 기록.
               saveManager.setBestTime(currentStageName, currentPlayTime);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) rightPressed = true;
        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_W) {
            if (isDead) resetGame();
            else jumpPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) rightPressed = false;
        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_W) {
            jumpPressed = false;
            jumpLocked = false; 
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void resetGame() {
        currentLives = maxLives; 
        respawn();               
        isDead = false;
        isCleared = false; 
        clearDelayTimer = 0;
        currentPlayTime = 0;
        
        // Reset breakable platforms
        mapManager.resetBreakablePlatforms();
        
        // Reset springboards
        mapManager.resetSpringboards();
        
        if (activeBubbles != null) activeBubbles.clear();
        
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        jumpLocked = false;
        
        // Reset animation controller
        if (animationController != null) {
            animationController.reset();
        }
    }
    
    public void changeStage(String stage) {
        this.currentStageName = stage; 
        mapManager.loadLevel(stage);
        
        // Update animation controller for stage-specific sprites
        if (animationController != null) {
            animationController.setStage(stage);
        }
        
        // Reset breakable platforms
        mapManager.resetBreakablePlatforms();
        
        // Reset springboards
        mapManager.resetSpringboards();
        
        // 애니메이션 초기화
        isShowingStageName = true;
        stageNameAnimTimer = 0;
        
        currentLives = maxLives; 
        respawn();
        isDead = false;
        isCleared = false; 
        clearDelayTimer = 0;
        
        if (activeBubbles != null) activeBubbles.clear();

        if (mapManager.isUnderwater()) setBackground(new Color(0, 100, 0)); 
        else setBackground(new Color(135, 206, 235));
        
        currentPlayTime = 0;
        
        // Initialize rival character for Stage 6
        if (stage.equals("Rival Battle")) {
            // Spawn rival at same starting position as player
            rivalCharacter = new RivalCharacter(mapManager.getSpawnX(), mapManager.getSpawnY());
            isRivalDefeated = false;
            defeatMessageTimer = 0;
        } else {
            rivalCharacter = null;
            isRivalDefeated = false;
            defeatMessageTimer = 0;
        }
    }
}