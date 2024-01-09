package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class Lobby extends JPanel {

	private boolean playerReady;
	public int numberOfPlayers;

	SequentialSpace lobbySpace = new SequentialSpace();
	public JScrollPane playerPanel;
	public JFrame playerLabel;
	ArrayList<String> players2;
	JTable table;

	JButton backButton = new JButton("<-");
	ViewManager viewManager;
	Client client;

	public Lobby(ViewManager viewManager, Client client) {
		this.viewManager = viewManager;
		this.client = client;
		setBounds(viewManager.getBounds());
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

		// Players added to list for testing
		players2 = new ArrayList<String>();
		for (int i = 1; i < 26; i++) {
			players2.add(client.getName() + i);
		}

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