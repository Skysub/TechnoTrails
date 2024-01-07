package dk.dtu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame{
	static final int SCREEN_HEIGHT = 700;
    static final int SCREEN_WIDTH = 700;
    JButton settingsButton = new JButton("SETTINGS");
    JButton play = new JButton("Play Game");
    BoardPanel board = new BoardPanel();

    public GUI() {
        initGUI();
    }

    public void initGUI() {
        setTitle("Tehcno Trails");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(SCREEN_HEIGHT, SCREEN_WIDTH);
        setFocusable(true);
        setLayout(null); // Set the layout to null for absolute positioning

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
            startScreen(g);
        }

    public void startScreen(Graphics g) {
        g.setColor(new Color(0, 76, 153));
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics metric = getFontMetrics(g.getFont());
        g.drawString("Techno Trails", (SCREEN_WIDTH - metric.stringWidth("Techno Trails")) / 2, 200);

        play.setBounds(SCREEN_WIDTH / 2 - 75, 250, 150, 75);
        play.setForeground(Color.white);
        play.setBackground(new Color(0,76,153));
        play.setOpaque(true);
        play.setBorderPainted(false);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("test");
            }
        });



        settingsButton.setBounds(SCREEN_WIDTH / 2 - 60, 350, 120, 50);
        settingsButton.setForeground(Color.white);
        settingsButton.setBackground(new Color(0, 76, 153));
        settingsButton.setOpaque(true);
        settingsButton.setBorderPainted(false);
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("test2");
            }
        });
        

        board.add(play);
        board.add(settingsButton);

    }
    }
}
