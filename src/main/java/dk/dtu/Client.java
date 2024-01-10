package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Client{
	
	static final int defaultTickRate = 60;
	
	Boolean host = false; //Is this player also a host?
	Server server = null; //The server object we're hosting
	private String myName = "unset";
	
	Client(){
		setName(RandomWords.getRandomWord());
	}


	public void joinLobby(String hostAddress) {
		try {
			// Connect to the remote lobby space hosted by the server
			Space lobbySpace = new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep");
			lobbySpace.put(getName(), false); // Add the client to the lobby space
			System.out.println("You have joined the lobby");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//Used when you're the host
	public void CreateLobby() {
		host = true;
		ServerInfo si = new ServerInfo();
		si.tps = defaultTickRate;
		si.playerList = new ArrayList<ImmutablePair<Integer, String>>();
		si.addPlayer(getName());
	
		SpaceRepository repository = new SpaceRepository();
		repository.addGate("tcp://localhost:9001/?keep"); // Host's IP address and port
		Space lobbySpace = new SequentialSpace();
		repository.add("lobby", lobbySpace);
	
		try {
			lobbySpace.put(getName(), false); // Add the host to the lobby space
			System.out.println("You have created and joined the lobby");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		server = new Server();
	}
	
	
	//Closing the lobby as well as closing the game when you're the host
	public void CloseLobby() {
		host = false;
		server.kill();
		server = null;
	}

	//===============================================
	//Getters and setters

	public String getName() {
		return myName;
	}

	public void setName(String myName) {
		this.myName = myName;
	}
}