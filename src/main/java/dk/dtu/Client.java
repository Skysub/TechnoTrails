package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Client{
	
	static final int defaultTickRate = 60;
	
	Boolean host = false; //Is this player also a host?
	Server server = null; //The server object we're hosting
	
	private String myName = "unset";
	
	Client(){
		setName(RandomWords.getRandomWord());
	}
	
	//Used when you're the host
	void CreateLobby() {
		host = true;
		ServerInfo si = new ServerInfo();
		si.tps = defaultTickRate;
		si.playerList = new ArrayList<ImmutablePair<Integer, String>>();
		si.addPlayer(getName());
		server = new Server();
	}
	
	//Closing the lobby as well as closing the game when you're the host
	void CloseLobby() {
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