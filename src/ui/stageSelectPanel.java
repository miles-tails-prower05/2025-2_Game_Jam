package ui;

import java.awt.*;
import java.awt.event.*; // 이벤트 리스너 추가
import javax.swing.*;
import stage.GamePanel;
import stage.MapManager;
import data.SaveManager; // ★ DataManager 임포트

public class stageSelectPanel extends JPanel {
    private CardLayout cards;
    private Container frame;
    private GamePanel gamePanel;
    
    private SaveManager saveManager;
    
    // ★ 버튼을 멤버 변수로 승격
    private JButton stage1Btn;
    private JButton stage2Btn;
    private JButton backToTitleBtn; // 타이틀로 가기 버튼 추가

    public stageSelectPanel(Container frame, CardLayout cards, GamePanel gamePanel, SaveManager saveManager) {
        this.frame = frame;
        this.cards = cards;
        this.gamePanel = gamePanel;
        this.saveManager = saveManager;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 300));
        setBackground(Color.DARK_GRAY);

        // 버튼 생성
        stage1Btn = new JButton();
        stage2Btn = new JButton();
        
        Font btnFont = new Font("Malgun Gothic", Font.BOLD, 20);
        stage1Btn.setFont(btnFont);
        stage2Btn.setFont(btnFont);
        stage1Btn.setPreferredSize(new Dimension(250, 80)); // 텍스트가 길어지므로 크기 조절
        stage2Btn.setPreferredSize(new Dimension(250, 80));

        // 초기 텍스트 설정
        updateButtonText();

        stage1Btn.addActionListener(e -> {
            gamePanel.changeStage("스테이지 1");
            cards.show(frame, "GAME");
            gamePanel.requestFocus();
        });

        stage2Btn.addActionListener(e -> {
            gamePanel.changeStage("스테이지 2");
            cards.show(frame, "GAME");
            gamePanel.requestFocus();
        });
        
        // 3. 타이틀로 가기 버튼 (추가된 부분)
        backToTitleBtn = new JButton("타이틀로 가기");
        backToTitleBtn.setFont(btnFont);
        backToTitleBtn.setPreferredSize(new Dimension(250, 80));
        backToTitleBtn.setBackground(new Color(150, 50, 50)); // 구분하기 쉽게 붉은 계열 색상 추가
        backToTitleBtn.setForeground(Color.WHITE);
        backToTitleBtn.addActionListener(e -> {
            cards.show(frame, "TITLE"); // 타이틀 화면으로 이동
        });
        
        add(stage1Btn);
        add(stage2Btn);
        add(backToTitleBtn);
        
        // ★ 추가: 이 패널이 화면에 보일 때마다 기록 갱신
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateButtonText();
            }
        });
    }
    
    // ★ 추가: 저장된 기록을 불러와 버튼 텍스트 변경
    private void updateButtonText() {
        long time1 = saveManager.getBestTime("스테이지 1");
        long time2 = saveManager.getBestTime("스테이지 2");
        
        stage1Btn.setText(formatButtonText("스테이지 1", time1));
        stage2Btn.setText(formatButtonText("스테이지 2", time2));
    }
    
    // ★ HTML 태그를 사용하여 멀티라인 텍스트 만들기
    private String formatButtonText(String stageName, long time) {
        String timeStr;
        if (time == -1) {
            timeStr = "No Record";
        } else {
            long minutes = (time / 1000) / 60;
            long seconds = (time / 1000) % 60;
            long hund = (time / 10) % 100;
            timeStr = String.format("%02d:%02d.%02d", minutes, seconds, hund);
        }
        
        return "<html><center>" + stageName + "<br><font size='3' color='blue'>" + timeStr + "</font></center></html>";
    }
}