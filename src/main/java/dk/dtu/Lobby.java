package dk.dtu;
import javax.swing.*;
import org.jspace.*;
import java.awt.*;
import java.util.List;

public class Lobby extends JPanel {
    private Space lobbySpace;
    JPanel playerPanel;
    private boolean playerReady;

    public Lobby() {
        lobbySpace = new SequentialSpace();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 76, 153));

        JLabel portLabel = new JLabel("Port: 12345", SwingConstants.CENTER);
        portLabel.setFont(new Font("Serif", Font.BOLD, 40));
        portLabel.setForeground(Color.WHITE);
        add(portLabel, BorderLayout.NORTH);

        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBackground(new Color(0, 76, 153));
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        add(playerPanel, BorderLayout.CENTER);
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

    public boolean allPlayersReady(int numberOfPlayers) throws InterruptedException {
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
    }}
