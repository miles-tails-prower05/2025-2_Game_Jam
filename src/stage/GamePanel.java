package stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    
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

    public GamePanel() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(new Color(0, 100, 0)); 
        setFocusable(true);
        addKeyListener(this);

        mapManager = new MapManager();
        
        // 초기 스테이지 설정 (1: 수중)
        changeStage(2);

        activeBubbles = new ArrayList<>();
        timer = new Timer(DELAY, this);
        timer.start();
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
        
        if (key == KeyEvent.VK_1) changeStage(1);
        if (key == KeyEvent.VK_2) changeStage(2);
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
    
    private void changeStage(int stage) {
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