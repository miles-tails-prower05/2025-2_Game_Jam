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
    
    // --- UI 컴포넌트 ---
    private JButton menuButton;
    private JPanel pausePanel;
    private JButton btnRestart, btnStageSelect, btnExitTitle, btnResume;
    
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

        mapManager = new MapManager();
        this.frame = frame;
        this.cards = cards;
        this.eventPanel = eventPanel;
        this.saveManager = saveManager;

        menuButton = new JButton("MENU");
        menuButton.setBounds(WINDOW_WIDTH - 100, 20, 80, 40);
        menuButton.setFocusable(false); 
        menuButton.addActionListener(e -> showPauseMenu());
        add(menuButton);

        initPausePanel();

        changeStage("스테이지 1");
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

        if (mapManager.isUnderwater()) {
            g.setColor(Color.BLACK);
            for(int x : mapManager.getBubbleSpawnersX()) {
                g.fillOval(x, 550, 30, 10);
            }
        }

        // 플랫폼
        g.setColor(new Color(180, 100, 50));
        for (Rectangle platform : mapManager.getPlatforms()) {
            if (platform.x + platform.width > cameraX && platform.x < cameraX + WINDOW_WIDTH) {
                g.fillRect(platform.x, platform.y, platform.width, platform.height);
                g.setColor(Color.BLACK);
                g.drawRect(platform.x, platform.y, platform.width, platform.height);
                g.setColor(new Color(180, 100, 50)); 
            }
        }

        // 가시
        g.setColor(Color.LIGHT_GRAY);
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
        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, playerWidth, playerHeight);
        
        if (mapManager.isUnderwater() && currentOxygen < 300 && (currentOxygen / 20) % 2 == 0) {
             g.setFont(new Font("Arial", Font.BOLD, 40));
             g.setColor(Color.RED);
             g.drawString(String.valueOf(currentOxygen/60), playerX, playerY - 20);
        }
    }

    private void drawUI(Graphics2D g) {
    	// ★ 추가: 현재 경과 시간 표시 (우측 상단)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String timeStr = formatTime(currentPlayTime);
        g.drawString("Time: " + timeStr, WINDOW_WIDTH - 200, 80);
    	
        if (isDead) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", WINDOW_WIDTH/2 - 100, WINDOW_HEIGHT/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE or W to Restart", WINDOW_WIDTH/2 - 120, WINDOW_HEIGHT/2 + 50);
            return;
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
            String clearTimeStr = "Clear Time: " + formatTime(currentPlayTime);
            g.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
            FontMetrics fm1 = g.getFontMetrics();
            int timeX = (WINDOW_WIDTH - fm1.stringWidth(clearTimeStr)) / 2;
            g.drawString(clearTimeStr, timeX, WINDOW_HEIGHT / 2 + 60); // "클리어 했습니다" 아래에 표시
            return;
        }

        int lifeIconX = 20;
        int lifeIconY = 20; 
        
        g.setColor(Color.BLUE);
        g.fillRect(lifeIconX, lifeIconY, 30, 30);
        g.setColor(Color.BLACK); 
        g.drawRect(lifeIconX, lifeIconY, 30, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("x " + (currentLives-1), lifeIconX + 40, lifeIconY + 25);
    }
    
    // ★ 추가: 시간을 "분:초.밀리초" 형식으로 변환하는 헬퍼 메서드
    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long hund = (millis / 10) % 100; // 10ms 단위
        return String.format("%02d:%02d.%02d", minutes, seconds, hund);
    }

    private void update() {
    	if (isDead) return;
        
        if (isCleared) {
            clearDelayTimer++;
            if (clearDelayTimer > 180) {
                if (isStoryMode) {
                    if ("스테이지 1".equals(currentStageName)) {
                        changeStage("스테이지 2");
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
            if (bubbleSpawnTimer > 100) {
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

        checkInteractions(); 

        cameraX = playerX - WINDOW_WIDTH / 2;
        if (cameraX < 0) cameraX = 0; 
        if (cameraX > mapManager.getLevelWidth() - WINDOW_WIDTH) 
            cameraX = mapManager.getLevelWidth() - WINDOW_WIDTH;
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
        playerX = 50;   
        playerY = 300;
        velocityX = 0;
        velocityY = 0;
        currentOxygen = maxOxygen; 
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
        
        if (activeBubbles != null) activeBubbles.clear();
        
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        jumpLocked = false;
    }
    
    public void changeStage(String stage) {
        this.currentStageName = stage; 
        mapManager.loadLevel(stage);
        
        currentLives = maxLives; 
        respawn();
        isDead = false;
        isCleared = false; 
        clearDelayTimer = 0;
        
        if (activeBubbles != null) activeBubbles.clear();

        if (mapManager.isUnderwater()) setBackground(new Color(0, 100, 0)); 
        else setBackground(new Color(135, 206, 235));
        
        currentPlayTime = 0;
    }
}