package dk.dtu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView extends JPanel implements View {
    private BattlePanel battlePanel;
    private GameControls gameControls;
    private GameLeaderboardPanel gameLeaderboardPanel;
    private GameChatPanel chatPanel;
    ViewManager viewManager;
    Client client;
    Menu menu;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int countdownNumber = -1;
    GridBagConstraints gbc = new GridBagConstraints();
    JPanel rightPanel = new JPanel(new GridBagLayout());

    public GameView(ViewManager viewManager, Client client) {
        this.viewManager = viewManager;
        this.client = client;
        gamePanels();
    }

    public void gamePanels() {
        gameControls = new GameControls();
        battlePanel = new BattlePanel(client);

        setLayout(new BorderLayout());

        // TopRightPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
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

        // Setbounds doesn't work. Use prefferedSize instead
        //countdownLabel.setBounds(battlePanel.getWidth() / 2 - 40, battlePanel.getHeight() / 2 - 50, 100, 100);

        battlePanel.add(countdownLabel);

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownNumber--;
                if (countdownNumber <= 0) {
                    countdownTimer.stop();
                    countdownLabel.setText(""); // Clear the countdown label
                    battlePanel.remove(countdownLabel); // The component is no longer needed
                } else {

                    // countdownNumber++;
                    countdownLabel.setText(String.valueOf(countdownNumber));
                }
            }
        });

        countdownTimer.start();
    }

    public void whenEntering() {
    	battlePanel.drawGameTimer.start();
    	
        if (countdownNumber == -1)
            initCountdown();
            
        gameLeaderboardPanel = new GameLeaderboardPanel(client.getServerInfo());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(gameLeaderboardPanel, gbc);

        chatPanel = new GameChatPanel(client);
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        rightPanel.add(chatPanel, gbc);

    }

    public void whenExiting() {
    	battlePanel.drawGameTimer.stop();
    }

    public void clientRequestedUpdate() {

    }

}
