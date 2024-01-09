package dk.dtu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class BattlePanel extends JPanel {
    private CustomDrawingPanel drawingPanel;

    public BattlePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        drawingPanel = new CustomDrawingPanel();
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        drawingPanel.setPreferredSize(new Dimension(400, 400));
        add(drawingPanel, gbc);
    }

    public void drawGame(PlayerInfo p) {
        drawingPanel.drawPlayer(drawingPanel, p);
    }

    private class CustomDrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            Rectangle2D.Float rect = new Rectangle2D.Float(50, 50, 50.5f, 50.5f);
            g2d.fill(rect);
        }

 public void drawPlayer(JPanel panel, PlayerInfo p) {
        Graphics g = panel.getGraphics();
        if (g != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);

            // Draw a filled rectangle (x, y, width, height)
            // Rectangle2D.Float rect = new Rectangle2D.Float(p.x, p.y, 50.5f, 50.5f);
            Rectangle2D.Float rect = new Rectangle2D.Float(50, 50, 50.5f, 50.5f);
            g2d.fill(rect);

        }
    }
    }
}

