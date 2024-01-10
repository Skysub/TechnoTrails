package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class Lobby extends JPanel {

    private boolean playerReady;
    public int numberOfPlayers;

    public SpaceRepository repository;
    public Space lobbySpace;

    public JScrollPane playerPanel;
    public JScrollPane chatPanel;
    public JFrame playerLabel;
    private JTextField chatField;
    ArrayList<String> players2;
    ArrayList<String> chat;
    JTable playerTable;
    JTable chatTable;

    JButton backButton = new JButton("<-");
    JButton readyButton = new JButton("Ready");
    JButton startButton = new JButton("Start Game");
    ViewManager viewManager;
    Client client;
    Menu menu;

    public Lobby(ViewManager viewManager, Client client) {
        this.viewManager = viewManager;
        this.client = client;
        setBounds(viewManager.getBounds());

        this.repository = new SpaceRepository();
        this.lobbySpace = new SequentialSpace();
        this.repository.add("lobby", this.lobbySpace);

        initLobby();
    }

    public void initLobby() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.ipady = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 10, 0, 10);

		JLabel title = new JLabel("Port: " + client.getHostAddress());
		title.setFont(new Font("Serif", Font.BOLD, 40));
		title.setForeground(new Color(0, 76, 153));
		title.setHorizontalAlignment(0);
		players2 = new ArrayList<String>();
		

        // The table showing the players
        String[] TABLE_COLUMNS = { "Players" };
        DefaultTableModel tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }
        };
        for (int i = 0; i < players2.size(); i++) {

            tableModel.addRow(new String[] { players2.get(i) });
        }
        playerTable = new JTable(tableModel);
        playerTable.setRowHeight(20);

        String[] CHAT_COLUMNS = { "Chat" };
        DefaultTableModel chatModel = new DefaultTableModel(CHAT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }
        };
        // for (int i = 0; i < chat.size(); i++) {

        // chatModel.addRow(new String[] { chat.get(i) });
        // }
        chatTable = new JTable(chatModel);
        chatTable.setRowHeight(20);

        playerPanel = new JScrollPane(playerTable);
        playerPanel.setPreferredSize(new Dimension(400, 200));
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        playerPanel.getViewport().setBackground(new Color(0, 76, 153));
        // playerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chatPanel = new JScrollPane(chatTable);
        chatPanel.setPreferredSize(new Dimension(400, 200));
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        chatPanel.getViewport().setBackground(new Color(0, 76, 153));

        chatField = new JTextField();
        chatField.setPreferredSize(new Dimension(150, 1));
        Border border = BorderFactory.createLineBorder(Color.WHITE, 2);
        chatField.setBorder(border);

        backButton.setPreferredSize(new Dimension(50, 50));
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
        backgbc.insets = new Insets(20, 20, 20, 20);

        readyButton.setPreferredSize(new Dimension(50, 50));
        readyButton.setForeground(Color.blue);
        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerReady = true;
            }
        });

        startButton.setPreferredSize(new Dimension(50, 50));
        startButton.setForeground(Color.blue);
        startButton.setToolTipText("Only host can start game when all players are ready");
        startButton.setEnabled(playerReady);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        GridBagConstraints readyGbc = new GridBagConstraints();
        readyGbc.gridx = 0; // Adjust the grid position as needed
        readyGbc.gridy = 3; // Adjust the grid position as needed
        readyGbc.fill = GridBagConstraints.HORIZONTAL; // Prevents expansion
        readyGbc.anchor = GridBagConstraints.CENTER; // Center the button
        readyGbc.insets = new Insets(10, 200, 10, 200); // Add some padding

        GridBagConstraints paddgbc = new GridBagConstraints();
        paddgbc.gridheight = GridBagConstraints.REMAINDER;
        paddgbc.fill = GridBagConstraints.HORIZONTAL;
        paddgbc.weighty = 2;

        add(backButton, backgbc);
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.gridwidth = 2; // Span across two columns
        add(title, gbc);
        add(title, gbc);
        add(Box.createVerticalStrut(10), paddgbc);
        gbc.gridx = 0; // First column
        gbc.gridy = 1; // Second row
        gbc.gridheight = 1; // Takes up one row
        gbc.gridwidth = 1; // Takes up one column
        add(chatPanel, gbc);
        gbc.gridx = 0; // First column
        gbc.gridy = 2; // Second row
        gbc.gridheight = 1; // Takes up one row
        gbc.gridwidth = 1; // Takes up one column
        gbc.weighty=0.1;
        add(chatField, gbc);
        gbc.gridx = 1; // Second column, to the right of chatPanel
        gbc.gridy = 1; // Same row as chatPanel
        gbc.gridheight = 2; // Takes up one row
        gbc.gridwidth = 1; // Takes up one column
        add(playerPanel, gbc);
        add(Box.createVerticalStrut(20), paddgbc);
        add(startButton, readyGbc);
        readyGbc.gridx = 1;
        readyGbc.gridy = 3;
        add(readyButton, readyGbc);
        add(Box.createVerticalStrut(30), paddgbc);

    }

    void remakePlayerTable() {
        initLobby();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(102, 178, 255));
        g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());

        // drawPlayerPanel(g);
    }

	public void playerJoin() throws InterruptedException {
		// Add player to the JSpace lobby
		lobbySpace.put(client.getName(), false); // False indicates the player is not ready
		updatePlayerList();
	}


	private void updatePlayerList() throws InterruptedException {
		// Query all players from the lobby space
		List<Object[]> allPlayers = lobbySpace.queryAll(new FormalField(String.class), new FormalField(Boolean.class));
		
		// Clear the existing player list
		players2.clear();
	
		// Add each player to the list
		for (Object[] playerInfo : allPlayers) {
			String playerName = (String) playerInfo[0];
			client.setName(playerName);
			players2.add(playerName);
		}
	
		// Update the UI with the new list
		updatePlayerTable();
	}
	
	private void updatePlayerTable() {
		DefaultTableModel tableModel = (DefaultTableModel) playerTable.getModel();
		tableModel.setRowCount(0); // Clear existing table rows
	
		// Add new rows for each player
		for (String playerName : players2) {
			tableModel.addRow(new Object[]{ playerName });
		}
	}
		
	

	/*public void playerReady(String playerName) throws InterruptedException {
		RemoteSpace lobbySpace = new RemoteSpace("tcp://" + getHostAddress() + ":9001/lobby?keep");

		lobbySpace.get(new ActualField(playerName), new ActualField(playerReady));
		playerReady = true;
		lobbySpace.put(playerName, playerReady);
	}*/

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