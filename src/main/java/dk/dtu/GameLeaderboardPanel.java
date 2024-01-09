package dk.dtu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JTable;

public class GameLeaderboardPanel extends JTable {

    public GameLeaderboardPanel() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(200, 200));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
