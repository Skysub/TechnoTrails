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
    	setLayout(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.ipady = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 1;
        
        JLabel title = new JLabel("Port: " + 12345);
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(new Color(0, 76, 153));
        title.setHorizontalAlignment(0);
        
        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setPreferredSize(new Dimension(400,200));
        playerPanel.setBackground(new Color(0, 76, 153));
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        backButton.setPreferredSize(new Dimension(50,50));
        backButton.setForeground(Color.blue);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	viewManager.changeView("menu");
            }
        });
        
    	GridBagConstraints backgbc = new GridBagConstraints();
    	backgbc.gridwidth = GridBagConstraints.REMAINDER;
    	backgbc.fill = GridBagConstraints.NONE;
    	backgbc.anchor = GridBagConstraints.FIRST_LINE_START;
    	backgbc.weightx = 1;
    	backgbc.weighty = 1;
    	backgbc.insets = new Insets(20,20,20,20);
        
        add(backButton, backgbc);
        add(Box.createVerticalGlue(), gbc);
        add(title, gbc);
        add(Box.createVerticalStrut(50), gbc);
        add(playerPanel, gbc);
        add(Box.createVerticalStrut(200), gbc);
        

    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(102, 178, 255));
        g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());
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