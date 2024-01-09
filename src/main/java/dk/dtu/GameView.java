package dk.dtu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class GameView extends JFrame {
    private LeftPanel leftPanel;
    private JPanel topRightPanel;
    private JTable bottomRightPanel;

    public GameView() {
        setTitle("Three Boxes Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        gamePanels();
    }

    public void gamePanels() {
        leftPanel = new LeftPanel();

        topRightPanel = new JPanel();
        topRightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        topRightPanel.setPreferredSize(new Dimension(200, 200));

        bottomRightPanel = new CustomChatTable();
        bottomRightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomRightPanel.setPreferredSize(new Dimension(200, 200));

        setLayout(new BorderLayout());

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(topRightPanel, gbc);

        gbc.gridy = 1;
        rightPanel.add(bottomRightPanel, gbc);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setVisible(true);
    }

    public JPanel getTopRightPanel() {
        return topRightPanel;
    }

    public JTable getBottomRightPanel() {
        return bottomRightPanel;
    }

    private class CustomChatTable extends JTable {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Custom painting code for the table
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new GameView());
    }
}
