package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private BattlePanel battlePanel;
    private GameLeaderboardPanel gameLeaderboardPanel;
    private GameChatPanel chatPanel;

    public GameView() {
        setTitle("Three Boxes Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        gamePanels();
    }

    public void gamePanels() {
        battlePanel = new BattlePanel();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameView());
    }
}
