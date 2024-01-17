package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel implements View {
    private BattlePanel battlePanel;
    private GameControls gameControls;
    private GameLeaderboardPanel gameLeaderboardPanel;
    private GameChatPanel chatPanel;
    ViewManager viewManager;
    Client client;
    Menu menu;
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
        addKeyListener(gameControls);

        setLayout(new BorderLayout());

        // TopRightPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(battlePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        setFocusable(true);
        setVisible(true);
    }

    public void RefreshScoreboard() {
        gameLeaderboardPanel.RefreshScoreboard(client.getServerInfo());
    }

    public void whenEntering() {
        battlePanel.drawGameTimer.start();
        battlePanel.startCountdown();

        if (gameLeaderboardPanel != null)
            remove(gameLeaderboardPanel);
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
        repaint();
        requestFocus();
    }

    public void whenExiting() {
        battlePanel.drawGameTimer.stop();
        battlePanel.setWinnerTimeLeft(-10);
        remove(gameLeaderboardPanel);
    }

    public void clientRequestedUpdate() {
        if (gameLeaderboardPanel != null)
            gameLeaderboardPanel.RefreshScoreboard(client.getServerInfo());
    }

    public GameControls getGameControls() {

        return gameControls;
    }

}
