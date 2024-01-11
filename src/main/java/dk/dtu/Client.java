package dk.dtu;

import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {

	GameState gameState;
	Space chatSpace;
	private String myName = "unset";
	public String hostAddress = "localhost";
	public Server server;
	Boolean client = false;

	Client() {
		setName(RandomWords.getRandomWord());
	}

	public void joinLobby(String hostAddress) {
		try {
			client = true;
			Space lobbySpace = new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep");
			chatSpace = new RemoteSpace("tcp://" + hostAddress + ":9001/chat?keep");
			lobbySpace.put(getName(), false);
			System.out.println("You have joined the lobby");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	public void setServer(Server server) {
		this.server = server;
	}

	public String getClientMessage() {
		return server.getChatMessage();
	}
	public boolean getCheckClient(){

		return client;
	}

	
}
