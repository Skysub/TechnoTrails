package dk.dtu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class GameChatPanel extends JPanel {
    static final int SCREEN_HEIGHT = 720;
	static final int SCREEN_WIDTH = 1280;

    public GameChatPanel() {
         setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(200, 200));

    }

  @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(102, 178, 255));
		    g.fillRect(0, 0, 200, 400);
        }
}

