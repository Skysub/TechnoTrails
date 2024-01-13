package dk.dtu;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.table.DefaultTableModel;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {

	ViewManager viewManager;
	GameState gameState;
	
	Space chatSpace;
	ChatClient chatClient;
	Thread chatThread;
	
	private String myName = "unset";
	public String hostAddress = "localhost";
	public Server server;
	private boolean isHost = false;
	private boolean disconnecting = false;
	private int myID = -1;
	
	Space lobbySpace;
	LobbyClient lobbyClient;
	Thread lobbyClientThread;
	DefaultTableModel chatModel = new DefaultTableModel();

	ServerInfo serverInfo;

	Client(ViewManager viewManager) {
		this.viewManager = viewManager;
		setName(RandomWords.getRandomWord());
		serverInfo = new ServerInfo();
		serverInfo.playerList = new HashMap<Integer, PlayerServerInfo>();
	}

	public boolean CreateLobby(String hAddress) {
		this.hostAddress = hAddress;

		// check if a lobby already exists here
		try {
			new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep").close();

			System.out.println("A lobby already exists at the adress: " + hostAddress);
			return false; // A lobbyspace is already open here
		} catch (UnknownHostException e) {
			System.out.println("host bad");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// We may continue
		}

		isHost = true;
		server = new Server();
		server.createLobby(); // This starts the server and initializes the lobby
		try {
			joinLobby(hostAddress);
		} catch (IOException | InterruptedException e) {
			System.out.println("Could not join fresh lobby");
			e.printStackTrace();
		}
		return true;
	}

	public void KillLobby() {
		server.kill();
		server = null;
		isHost = false;
	}

	public boolean joinLobby(String hostAddress) throws UnknownHostException, IOException, InterruptedException {
		lobbySpace = new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep");
		chatSpace = new RemoteSpace("tcp://" + hostAddress + ":9001/chat?keep");

		// 1
		int randomInt = ThreadLocalRandom.current().nextInt(1000001, 1000000001); // 1 million to 1 billion
		lobbySpace.put(randomInt, ClientToLobbyMessage.ClientJoin);

		// 4
		Object[] response = lobbySpace.get(new FormalField(Integer.class), new ActualField(randomInt));
		myID = (int) response[0];

		// 5
		lobbySpace.put(myID, myName);

		// response = lobbySpace.query(new FormalField(ServerInfo.class));
		// setNewServerInfo((ServerInfo)response[0]);

		System.out.println("You have joined the lobby");

		// Enter loop querying the lobby
		lobbyClient = new LobbyClient(lobbySpace, myID, this);
		lobbyClientThread = new Thread(lobbyClient);
		lobbyClientThread.start();
		
		initializeChatClient();
		return true;
	}


	public void initializeChatClient() {
        chatClient = new ChatClient(chatSpace, this, myID, chatModel); 
        chatThread = new Thread(chatClient);
        chatThread.start();
    }

	public void AttemptDisconnect() {
		if (!disconnecting) {
			System.out.println("Attempting to disconnect");
			try {
				if (lobbySpace != null) {
					lobbySpace.put(myID, ClientToLobbyMessage.ClientDisconnect);
					disconnecting = true;
				} else {
					System.out.println("lobbySpace was null when attempting to disconnect");
				}
			} catch (InterruptedException e) {
				System.out.println("Error when attempting to disconnect");
				e.printStackTrace();
			}
		} else {
			System.out.println("Already attempting to disconnect");
		}
	}

	public boolean FinalizeDisconnect() {
		try {
			System.out.print("Finalizing disconnection, sending (" + myID + ", ClientDone) ...");
			lobbySpace.put(myID, ClientToLobbyMessage.ClientDone);
			System.out.println("sent");
		} catch (InterruptedException e) {
			System.out.println("Error when finalizing a disconnect");
			e.printStackTrace();
		}
		chatSpace = null;
		lobbySpace = null;
		// gameSpace = null;
		myID = -1;
		serverInfo = new ServerInfo();
		serverInfo.playerList = new HashMap<Integer, PlayerServerInfo>();
		hostAddress = "localhost";
		lobbyClientThread = null;
		lobbyClient = null;
		disconnecting = false;

		viewManager.changeView("menu");
		return true;
	}
	
	public void ToggleReady() {
		try {
			lobbySpace.put(myID, ClientToLobbyMessage.ClientToggleReady);
		} catch (InterruptedException e) {
			System.out.println("Error when toggling ready");
			e.printStackTrace();
		}
	}
	
	public boolean IsEveryoneReady() {
		for (PlayerServerInfo player : serverInfo.playerList.values()) {
			if(!player.ready) return false;
		}
		return true;
	}

	public void setNewServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		viewManager.updateView();
	}

	// ==========================================
	// Getters and setters

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getName() {
		return myName;
	}

	public void setName(String myName) {
		this.myName = myName;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Space getServerChatSpace() {
		return server.getChatSpace();
	}

	public Space getClientChatSpace() {
		return chatSpace;
	}

	public Object getServer() {
		return server;
	}

	public String getClientMessage() {
		return server.getChatMessage();
	}

	public boolean getIsHost() {
		return isHost;
	}

	public ServerInfo getServerInfo() {
		return this.serverInfo;
	}

	public int getMyID() {
		return myID;
	}
}
