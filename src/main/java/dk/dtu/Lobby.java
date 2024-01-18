package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class Lobby extends JPanel implements View {

	public int numberOfPlayers;

	public SpaceRepository repository;
	public Space lobbySpace;
	public int greenRowIndex;

	private Timer chatUpdateTimer;

	public JScrollPane playerPanel;
	public JScrollPane chatPanel;
	public JFrame playerLabel;
	private JTextField chatField;
	DefaultTableModel chatModel;
	DefaultTableModel tableModel;
	ArrayList<String> players2;
	ArrayList<String> chat;
	JTable playerTable;
	JTable chatTable;

	JButton backButton = new JButton("<-");;
	JButton readyButton = new JButton("Not Ready");
	JButton startButton = new JButton("Not all players ready");
	ViewManager viewManager;
	Client client;

	public Lobby(ViewManager viewManager, Client client) {
		this.viewManager = viewManager;
		this.client = client;
		setBounds(viewManager.getBounds());
		this.repository = new SpaceRepository();
		this.lobbySpace = new SequentialSpace();
		this.repository.add("lobby", this.lobbySpace);
		players2 = new ArrayList<String>();
		chatModel = getClientChatModel();

	}

	public void initPlayerTable(ServerInfo info) {
		// The table showing the players
		String[] TABLE_COLUMNS = { "Players" };
		tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
			private static final long serialVersionUID = -4371585539792034587L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		for (PlayerServerInfo player : info.playerList.values()) {
			String readyString = "Ready - ";
			if (!player.ready) {
				readyString = "Not ready - ";
			}
			tableModel.addRow(new String[] { readyString + player.name });
		}
		playerTable = new JTable(tableModel);
		playerTable.setRowHeight(20);
	}

	public void initLobby() {
		removeAll();
		initPlayerTable(client.getServerInfo());
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.ipady = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 10, 0, 10);

		JLabel title = new JLabel("IP: " + client.getHostAddress());
		title.setFont(new Font("Serif", Font.BOLD, 40));
		title.setForeground(new Color(0, 76, 153));
		title.setHorizontalAlignment(0);

		String[] CHAT_COLUMNS = { "Chat" };
		chatModel = new DefaultTableModel(CHAT_COLUMNS, 0) {
			private static final long serialVersionUID = -1892645556686553938L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};

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
		chatField.setFocusable(true);
		if (chatField.getKeyListeners().length == 0)
			chatField.addKeyListener(new MyKeyAdapter());

		backButton.setPreferredSize(new Dimension(150, 50));
		backButton.setForeground(Color.blue);
		if (backButton.getActionListeners().length == 0) {
			backButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (client.getIsHost()) {
						System.out.println("Host pressed closed lobby");
						client.KillLobby();
					} else {
						System.out.println("Non host pressed leave lobby");
						client.AttemptDisconnect();
					}
				}
			});
		}

		GridBagConstraints backgbc = new GridBagConstraints();
		backgbc.gridwidth = GridBagConstraints.REMAINDER;
		backgbc.fill = GridBagConstraints.NONE;
		backgbc.anchor = GridBagConstraints.FIRST_LINE_START;
		backgbc.weightx = 1;
		backgbc.weighty = 1;
		backgbc.insets = new Insets(20, 20, 20, 20);

		readyButton = new JButton("Not ready");
		readyButton.setPreferredSize(new Dimension(50, 50));
		readyButton.setForeground(Color.blue);
		if (readyButton.getActionListeners().length == 0) {
			readyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Readybutton pressed");
					client.ToggleReady();
				}
			});
		}

		startButton.setPreferredSize(new Dimension(50, 50));
		startButton.setForeground(Color.blue);
		startButton.setToolTipText("Only host can start game when all players are ready");
		if (startButton.getActionListeners().length == 0) {
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Startbutton pressed");

					client.HostStartGame();
					// change game view for all players
					// server.changeView("gameView");

					// no ^
					// The clients should change the view themselves after receiving a
					// LobbyToClientMessage
					// The server shouldn't be telling the clients what exact view to be on
					// It bloats the serverInfo object unnecessarily

				}
			});
		}

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
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(title, gbc);
		add(Box.createVerticalStrut(10), paddgbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		add(chatPanel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 0.1;
		add(chatField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.gridwidth = 1;
		add(playerPanel, gbc);
		add(Box.createVerticalStrut(20), paddgbc);
		add(startButton, readyGbc);
		readyGbc.gridx = 1;
		readyGbc.gridy = 3;
		add(readyButton, readyGbc);
		add(Box.createVerticalStrut(30), paddgbc);
		revalidate();
	}

	// Called when the view is changed to lobby
	public void whenEntering() {
		if (client.getIsHost()) {
			backButton.setText("Close Lobby");
		} else {
			backButton.setText("Leave Lobby");
		}
	}

	public void whenExiting() {
	

	}

	public void clientRequestedUpdate() {
		initLobby();
		updateChatModel();
		if (client.getServerInfo().playerList.get(client.getMyID()).ready) {
			readyButton.setText("Ready");
		} else {
			readyButton.setText("Not Ready");
		}

		if (client.IsEveryoneReady()) {
			if (client.getIsHost()) {
				startButton.setText("Start Game");
			} else {
				startButton.setText("Waiting for host to start");
			}
		} else {
			startButton.setText("Not everyone is ready");
		}
		startButton.setEnabled(client.getIsHost() && client.IsEveryoneReady());
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(new Color(102, 178, 255));
		g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());
	}

	String message;

	public class MyKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				message = chatField.getText();
				if(message.isEmpty()){
					return;
				}
				chatField.setText("");
				try {
					client.getClientChatSpace().put(client.getName(), message);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public DefaultTableModel getClientChatModel() {
		return client.chatModel;
	}

// Create a method to periodically check for new chat messages and update the chat model
public void updateChatModel() {
    // Check if the current view is the lobby before updating the chat model
    if (viewManager.getCurrentView().equals("lobby")) {
        if (chatUpdateTimer == null) {
            chatUpdateTimer = new Timer(10, new ActionListener() { // Adjust the interval as needed
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Query for new chat messages
                        List<Object[]> chatMessages = client.getClientChatSpace().queryAll(new FormalField(String.class),
                                new FormalField(String.class));

                        // Update the chat model with new messages
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // Update the model only with new messages
                                if (chatMessages.size() != chatModel.getRowCount()) {
                                    for (int i = chatModel.getRowCount(); i < chatMessages.size(); i++) {
                                        Object[] chatm = chatMessages.get(i);
                                        chatModel.addRow(new Object[] { chatm[0] + ": " + chatm[1] });
                                    }
                                }
                            }
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            chatUpdateTimer.setRepeats(true);
            chatUpdateTimer.start();
        }
    } else {
        // If not in the lobby, stop the chat update timer
        if (chatUpdateTimer != null) {
            chatUpdateTimer.stop();
            chatUpdateTimer = null;
        }
    }
}



}