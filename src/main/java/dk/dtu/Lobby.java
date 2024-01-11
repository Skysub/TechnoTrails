package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import dk.dtu.Lobby.MyKeyAdapter;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;

public class Lobby extends JPanel implements View {

    private boolean playerReady;
    public int numberOfPlayers;

    public SpaceRepository repository;
    public Space lobbySpace;
    public int greenRowIndex;

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

    JButton backButton = new JButton("<-");
    JButton readyButton = new JButton("Ready");
    JButton startButton = new JButton("Start Game");
    ViewManager viewManager;
    Client client;
    Server server;

    public Lobby(ViewManager viewManager, Client client) {
        this.viewManager = viewManager;
        this.client = client;
        setBounds(viewManager.getBounds());
        this.repository = new SpaceRepository();
        this.lobbySpace = new SequentialSpace();
        this.repository.add("lobby", this.lobbySpace);
        players2 = new ArrayList<String>();
        

        
    }


    public void initPlayerTable(){
        
        try {
            playerJoin(); // Call this when the Lobby view is initialized
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
               // The table showing the players
        String[] TABLE_COLUMNS = { "Players" };
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }
        };
        for (int i = 0; i < players2.size(); i++) {

            tableModel.addRow(new String[] { players2.get(i) + client.getName() });
        }
        playerTable = new JTable(tableModel);
        playerTable.setRowHeight(20);
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

 
        String[] CHAT_COLUMNS = { "Chat" };
        chatModel = new DefaultTableModel(CHAT_COLUMNS, 0) {
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
        chatField.addKeyListener(new MyKeyAdapter());

        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setForeground(Color.blue);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
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

    }
    
	//Called when the view is changed to lobby
	public void whenEntering() {
		initPlayerTable();
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
            players2.add(playerName);
        }

        // Update the UI with the new list
        updatePlayerTable();
    }

    private void updatePlayerTable() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (playerTable != null) {
                    DefaultTableModel tableModel = (DefaultTableModel) playerTable.getModel();
                    tableModel.setRowCount(0);

                    for (String playerName : players2) {
                        tableModel.addRow(new Object[] { playerName });
                    }
                }
            }
        });
    }

    /*
     * public void playerReady(String playerName) throws InterruptedException {
     * RemoteSpace lobbySpace = new RemoteSpace("tcp://" + getHostAddress() +
     * ":9001/lobby?keep");
     * 
     * lobbySpace.get(new ActualField(playerName), new ActualField(playerReady));
     * playerReady = true;
     * lobbySpace.put(playerName, playerReady);
     * }
     */

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

    String message;

    public class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                message = chatField.getText();
                chatField.setText("");
                try {
                    client.getChatSpace().put(client.getName(), message);
                    //System.out.println(client.getClientMessage());
                    chatModel.addRow(new Object[]  {client.getClientMessage()});
                    System.out.println("client.getClientMessage()");
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }
}