package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import stage.GamePanel;

public class titlePanel extends JPanel {
    private Image backgroundImage; // 배경 이미지 변수
    private JButton startButton;
    private JButton storyButton;
    private JButton stageSelectButton;
    private CardLayout cards;
    private Container frame;
    private GamePanel gamePanel;

    public titlePanel(Container frame, CardLayout cards, GamePanel gamePanel) {
        this.frame = frame;
        this.cards = cards;
        this.gamePanel = gamePanel;
        
        // 화면 크기 고정 (GamePanel과 동일하게)
        setPreferredSize(new Dimension(1280, 720));
        setLayout(null); // 자유로운 배치를 위해 null 레이아웃 사용
        setBackground(Color.BLACK); // 이미지가 없을 경우를 대비한 배경색

        // 배경 이미지 로드 (프로젝트 폴더에 'title_bg.png'가 있다면 사용)
        // backgroundImage = new ImageIcon("title_bg.png").getImage();

        // 1. 게임 시작 버튼
        startButton = new JButton("게임 시작");
        startButton.setBounds(540, 500, 200, 50); // 위치 지정 (x, y, width, height)
        startButton.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        
        // 2. 하위 버튼들 (초기에는 숨김)
        storyButton = new JButton("스토리 진행");
        storyButton.setBounds(430, 500, 200, 50);
        storyButton.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        storyButton.setVisible(false);

        stageSelectButton = new JButton("스테이지 선택");
        stageSelectButton.setBounds(650, 500, 200, 50);
        stageSelectButton.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        stageSelectButton.setVisible(false);

        // --- 이벤트 리스너 ---
        
        // '게임 시작' 클릭 시 -> 하위 버튼 2개 보여주기
        startButton.addActionListener(e -> {
            startButton.setVisible(false);
            storyButton.setVisible(true);
            stageSelectButton.setVisible(true);
            repaint();
        });

        // '스토리 진행' 클릭 시 -> 바로 1스테이지 시작
        storyButton.addActionListener(e -> {
        	gamePanel.setStoryMode(true);
            gamePanel.changeStage("스테이지 1");
            cards.show(frame, "GAME");
            gamePanel.requestFocus(); // 키 입력을 받기 위해 포커스 요청 필수
        });

        // '스테이지 선택' 클릭 시 -> 선택 화면으로 이동
        stageSelectButton.addActionListener(e -> {
        	gamePanel.setStoryMode(false);
            cards.show(frame, "SELECT");
        });

        add(startButton);
        add(storyButton);
        add(stageSelectButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경 그리기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 이미지가 없으면 텍스트로 타이틀 표시
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("Game Title", 480, 200);
        }
    }
}