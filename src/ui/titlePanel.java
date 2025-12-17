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
    private eventScenePanel eventPanel;

    public titlePanel(Container frame, CardLayout cards, GamePanel gamePanel, eventScenePanel eventPanel) {
        this.frame = frame;
        this.cards = cards;
        this.gamePanel = gamePanel;
        this.eventPanel = eventPanel;
        
        // 화면 크기 고정 (GamePanel과 동일하게)
        setPreferredSize(new Dimension(1280, 720));
        setLayout(null); // 자유로운 배치를 위해 null 레이아웃 사용
        setBackground(Color.BLACK); // 이미지가 없을 경우를 대비한 배경색

        // 배경 이미지 로드
        try {
            java.net.URL imgUrl = getClass().getResource("/ui/images/title_bg.png");
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("타이틀 배경 이미지를 찾을 수 없습니다:  /ui/images/title_bg.png");
            }
        } catch (Exception ex) {
            System.err.println("타이틀 배경 이미지 로드 오류: " + ex. getMessage());
        }

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

        // '스토리 진행' 클릭 시 -> 컷씬 재생 후 1스테이지 시작
        storyButton.addActionListener(e -> {
        	gamePanel.setStoryMode(true);
            gamePanel.changeStage("Gold Coast");
            eventPanel.startCutscene("intro_script.txt", "GAME");
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
        
        // 타이틀 화면(패널)이 다시 화면에 그려질 때마다 실행되는 리스너입니다.
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                resetTitleUI();
            }
        });
    }
    
    // 타이틀 화면의 UI 상태를 초기화하는 메서드
    private void resetTitleUI() {
        // 버튼을 다시 보이게 설정
    	startButton.setVisible(true);
    	storyButton.setVisible(false);
        stageSelectButton.setVisible(false);
        
        // 만약 애니메이션이나 다른 UI 요소가 있다면 여기서 초기화합니다.
        // 예: startBtn.setText("게임 시작");
        
        revalidate();
        repaint();
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