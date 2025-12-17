package ui;

import java.awt.*;
import javax.swing.*;
import stage.GamePanel;

public class stageSelectPanel extends JPanel {
    private CardLayout cards;
    private Container frame;
    private GamePanel gamePanel;

    public stageSelectPanel(Container frame, CardLayout cards, GamePanel gamePanel) {
        this.frame = frame;
        this.cards = cards;
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 300)); // 중앙 정렬
        setBackground(Color.DARK_GRAY);

        JButton stage1Btn = new JButton("스테이지 1");
        JButton stage2Btn = new JButton("스테이지 2");
        
        // 버튼 스타일 설정
        Font btnFont = new Font("Malgun Gothic", Font.BOLD, 20);
        stage1Btn.setFont(btnFont);
        stage2Btn.setFont(btnFont);
        stage1Btn.setPreferredSize(new Dimension(200, 60));
        stage2Btn.setPreferredSize(new Dimension(200, 60));

        // 스테이지 1 버튼 기능
        stage1Btn.addActionListener(e -> {
            gamePanel.changeStage("스테이지 1"); // GamePanel의 public 메서드 호출
            cards.show(frame, "GAME");
            gamePanel.requestFocus(); // 포커스 이동
        });

        // 스테이지 2 버튼 기능
        stage2Btn.addActionListener(e -> {
            gamePanel.changeStage("스테이지 2");
            cards.show(frame, "GAME");
            gamePanel.requestFocus();
        });
        
        add(stage1Btn);
        add(stage2Btn);
    }
}