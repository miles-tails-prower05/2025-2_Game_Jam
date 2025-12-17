import javax.swing.*;
import java.awt.*;

import stage.*;

public class GameMain {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Sonic Labyrinth Zone - HD");
        GamePanel gamePanel = new GamePanel(); 
        
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}