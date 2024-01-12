package dk.dtu;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class LobbyClient implements Runnable {

	Space lobbySpace;
	int myID;
	Client client;

	LobbyClient(Space lobbySpace, int myID, Client client) {
		this.lobbySpace = lobbySpace;
		this.myID = myID;
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			Object[] response;
			try {
				// 5 in server-client lobby update, 3 in host kills server, 3 in lobby signals
				// gamestart
				response = lobbySpace.get(new ActualField(myID), new FormalField(LobbyToClientMessage.class));
			} catch (InterruptedException e) {
				System.out.println("Error in LobbyClient when reading the lobbySpace");
				e.printStackTrace();
				continue;
			}

			// Now to handle the lobbymessage
			try {
				switch ((LobbyToClientMessage) response[1]) {
					case LobbyUpdate:
						// 6
						client.setNewServerInfo((ServerInfo) lobbySpace.query(new FormalField(ServerInfo.class))[0]);
						break;

					case LobbyGameStart:

						break;

					case LobbyShutdown:
						client.AttemptDisconnect();
						break;
						
					case LobbySaysGoodbye:
						if(client.FinalizeDisconnect()) return;
						break;

					default:
						System.out.println(
								"Client hasn't implemented a response for the LobbyToClientMessage: "
										+ (LobbyToClientMessage) response[1]);
						break;
				}
			} catch (InterruptedException e) {
				System.out.println("Error in LobbyClient when handling a LobbyToClientMessage: " + (LobbyToClientMessage) response[1]);
			}
		}
	}
}
