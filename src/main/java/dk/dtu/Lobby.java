package dk.dtu;

import java.awt.*;
import javax.swing.*;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import java.util.List;
import java.awt.event.*;

import dk.dtu.ServerInfo;

public class Lobby extends JPanel {
    private boolean playerReady;
    public int numberOfPlayers;

    SequentialSpace lobbySpace = new SequentialSpace();
    public JPanel playerPanel;
    public JFrame playerLabel;

    static final int SCREEN_HEIGHT = 720;
    static final int SCREEN_WIDTH = 1280;
    JButton backButton = new JButton("<-");

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        lobbyScreen(g);

    }

    public void lobbyScreen(Graphics g) {

        g.setColor(new Color(0, 76, 153));
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics metric = getFontMetrics(g.getFont());
        g.drawString("Port: " + 12345, (SCREEN_WIDTH - metric.stringWidth("Port: " + 12345)) / 2, 100);

        List<String> players2 = ServerInfo.nameList;

        // Players added to list for testing
        players2.add("Player1");
        players2.add("Player2");
        players2.add("Player3");
        players2.add("Player4");

        // create a box
        g.setColor(new Color(222, 240, 255));
        g.fillRect(SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 200, 400, 200);

        // creat an outline for the box
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(10));
        g2.setColor(new Color(0, 76, 153));
        g2.drawRect(SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 200, 400, 200);

        // Prints out the players in the lobby
        g.setColor(new Color(0, 76, 153));
        g.setFont(new Font("Serif", Font.BOLD, 20));
        FontMetrics metric2 = getFontMetrics(g.getFont());
        g.drawString("Players in lobby: ", (SCREEN_WIDTH - metric2.stringWidth("Players in lobby: ")) / 2, 200);

        // Print list of players
        int y = 230;
        g.setFont(new Font("Serif", Font.PLAIN, 17));
        for (String i : players2) {
            g.drawString(i, (SCREEN_WIDTH - metric2.stringWidth(i)) / 2, y);
            y += 25;
        }

        backButton.setBounds(25, 25, 50, 50);
        backButton.setForeground(Color.blue);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Menu();
            }
        });

    }

    public void playerJoin(String playerName) throws InterruptedException {
        playerReady = false;
        lobbySpace.put(playerName, playerReady);
    }

    public void playerReady(String playerName) throws InterruptedException {
        lobbySpace.get(new ActualField(playerName), new ActualField(playerReady));
        playerReady = true;
        lobbySpace.put(playerName, playerReady);
    }

    public boolean allPlayersReady() throws InterruptedException {
        List<Object[]> players = lobbySpace.queryAll(new FormalField(String.class), new ActualField(Boolean.class));
        if (players.size() != numberOfPlayers) {
            return false;
        }

        // Check if all players are ready
        for (Object[] player : players) {
            if (!(Boolean) player[1]) {
                return false;
            }
        }

        return true;
    }

}