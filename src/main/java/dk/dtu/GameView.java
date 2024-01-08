package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    public GameView() {
        setTitle("Three Boxes Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        // Create three panels
        JPanel leftPanel = new JPanel();
        JPanel topRightPanel = new JPanel();
        JPanel bottomRightPanel = new JPanel();

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameView());
    }
}
