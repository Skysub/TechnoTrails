package dk.dtu;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;



public class Server {

	ServerInfo info;
	Space lobbySpace;
	Space chatSpace;
	Space gameSpace;
	static final int defaultTickRate = 30;
	String hostAddress = "";
	int lastID = 0;

	LobbyServer lobbyServer;
	Thread lobbyThread;
	GameServer gameServer;
	Thread gameThread;
	GameInputServer gameInputServer;
	Thread inputThread;
	Thread chatThread;
	SpaceRepository repository;
	boolean shuttingDown = false;

	Game game;

	Server() {
		info = new ServerInfo();
		info.tps = defaultTickRate;
		info.playerList = new HashMap<Integer, PlayerServerInfo>();
	}

	public boolean createLobby() {

		 this.hostAddress = getLocalIP();
		
		repository = new SpaceRepository();
		repository.addGate("tcp://" + hostAddress + ":9001/?keep");
		lobbySpace = new SequentialSpace();
		repository.add("lobby", lobbySpace);
		chatSpace = new SequentialSpace();
		repository.add("chat", chatSpace);

		lobbyServer = new LobbyServer(lobbySpace, info, this);
		lobbyThread = new Thread(lobbyServer);
		lobbyThread.start();


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

	public String getLocalIP() {
		String ip = "";
		try {
            // Get all network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Get all IP addresses associated with the network interface
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Check if it's an IPv4 address and not loopback address
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(".")) {
						ip = inetAddress.getHostAddress();
                        //System.out.println("IPv4 Address: " + inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

	return ip;
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

		if (game != null)
			EndGame();

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

	public void StartGame() {
		gameSpace = new SequentialSpace();
		repository.add("game", gameSpace);

		game = new Game(info, gameSpace);

		GameState gs = game.StartGame();

		try {
			gameSpace.put(gs);

			gameInputServer = new GameInputServer(game, gameSpace);
			inputThread = new Thread(gameInputServer);
			inputThread.start();

			gameSpace.get(new ActualField("GameInputServerReady"));

			gameServer = new GameServer(game, gameSpace, lobbySpace, info.tps);
			gameThread = new Thread(gameServer);
			System.out.println("Starting the gameServerThread");
			gameThread.start();

			gameSpace.get(new ActualField("game server ready"));
		} catch (InterruptedException e) {
			System.out.println("Error when starting the game threads");
			e.printStackTrace();
		}

		// Finally we tell players that the game is beginning
		try {
			for (int id : info.playerList.keySet()) {
				lobbySpace.put(id, LobbyToClientMessage.LobbyGameStart);
				gameSpace.get(new ActualField(id), new ActualField("Gaming initialized"));
			}

			// Unpausing the game (gets processed when the countdown is over)
			PlayerInput unpause = new PlayerInput();
			unpause.playerActions = new ArrayList<ImmutablePair<PlayerAction, Float>>();
			unpause.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.HostUnPause, 0f));
			gameSpace.put(unpause);
		} catch (InterruptedException e) {
			System.out.println("Error when informing about game start info or starting the game");
			e.printStackTrace();
		}
	}

	public void EndGame() {
		PlayerInput out = new PlayerInput();
		out.playerActions = new ArrayList<ImmutablePair<PlayerAction, Float>>();
		out.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.Shutdown, 0f));
		try {
			gameSpace.put(out);
			gameSpace.get(new ActualField("InputServer Done"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		game = null;
		repository.remove("game");
	}
	
	public void IncrementScore(int ID) {
		if(ID == 0) return; //Nobody won
		info.playerList.get(ID).score++;
		ServerClientLobbyUpdate();
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

	public String getChatMessage() {

		try {
			Object[] t = chatSpace.getp(new FormalField(String.class), new FormalField(String.class));
			return t[0] + ": " + t[1];

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";

	}

	public Space getChatSpace() {
		return chatSpace;
	}
}
