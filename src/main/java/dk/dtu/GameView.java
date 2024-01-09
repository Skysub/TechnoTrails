package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private LeftPanel leftPanel;
    private TopRightPanel topRightPanel;
    private BottomRightPanel bottomRightPanel;

    public GameView() {
        setTitle("Three Boxes Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        gamePanels();
    }

    public void gamePanels() {
        leftPanel = new LeftPanel();
        topRightPanel = new TopRightPanel();
        bottomRightPanel = new BottomRightPanel();

        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // TopRightPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5; // Weight determines how much space it takes relative to other components
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(topRightPanel, gbc);

        // BottomRightPanel
        gbc.gridy = 1; // Placing it below the TopRightPanel
        gbc.weighty = 0.5;
        rightPanel.add(bottomRightPanel, gbc);

        add(rightPanel, BorderLayout.EAST);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameView());
    }
}
