package dk.dtu;

import java.awt.*;
import javax.swing.*;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;

import java.util.List;
import java.awt.event.*;

public class Lobby extends JPanel{
    private boolean playerReady;
    public int numberOfPlayers;

    SequentialSpace lobbySpace = new SequentialSpace();
    public JPanel playerPanel;
    JButton backButton = new JButton("<-");
    ViewManager viewManager;
    
    public Lobby(ViewManager viewManager)  {
    	this.viewManager = viewManager;
    	setBounds(viewManager.getBounds());
    	initLobby();
    }
    

    public void initLobby() {
        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBounds(viewManager.getWidth() / 2 - 200, viewManager.getHeight() / 2 - 200, 400, 200);
        playerPanel.setBackground(new Color(0, 76, 153));
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        backButton.setBounds(25, 25, 50, 50);
        backButton.setForeground(Color.blue);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	viewManager.changeView("menu");
            }
        });
        
        add(playerPanel);
        add(backButton);

    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(102, 178, 255));
        g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());

        g.setColor(new Color(0, 76, 153));
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics metric = getFontMetrics(g.getFont());
        g.drawString("Port: " + 12345, (viewManager.getWidth() - metric.stringWidth("Port: " + 12345)) / 2, 100);
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