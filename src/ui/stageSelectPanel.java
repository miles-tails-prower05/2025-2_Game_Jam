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
    private JButton stage3Btn;
    private JButton stage4Btn;
    private JButton stage5Btn;
    private JButton stage6Btn;
    private JButton stage7Btn;

    public stageSelectPanel(Container frame, CardLayout cards, GamePanel gamePanel, SaveManager saveManager) {
        this.frame = frame;
        this.cards = cards;
        this.gamePanel = gamePanel;
        this.saveManager = saveManager;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(new GridLayout(3, 3, 20, 20));
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // 버튼 생성
        stage1Btn = new JButton();
        stage2Btn = new JButton();
        stage3Btn = new JButton();
        stage4Btn = new JButton();
        stage5Btn = new JButton();
        stage6Btn = new JButton();
        stage7Btn = new JButton();
        
        Font btnFont = new Font("Malgun Gothic", Font.BOLD, 16);
        stage1Btn.setFont(btnFont);
        stage2Btn.setFont(btnFont);
        stage3Btn.setFont(btnFont);
        stage4Btn.setFont(btnFont);
        stage5Btn.setFont(btnFont);
        stage6Btn.setFont(btnFont);
        stage7Btn.setFont(btnFont);

        // 초기 텍스트 설정
        updateButtonText();

        // Add action listeners using helper method
        addStageListener(stage1Btn, "Gold Coast");
        addStageListener(stage2Btn, "Emerald Sea");
        addStageListener(stage3Btn, "Shattered Wreckage");
        addStageListener(stage4Btn, "스테이지 4");
        addStageListener(stage5Btn, "스테이지 5");
        addStageListener(stage6Btn, "스테이지 6");
        addStageListener(stage7Btn, "스테이지 7");
        
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
        add(stage3Btn);
        add(stage4Btn);
        add(stage5Btn);
        add(stage6Btn);
        add(stage7Btn);
        
        // Back button
        JButton backBtn = new JButton("뒤로가기");
        backBtn.setFont(btnFont);
        backBtn.addActionListener(e -> {
            cards.show(frame, "TITLE");
        });
        add(backBtn);
        
        // ★ 추가: 이 패널이 화면에 보일 때마다 기록 갱신
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateButtonText();
            }
        });
    }
    
    // Helper method to add stage action listener
    private void addStageListener(JButton button, String stageName) {
        button.addActionListener(e -> {
            gamePanel.changeStage(stageName);
            cards.show(frame, "GAME");
            gamePanel.requestFocus();
        });
    }
    
    // ★ 추가: 저장된 기록을 불러와 버튼 텍스트 변경
    private void updateButtonText() {
        long time1 = saveManager.getBestTime("Gold Coast");
        long time2 = saveManager.getBestTime("Emerald Sea");
        long time3 = saveManager.getBestTime("Shattered Wreckage");
        long time4 = saveManager.getBestTime("스테이지 4");
        long time5 = saveManager.getBestTime("스테이지 5");
        long time6 = saveManager.getBestTime("스테이지 6");
        long time7 = saveManager.getBestTime("스테이지 7");
        
        stage1Btn.setText(formatButtonText("스테이지 1: Gold Coast", time1));
        stage2Btn.setText(formatButtonText("스테이지 2: Emerald Sea", time2));
        stage3Btn.setText(formatButtonText("스테이지 3: Shattered Wreckage", time3));
        stage4Btn.setText(formatButtonText("스테이지 4: 심해", time4));
        stage5Btn.setText(formatButtonText("스테이지 5: 수중 미로", time5));
        stage6Btn.setText(formatButtonText("스테이지 6: 평행 세계", time6));
        stage7Btn.setText(formatButtonText("스테이지 7: 외딴 섬", time7));
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