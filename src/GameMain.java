import javax.swing.*;
import java.awt.*;
import stage.*;
import ui.*;

public class GameMain {
    public static void main(String[] args) {        
        final JFrame frame = new JFrame("2D 플랫포머 게임");
        final CardLayout cards = new CardLayout();
        frame.setLayout(cards);
        
        // 1. 게임 패널 생성 (기본값 스테이지 1)
        // 화면 전환 시 Timer 제어를 위해 GamePanel의 addComponentListener 코드가 잘 동작할 것입니다.
        GamePanel gamePanel = new GamePanel(frame.getContentPane(), cards, "스테이지 1");
        
        // 2. 타이틀 패널 생성
        titlePanel title = new titlePanel(frame.getContentPane(), cards, gamePanel);
        
        // 3. 스테이지 선택 패널 생성
        stageSelectPanel stageSelect = new stageSelectPanel(frame.getContentPane(), cards, gamePanel);
        
        // 카드 레이아웃에 패널 추가 (이름표 부여)
        frame.add(title, "TITLE");        // 타이틀 화면
        frame.add(stageSelect, "SELECT"); // 스테이지 선택 화면
        frame.add(gamePanel, "GAME");     // 실제 게임 화면
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // 패널들의 preferredSize에 맞춰 창 크기 조절
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // 시작할 때 타이틀 화면 보여주기
        cards.show(frame.getContentPane(), "TITLE");
    }
}