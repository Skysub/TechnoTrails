package dk.dtu;

import java.awt.*;
import javax.swing.*;
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
	public JFrame playerLabel;
	ArrayList<String> players2;
	JTable table;

	JButton backButton = new JButton("<-");
	ViewManager viewManager;
	Client client;
	Menu menu;

	public Lobby(ViewManager viewManager, Client client, Menu menu) {
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
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1;

		JLabel title = new JLabel("Port: " + 12345);
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
		table = new JTable(tableModel);
		table.setRowHeight(20);

		playerPanel = new JScrollPane(table);
		playerPanel.setPreferredSize(new Dimension(400, 200));
		playerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		playerPanel.getViewport().setBackground(new Color(0, 76, 153));
		// playerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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

		GridBagConstraints paddgbc = new GridBagConstraints();
		paddgbc.gridheight = GridBagConstraints.REMAINDER;
		paddgbc.fill = GridBagConstraints.HORIZONTAL;
		paddgbc.weighty = 2;

		add(backButton, backgbc);
		add(title, gbc);
		add(Box.createVerticalStrut(10), paddgbc);
		add(playerPanel, gbc);
		add(Box.createVerticalStrut(20), paddgbc);
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

	public void playerJoin(String playerName) throws InterruptedException {
		// Add player to the JSpace lobby
		lobbySpace.put(playerName, false); // False indicates the player is not ready
	
		// Update the list of players and the lobby UI
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
			players2.add(playerName);
			System.out.println(players2);
		}
	
		// Update the UI with the new list
		updatePlayerTable();
	}
	private void updatePlayerTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
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