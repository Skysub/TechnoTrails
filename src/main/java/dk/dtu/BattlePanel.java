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

import org.apache.commons.lang3.tuple.ImmutablePair;

public class BattlePanel extends JPanel {
    private CustomDrawingPanel drawingPanel;
    private Client client;

    public BattlePanel(Client client) {
        this.client = client;
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

    private class CustomDrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            for (PlayerInfo p : client.getGameState().players)
                drawGame(g2d, p);
        }

        public void drawGame(Graphics2D g2d, PlayerInfo p) {
            drawingPanel.drawPlayer(g2d, p);
            drawingPanel.drawTrail(g2d, p);
        }

        public void drawPlayer(Graphics2D g2d, PlayerInfo p) {
            g2d.setColor(Color.RED);
            Rectangle2D.Float rect = new Rectangle2D.Float(p.x, p.y, 50.5f, 50.5f);
            g2d.fill(rect);

        }

        public void drawTrail(Graphics2D g2d, PlayerInfo p) {

            g2d.setColor(Color.RED);
            for (ImmutablePair<Float, Float> t : p.trail) {
                Rectangle2D.Float rect = new Rectangle2D.Float(t.left, t.right, 25, 25);
                g2d.fill(rect);
            }

        }
    }
}
