package dk.dtu;

import org.jspace.Space;

//Handles the lobby functionality of the server
public class LobbyServer implements Runnable {
	
	Space lobbySpace;
	ServerInfo info;

	LobbyServer (Space lobbySpace, ServerInfo info){
		this.lobbySpace = lobbySpace;
		this.info = info;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
