package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import dk.dtu.ServerInfo;

import java.awt.event.*;

public class Menu extends JFrame {

    static final int SCREEN_HEIGHT = 720;
    static final int SCREEN_WIDTH = 1280;
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
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
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
                add(lobby.backButton);
                setVisible(true);
            }
        }

        public void startScreen(Graphics g) {

            g.setColor(new Color(0, 76, 153));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            FontMetrics metric = getFontMetrics(g.getFont());
            g.drawString("Techno Trails", (SCREEN_WIDTH - metric.stringWidth("Techno Trails")) / 2, 200);

            // Add a JLabel for instruction
            g.setColor(new Color(0, 76, 153));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            g.drawString("Name", 250, 505);

            // Add a JTextField for input
            JTextField textField;
            textField = new JTextField();
            textField.setBounds(SCREEN_WIDTH / 2 - 60, 500, 120, 40);
            add(textField);
            setVisible(true);

            new ServerInfo();

            // Add a "Save" JButton
            JButton saveButton = new JButton("Save");
            saveButton.setBounds(SCREEN_WIDTH / 2 - 30, 550, 60, 30);

            // Add an ActionListener to the "Save" button
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    System.out.println("Save button clicked");

                    // Get the text from the JTextField
                    String name = textField.getText();

                    // Add the name to the list
                    ServerInfo.addName(name);

                    // Clear the JTextField
                    textField.setText("");
                }
            });

            hostButton.setBounds(SCREEN_WIDTH / 2 - 60, 250, 120, 50);
            hostButton.setForeground(Color.white);
            hostButton.setBackground(new Color(0, 76, 153));
            hostButton.setOpaque(true);
            hostButton.setBorderPainted(false);
            hostButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // System.out.println("test");
                    currentscreen = "playscreen";
                    board.remove(hostButton);
                    board.remove(joinButton);
                    board.remove(textField);
                    board.remove(saveButton);

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
            board.add(saveButton);

        }

    }
}
