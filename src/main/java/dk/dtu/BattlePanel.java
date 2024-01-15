package dk.dtu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class BattlePanel extends JPanel {
    private CustomDrawingPanel drawingPanel;
    private Client client;
    static final int SCREEN_HEIGHT = 720;
    static final int SCREEN_WIDTH = 1280;

    public BattlePanel(Client client) {
        this.client = client;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        drawingPanel = new CustomDrawingPanel();
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        drawingPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        drawingPanel.setBackground(Color.black);
        add(drawingPanel, gbc);
        Timer timer = new Timer(1000 / 60, e -> repaint());
        timer.start();
    }

    private class CustomDrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            drawGame(g2d, client.getGameState());

        }

        public void drawGame(Graphics2D g2d, GameState gameState) {
        	Stroke old = g2d.getStroke();
        	g2d.setStroke(new BasicStroke(5));
        	g2d.drawRect(0, 0, gameState.levelX, gameState.levelY);

            for (PlayerInfo p : gameState.players.values()) {
            drawingPanel.drawPlayer(g2d, p);
            drawingPanel.drawTrail(g2d, p);
        }
    }

        public void drawPlayer(Graphics2D g2d, PlayerInfo p) {
            Path2D.Float triangle = new Path2D.Float();
            float size = 4;
            float x = p.x;
            float y = p.y;
            float rotation = p.rotation;

            // Move to the first vertex
            triangle.moveTo(
                    x + size * Math.cos(rotation),
                    y + size * Math.sin(rotation));

            // Line to the second vertex
            triangle.lineTo(
                    x + size * Math.cos(rotation + 2 * Math.PI / 3),
                    y + size * Math.sin(rotation + 2 * Math.PI / 3));

            // Line to the third vertex and close the path
            triangle.lineTo(
                    x + size * Math.cos(rotation - 2 * Math.PI / 3),
                    y + size * Math.sin(rotation - 2 * Math.PI / 3));
            triangle.closePath();

            g2d.setColor(Color.yellow);
            g2d.fill(triangle);
        }

        public void drawTrail(Graphics2D g2d, PlayerInfo p) {
            ArrayList<ImmutablePair<Float, Float>> trail = p.trail; // Assuming this is how you get the trail

            for (int i = 0; i < trail.size() - 1; i++) {

                ImmutablePair<Float, Float> point1 = trail.get(i);
                ImmutablePair<Float, Float> point2 = trail.get(i + 1);

                // Extracting the x and y coordinates of the points
                float x1 = point1.getLeft();
                float y1 = point1.getRight();
                float x2 = point2.getLeft();
                float y2 = point2.getRight();

                // Drawing a line between the points
                // Convert float coordinates to integers
                g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                // Alternatively, if you want to draw with float precision, you can use:
                // g2d.draw(new Line2D.Float(x1, y1, x2, y2));
            }
        }
    }
}
