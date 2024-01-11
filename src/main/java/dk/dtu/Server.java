package dk.dtu;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Server {

	ServerInfo info;
	Space lobbySpace;
	Space chatSpace;
	static final int defaultTickRate = 60;
	String hostAddress = "localhost";
	int lastID = 0;

	LobbyServer lobbyServer;
	Thread lobbyThread;

	Server() {
		info = new ServerInfo();
		info.tps = defaultTickRate;
		info.playerList = new HashMap<Integer, String>();
	}

	public boolean createLobby() {
		// addPlayer(hostName);
		SpaceRepository repository = new SpaceRepository();
		repository.addGate("tcp://" + hostAddress + ":9001/?keep");
		lobbySpace = new SequentialSpace();
		repository.add("lobby", lobbySpace);
		chatSpace = new SequentialSpace();
		repository.add("chat", chatSpace);

		lobbyServer = new LobbyServer(lobbySpace, info, this);
		lobbyThread = new Thread(lobbyServer);
		lobbyThread.start();

		try {
			lobbySpace.get(new ActualField(LobbyMessage.LobbyStart));
			setNewServerInfo(info);
		} catch (InterruptedException e) {
			System.out.println("Error when starting lobby");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// create list of names and id's
	public void addPlayer(int identifier) {
		String newName;
		try {
			//3
			lobbySpace.put(lastID++, identifier);
			//6
			newName = (String) lobbySpace.get(new ActualField(lastID), new FormalField(String.class))[1];
		} catch (InterruptedException e) {
			System.out.println("Error adding a player to lobby");
			e.printStackTrace();
			return;
		}
		info.playerList.put(lastID, newName);
		//7
		ServerClientLobbyUpdate();
	}

	void kill() {
		try {
			if (lobbySpace != null) {
				lobbySpace.put("shutdown");
			}
			if (chatSpace != null) {
				chatSpace.put("shutdown");
			}
			// Add any additional cleanup or shutdown procedures here
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lobbySpace = null;
			chatSpace = null;
			info = null;
		}
	}

	public void setNewServerInfo(ServerInfo serverInfo) {
		this.info = serverInfo;
		ServerClientLobbyUpdate();
	}

	public void ServerClientLobbyUpdate() {
		try {
			// 1
			lobbySpace.getp(new FormalField(ServerInfo.class));

			// 2
			lobbySpace.put(info);

			// 3
			for (int id : info.playerList.keySet()) {
				// 4
				lobbySpace.put(id, LobbyMessage.LobbyUpdate);
			}

		} catch (InterruptedException e) {
			System.out.println("Error when updating server info");
			e.printStackTrace();
		}
	}

	public void printPlayers() {
		// print list of names and id's
		for (Entry<Integer, String> i : info.playerList.entrySet()) {
			System.out.println("ID " + i.getKey() + " : " + i.getValue());
		}
	}

	ServerInfo getInfo() {
		return info;
	}

	public String getChatMessage() {

		try {
			Object[] t = chatSpace.getp(new FormalField(String.class), new FormalField(String.class));
			return t[0] + ": " + t[1];

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	public Space getChatSpace() {
		return chatSpace;
	}
}
