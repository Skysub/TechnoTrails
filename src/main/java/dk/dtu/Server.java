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
	ChatServer chatServer;
	Thread chatThread;
	SpaceRepository repository;
	boolean shuttingDown = false;

	Server() {
		info = new ServerInfo();
		info.tps = defaultTickRate;
		info.playerList = new HashMap<Integer, PlayerServerInfo>();
	}

	public boolean createLobby() {
		repository = new SpaceRepository();
		repository.addGate("tcp://" + hostAddress + ":9001/?keep");
		lobbySpace = new SequentialSpace();
		repository.add("lobby", lobbySpace);
		chatSpace = new SequentialSpace();
		repository.add("chat", chatSpace);

		lobbyServer = new LobbyServer(lobbySpace, info, this);
		lobbyThread = new Thread(lobbyServer);
		lobbyThread.start();

		chatServer = new ChatServer(chatSpace, info, this);
		chatThread = new Thread(chatServer);
		chatThread.start();

		try {
			lobbySpace.get(new ActualField(LobbyToClientMessage.LobbyStart));
			// setNewServerInfo(info);
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
			// 3
			lastID++;
			lobbySpace.put(lastID, identifier);
			// 6
			newName = (String) lobbySpace.get(new ActualField(lastID), new FormalField(String.class))[1];
		} catch (InterruptedException e) {
			System.out.println("Error adding a player to lobby");
			e.printStackTrace();
			return;
		}
		info.playerList.put(lastID, new PlayerServerInfo(newName));
		// 7
		ServerClientLobbyUpdate();
	}

	public boolean DropClient(int ID) {
		try {
			System.out.print("Saying goodbye to ID:" + ID + " ... ");
			lobbySpace.put(ID, LobbyToClientMessage.LobbySaysGoodbye);
			System.out.println("sent");
			System.out.print("Waiting for ClientDone from ID:" + ID + " ... ");
			lobbySpace.get(new ActualField(ID), new ActualField(ClientToLobbyMessage.ClientDone));
			System.out.println("recieved");
		} catch (InterruptedException e) {
			System.out.println("Error when saying goodbye to a client");
			e.printStackTrace();
		}

		info.playerList.remove(ID);
		if (!shuttingDown)
			ServerClientLobbyUpdate();
		return (shuttingDown && info.playerList.isEmpty());
	}

	void kill() {
		System.out.println("Server shutting down");
		shuttingDown = true;
		try {
			for (int id : info.playerList.keySet()) {
				lobbySpace.put(id, LobbyToClientMessage.LobbyShutdown);
			}
			lobbySpace.get(new ActualField(LobbyToClientMessage.Done));
		} catch (InterruptedException e) {
			System.out.println("Error when shutting down the lobby");
			e.printStackTrace();
		}

		// Waiting for 100 ms and then shutting down the repository of spaces
		System.out.print("Shutting down the spaceRepo ... ");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("Attempted to sleep before shutting down repo, but was interrupted");
			e.printStackTrace();
		}
		repository.shutDown();
		System.out.println("done");
	}

	public void setNewServerInfo(ServerInfo serverInfo) {
		this.info = serverInfo;
		ServerClientLobbyUpdate();
	}

	public void TogglePlayerReady(int ID) {
		boolean pReady = info.playerList.get(ID).ready;
		info.playerList.get(ID).ready = !pReady;
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
				lobbySpace.put(id, LobbyToClientMessage.LobbyUpdate);
			}

		} catch (InterruptedException e) {
			System.out.println("Error when updating server info");
			e.printStackTrace();
		}
	}

	public void printPlayers() {
		// print list of names and id's
		for (Entry<Integer, PlayerServerInfo> i : info.playerList.entrySet()) {
			System.out.println("ID " + i.getKey() + " : " + i.getValue().name);
		}
	}

	ServerInfo getInfo() {
		return info;
	}

	// change view for all clients
	public void changeView(String view) {
		try {
			// 1
			lobbySpace.getp(new FormalField(ServerInfo.class));

			// 2
			info.setView(view);

			// 3
			for (int id : info.playerList.keySet()) {
				// 4
				lobbySpace.put(id, LobbyToClientMessage.LobbyUpdate);
			}

		} catch (InterruptedException e) {
			System.out.println("Error when updating server info");
			e.printStackTrace();
		}
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
