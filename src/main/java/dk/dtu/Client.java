package dk.dtu;

import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {

	GameState gameState;
	Space chatSpace;
	private String myName = "unset";
	public String hostAddress = "localhost";
	public Server server;

	Client() {
		setName(RandomWords.getRandomWord());
	}

	public void joinLobby(String hostAddress) throws Exception {
		try {
			Space lobbySpace = new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep");
			chatSpace = new RemoteSpace("tcp://" + hostAddress + ":9001/chat?keep");
			lobbySpace.put(getName(), false);
			System.out.println("You have joined the lobby");
		} catch (Exception e) {
			// Re-throw the exception to be handled by the caller
			throw e;
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

	public Space getChatSpace() {
		return server.getChatSpace();
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
}
