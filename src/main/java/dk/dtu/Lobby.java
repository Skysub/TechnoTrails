package dk.dtu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Lobby extends JPanel {

    public JPanel playerPanel;
    static final int SCREEN_HEIGHT = 700;
    static final int SCREEN_WIDTH = 700;
    

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        lobbyScreen(g);

    }

    public void lobbyScreen (Graphics g) {

        g.setColor(new Color(0, 76, 153));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            FontMetrics metric = getFontMetrics(g.getFont());
            g.drawString("Port: " + 12345, (SCREEN_WIDTH - metric.stringWidth("Port: " + 12345)) / 2, 100);

            playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setBounds(SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 200, 400, 200);
            playerPanel.setBackground(new Color(0, 76, 153));
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            

    }




}