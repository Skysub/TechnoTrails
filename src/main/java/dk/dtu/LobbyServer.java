package dk.dtu;

import org.jspace.FormalField;
import org.jspace.Space;

//Handles the lobby functionality of the server
public class LobbyServer implements Runnable {
	
	Space lobbySpace;
	Server server;
	ServerInfo info;


	LobbyServer (Space lobbySpace, ServerInfo info, Server server){
		this.lobbySpace = lobbySpace;
		this.info = info;
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			lobbySpace.put(LobbyToClientMessage.LobbyStart);
		} catch (InterruptedException e) {
			System.out.println("Error in LobbyServer when starting up");
			e.printStackTrace();
		}
		
		while (true) {
			Object[] response;
			try {
				//2
				response = lobbySpace.get(new FormalField(Integer.class), new FormalField(ClientToLobbyMessage.class));
			} catch (InterruptedException e) {
				System.out.println("Error in LobbyServer when reading the lobbySpace");
				e.printStackTrace();
				continue;
			}

			int ID = (int) response[0];
			// Now to handle the lobbymessage
			try {
				switch ((ClientToLobbyMessage) response[1]) {
				case ClientJoin:
					server.addPlayer(ID);
					break;

				case ClientDisconnect:

					break;

				case ClientToggleReady:

					break;

				default:
					System.out.println(
							"LobbyServer hasn't implemented a response for the ClientToLobbyMessage: " + (ClientToLobbyMessage) response[1]);
					break;
				}
			} catch (Exception e) {
				System.out.println("Error in LobbyServer when handling a ClientToLobbyMessage: " + (ClientToLobbyMessage) response[1]);
			}
		}
	}
}
