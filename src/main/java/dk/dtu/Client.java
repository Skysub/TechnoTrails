package dk.dtu;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {
	
	ViewManager viewManager;
	GameState gameState;
	Space chatSpace;
	private String myName = "unset";
	public String hostAddress = "localhost";
	public Server server;
	private boolean isHost = false;
	private int myID = -1;
	Space lobbySpace;
	LobbyClient lobbyClient;
	Thread lobbyClientThread;
	

	ServerInfo serverInfo;

	Client(ViewManager viewManager) {
		this.viewManager = viewManager;
		setName(RandomWords.getRandomWord());
		serverInfo = new ServerInfo();
		serverInfo.playerList = new HashMap<Integer, String>();
	}

	public void CreateLobby(String hAddress) {
		this.hostAddress = hAddress;
		server = new Server();
		server.createLobby(); // This starts the server and initializes the lobby
		isHost = true;
		joinLobby(hostAddress);
	}

	public void KillLobby() {
		if (isHost) {
			LeaveLobby();

			server.kill();
			server = null;
			isHost = false;
		}
	}

	public void joinLobby(String hostAddress) {
		try {
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
			
			//response = lobbySpace.query(new FormalField(ServerInfo.class));
			//setNewServerInfo((ServerInfo)response[0]);
			
			System.out.println("You have joined the lobby");
		} catch (Exception e) {
			System.out.println("Error Joining lobby with ip: " + hostAddress);
		}

		// Enter loop querying the lobby
		lobbyClient = new LobbyClient(lobbySpace, myID, this);
		lobbyClientThread = new Thread(lobbyClient);
		lobbyClientThread.start();
	}

	public void LeaveLobby() {

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
}
