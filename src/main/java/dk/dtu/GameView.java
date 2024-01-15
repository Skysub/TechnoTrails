package dk.dtu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView extends JPanel implements View {
    private BattlePanel battlePanel;
    private GameLeaderboardPanel gameLeaderboardPanel;
    private GameChatPanel chatPanel;
    ViewManager viewManager;
	Client client;
	Lobby lobby;
    Menu menu;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int countdownNumber = -1;

    
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

    public void initCountdown() {
    	countdownNumber = GamePlay.GAME_COUNTDOWN;
        countdownLabel = new JLabel(String.valueOf(countdownNumber), SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Set font size and style
        countdownLabel.setForeground(Color.RED); // Set text color

        // Position the countdownLabel in the middle of the battlePanel
        
        //Setbounds doesn't work. Use prefferedSize instead
        //countdownLabel.setBounds(battlePanel.getWidth() / 2 - 20, battlePanel.getHeight() / 2 - 50, 100, 100);
        battlePanel.add(countdownLabel);

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownNumber--;
                if (countdownNumber <= 0) {
                    countdownTimer.stop();
                    countdownLabel.setText(""); // Clear the countdown label
                    battlePanel.remove(countdownLabel); //The component is no longer needed
                } else {

                    //countdownNumber++;
                    countdownLabel.setText(String.valueOf(countdownNumber));
                }
            }
        });

        countdownTimer.start();
    }
    
    public void whenEntering() {
    	if(countdownNumber == -1) initCountdown();
    }
    
	public void whenExiting() {
		
	}
	
	public void clientRequestedUpdate() {
		
	}

}
