package dk.dtu;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;

public class Menu extends JFrame {

    static final int SCREEN_HEIGHT = 700;
    static final int SCREEN_WIDTH = 700;
    JButton joinButton = new JButton("JOIN GAME");
    JButton hostButton = new JButton("HOST GAME");
    BoardPanel board = new BoardPanel();
    private JPanel playerPanel;
    public String currentscreen = "startscreen";

    public Menu() {
        initGUI();
    }

    public void initGUI() {
        setTitle("Tehcno Trails");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(SCREEN_HEIGHT, SCREEN_WIDTH);
        setFocusable(true);
        setLayout(null);

        board.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        add(board);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class BoardPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);

        }

        private void drawBoard(Graphics g) {
            g.setColor(new Color(102, 178, 255));
            g.fillRect(0, 0, getWidth(), getHeight());
            if (currentscreen == "startscreen") {
                startScreen(g);
            } else if (currentscreen == "playscreen") {
                hostScreen(g);
            }
        }

        public void startScreen(Graphics g) {
            g.setColor(new Color(0, 76, 153));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            FontMetrics metric = getFontMetrics(g.getFont());
            g.drawString("Techno Trails", (SCREEN_WIDTH - metric.stringWidth("Techno Trails")) / 2, 200);

            hostButton.setBounds(SCREEN_WIDTH / 2 - 60, 250, 120, 50);
            hostButton.setForeground(Color.white);
            hostButton.setBackground(new Color(0, 76, 153));
            hostButton.setOpaque(true);
            hostButton.setBorderPainted(false);
            hostButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("test");
                    currentscreen = "playscreen";
                    board.remove(hostButton);
                    board.remove(joinButton);
                    repaint();
                }
            });

            joinButton.setBounds(SCREEN_WIDTH / 2 - 60, 330, 120, 50);
            joinButton.setForeground(Color.white);
            joinButton.setBackground(new Color(0, 76, 153));
            joinButton.setOpaque(true);
            joinButton.setBorderPainted(false);
            joinButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("test2");
                }
            });

            board.add(hostButton);
            board.add(joinButton);

        }

        public void hostScreen(Graphics g) {

            g.setColor(new Color(0, 76, 153));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            FontMetrics metric = getFontMetrics(g.getFont());
            g.drawString("Port: " + 12345, (SCREEN_WIDTH - metric.stringWidth("Port: " + 12345)) / 2, 100);

            playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setBounds(SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 200, 400, 200);
            playerPanel.setBackground(new Color(0, 76, 153));
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            add(playerPanel);

        }

    }

    public static void main(String[] args) {
        new Menu();
    }

}
