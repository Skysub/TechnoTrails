package dk.dtu;

import javax.swing.*;
import java.awt.*;
import dk.dtu.PlayerInfo;
import java.awt.geom.Rectangle2D;

public class GameView extends JFrame {
    private JPanel leftPanel;
    private JPanel topRightPanel;
    private JTable bottomRightPanel;

    public GameView() {
        setTitle("Three Boxes Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        gamePanels();

    }

    public void gamePanels() {

        // Create three panels
        CustomDrawingPanel leftPanel = new CustomDrawingPanel();
        JPanel topRightPanel = new JPanel();
        JTable bottomRightPanel = new CustomChatTable();

        // Set black border for all panels
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        topRightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomRightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Set layout for the frame
        setLayout(new BorderLayout());

        // Set layout for the right side panels
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5; // Adjust the weight to control the filling
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(topRightPanel, gbc);

        gbc.gridy = 1;
        rightPanel.add(bottomRightPanel, gbc);

        // Set layout for the left panel
        JPanel panelLeft = new JPanel(new GridBagLayout());
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.weightx = 1.0; // Allow leftPanel to fill horizontally
        gbcLeft.weighty = 1.0; // Allow leftPanel to fill vertically
        gbcLeft.fill = GridBagConstraints.BOTH;
        panelLeft.add(leftPanel, gbcLeft);

        // Increase preferred sizes (adjust these values as needed)
        leftPanel.setPreferredSize(new Dimension(400, 400));
        topRightPanel.setPreferredSize(new Dimension(200, 200));
        bottomRightPanel.setPreferredSize(new Dimension(200, 200));

        // Add the panels to the frame
        add(panelLeft, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.WEST);

        // Display the frame
        setVisible(true);
    }

    // Getters for each of the panels
    public JPanel getlPanel() {
        return leftPanel;
    }

    public JPanel gettrPanel() {
        return topRightPanel;
    }

    public JPanel getbrPanel() {
        return bottomRightPanel;
    }

    // start fucntion of drawing the game
    public void drawGame(JPanel panel, PlayerInfo p) {
        drawPlayer(panel, p);

    }

    // drawing the player
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameView());
    }

    private class CustomDrawingPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);

            // Draw a filled rectangle (x, y, width, height)
            // Rectangle2D.Float rect = new Rectangle2D.Float(p.x, p.y, 50.5f, 50.5f);
            Rectangle2D.Float rect = new Rectangle2D.Float(50, 50, 50.5f, 50.5f);
            g2d.fill(rect);
        }
    }
    private class CustomChatTable extends JTable {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
        }
    }
}
