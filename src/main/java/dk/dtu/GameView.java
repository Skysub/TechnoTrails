package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel implements View {
    private BattlePanel battlePanel;
    private GameLeaderboardPanel gameLeaderboardPanel;
    private GameChatPanel chatPanel;
    ViewManager viewManager;
	Client client;
	Lobby lobby;
    Menu menu;

    public GameView(ViewManager viewManager, Client client) {
        this.viewManager = viewManager;
        this.client = client;
        gamePanels();
    }

    public void gamePanels() {
        battlePanel = new BattlePanel(client);
        gameLeaderboardPanel = new GameLeaderboardPanel();
        chatPanel = new GameChatPanel();

        setLayout(new BorderLayout());

        JPanel rightPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        
        // TopRightPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(gameLeaderboardPanel,gbc);
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        rightPanel.add(chatPanel,gbc);
        add(battlePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        setVisible(true);
    }
    
    public void whenEntering() {
    	
    }
    
	public void whenExiting() {
		
	}
	
	public void clientRequestedUpdate() {
		
	}

}
