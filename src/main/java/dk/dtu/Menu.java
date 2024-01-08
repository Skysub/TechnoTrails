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
    public String currentscreen = "startscreen";
    Lobby lobby = new Lobby();

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
                lobby.lobbyScreen(g);
                add(lobby.playerPanel);
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

    }
}
