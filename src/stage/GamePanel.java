package stage;

import javax.swing.*;
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
	private String panel;
	
	// --- UI 컴포넌트 ---
    private JButton menuButton;
    private JPanel pausePanel;
    private JButton btnRestart, btnStageSelect, btnExitTitle, btnResume;
    
    // --- 게임 모드 플래그 ---
    private boolean isStoryMode = false;
	
    // --- 설정 변수 ---
    private Timer timer;
    private final int DELAY = 16;
    private final int WINDOW_WIDTH = 1280;  
    private final int WINDOW_HEIGHT = 720;
    
    // --- MapManager ---
    private MapManager mapManager;

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

    // --- 동적 오브젝트 (공기 방울) ---
    private ArrayList<Bubble> activeBubbles; 
    private int bubbleSpawnTimer = 0; 

    // 내부 클래스: Bubble
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

    public GamePanel(Container frame, CardLayout cards, String panel) {
        // [레이아웃 변경] 절대 위치 배치를 위해 null 레이아웃 사용
        setLayout(null);
        
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(new Color(0, 100, 0)); 
        setFocusable(true);
        addKeyListener(this);

        mapManager = new MapManager();
        this.frame = frame;
        this.cards = cards;
        this.panel = panel;

        // --- 1. 메뉴 버튼 생성 (우측 상단) ---
        menuButton = new JButton("MENU");
        menuButton.setBounds(WINDOW_WIDTH - 100, 20, 80, 40);
        menuButton.setFocusable(false); // 키보드 포커스 뺏기지 않도록 설정
        menuButton.addActionListener(e -> showPauseMenu());
        add(menuButton);

        // --- 2. 일시정지 패널(팝업) 생성 ---
        initPausePanel();

        // 초기 설정
        changeStage(this.panel);
        activeBubbles = new ArrayList<>();
        
        timer = new Timer(DELAY, this);
        timer.start();

        // (기존 리스너 유지: 화면 전환 시 타이머 관리)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (timer != null && !timer.isRunning()) timer.start();
                pausePanel.setVisible(false); // 화면 돌아오면 메뉴 닫기
                menuButton.setVisible(true);
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                if (timer != null) timer.stop();
            }
        });
    }
    
    // 일시정지 메뉴 UI 초기화
    private void initPausePanel() {
        pausePanel = new JPanel();
        pausePanel.setBounds(WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 - 150, 300, 300);
        pausePanel.setBackground(new Color(0, 0, 0, 200)); // 반투명 검정 배경
        pausePanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4개의 버튼 세로 나열
        pausePanel.setVisible(false); // 처음엔 숨김

        // 버튼 스타일
        Font font = new Font("Malgun Gothic", Font.BOLD, 18);

        // 1) 게임 계속하기 (편의상 추가)
        btnResume = new JButton("계속하기");
        btnResume.setFont(font);
        btnResume.setFocusable(false);
        btnResume.addActionListener(e -> resumeGame());

        // 2) 재시작
        btnRestart = new JButton("스테이지 재시작");
        btnRestart.setFont(font);
        btnRestart.setFocusable(false);
        btnRestart.addActionListener(e -> {
            resumeGame(); // 패널 닫기
            resetGame();  // 게임 리셋
        });

        // 3) 스테이지 선택 (스토리 모드일 때만 비활성화)
        btnStageSelect = new JButton("스테이지 선택");
        btnStageSelect.setFont(font);
        btnStageSelect.setFocusable(false);
        btnStageSelect.setEnabled(false); // 기본 비활성화
        btnStageSelect.addActionListener(e -> {
            cards.show(frame, "SELECT");
        });

        // 4) 타이틀로 나가기
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

 // 메뉴 열기
    private void showPauseMenu() {
        if (timer.isRunning()) {
            timer.stop(); // 게임 일시정지
        }
        
        // [수정] 스토리 모드가 '아닐 때'만 스테이지 선택 가능
        // (스토리 모드는 순서대로 진행해야 하므로 선택 불가)
        btnStageSelect.setEnabled(!isStoryMode);
        
        menuButton.setVisible(false); // 메뉴 버튼 숨김
        pausePanel.setVisible(true);  // 일시정지 창 표시
    }

    // 메뉴 닫기 (게임 재개)
    private void resumeGame() {
        pausePanel.setVisible(false);
        menuButton.setVisible(true);
        if (!timer.isRunning()) {
            timer.start();
        }
        this.requestFocus(); // 키보드 제어권을 다시 가져옴 (중요)
    }

    // 모드 설정 (외부에서 호출)
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
        // 배경 설정
        if (mapManager.isUnderwater()) {
             g.setColor(new Color(0, 80, 0)); 
        } else {
             g.setColor(new Color(100, 180, 210)); 
        }
        
        for(int i=0; i < mapManager.getLevelWidth(); i+=50) {
            g.drawLine(i, 0, i, WINDOW_HEIGHT);
        }

        // 스포너 (물 속일 때만)
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
        
        // 카운트다운
        if (mapManager.isUnderwater() && currentOxygen < 300 && (currentOxygen / 20) % 2 == 0) {
             g.setFont(new Font("Arial", Font.BOLD, 40));
             g.setColor(Color.RED);
             g.drawString(String.valueOf(currentOxygen/60), playerX, playerY - 20);
        }
    }

    private void drawUI(Graphics2D g) {
        if (isDead) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", WINDOW_WIDTH/2 - 100, WINDOW_HEIGHT/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE or W to Restart", WINDOW_WIDTH/2 - 120, WINDOW_HEIGHT/2 + 50);
            return;
        }

        if (mapManager.isUnderwater()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString("AIR", 20, 30);
            
            if (currentOxygen < 300) g.setColor(Color.RED); 
            else g.setColor(Color.CYAN);
            
            g.fillRect(50, 15, currentOxygen / 5, 20); 
            g.setColor(Color.WHITE);
            g.drawRect(50, 15, maxOxygen / 5, 20); 
        } else {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("STAGE MODE", 20, 30);
        }
    }

    private void update() {
        if (isDead) return;
        
        if (mapManager.isUnderwater()) {
            currentOxygen--;
            if (currentOxygen <= 0) isDead = true;

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

        // 물리 엔진
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
        if (playerY > WINDOW_HEIGHT) isDead = true; 
    }

    private void checkInteractions() {
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        for (Rectangle spike : mapManager.getSpikes()) {
            if (playerRect.intersects(spike)) isDead = true; 
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
        // 필수 구현 메서드 (비워둠)
    }

    private void resetGame() {
        playerX = 50;
        playerY = 300;
        velocityX = 0;
        velocityY = 0;
        currentOxygen = maxOxygen;
        isDead = false;
        if (activeBubbles != null) activeBubbles.clear();
        
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        jumpLocked = false;
    }
    
    public void changeStage(String stage) {
        mapManager.loadLevel(stage);
        
        // 스테이지 바뀔 때 리셋
        playerX = 50;
        playerY = 300;
        velocityX = 0;
        velocityY = 0;
        currentOxygen = maxOxygen;
        isDead = false;
        if (activeBubbles != null) activeBubbles.clear();

        if (mapManager.isUnderwater()) setBackground(new Color(0, 100, 0)); 
        else setBackground(new Color(135, 206, 235));
    }
}