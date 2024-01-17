package dk.dtu;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {

	ViewManager viewManager;
	Space gameSpace;

	GameState gameState;
	

	Space chatSpace;
	ChatClient chatClient;
	Thread chatThread;

	private String myName = "unset";
	public String hostAddress = "10.212.135.113";
	public Server server;
	private boolean isHost = false;
	private boolean disconnecting = false;
	private int myID = -1;

	Space lobbySpace;
	LobbyClient lobbyClient;
	Thread lobbyClientThread;
	DefaultTableModel chatModel;

	ServerInfo serverInfo;

	Client(ViewManager viewManager) {
		this.viewManager = viewManager;
		setName(RandomWords.getRandomWord());
		serverInfo = new ServerInfo();
		serverInfo.playerList = new HashMap<Integer, PlayerServerInfo>();
	}

	public boolean CreateLobby(String hAddress) {
		this.hostAddress = hAddress;


		// check if a lobby already exists here
		try {
			new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep").close();
			new RemoteSpace("tcp://" + hostAddress + ":9001/chat?keep").close();

			System.out.println("A lobby already exists at the adress: " + hostAddress);
			return false; // A lobbyspace is already open here
		} catch (UnknownHostException e) {
			System.out.println("host bad");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// We may continue
		}

		isHost = true;
		server = new Server();
		server.createLobby(); // This starts the server and initializes the lobby
		try {
			joinLobby(hostAddress);
		} catch (IOException | InterruptedException e) {
			System.out.println("Could not join fresh lobby");
			e.printStackTrace();
		}
		return true;
	}

	public void KillLobby() {
		server.kill();
		server = null;
		isHost = false;
	}

	public boolean joinLobby(String hostAddress) throws UnknownHostException, IOException, InterruptedException {
		lobbySpace = new RemoteSpace("tcp://" + hostAddress + ":9001/lobby?keep");
		chatSpace = new RemoteSpace("tcp://" + hostAddress + ":9001/chat?keep");

		// 1
		int randomInt = ThreadLocalRandom.current().nextInt(1000001, 1000000001); // 1 million to 1 billion
		lobbySpace.put(randomInt, ClientToLobbyMessage.ClientJoin);

		// 4
		Object[] response = lobbySpace.get(new FormalField(Integer.class), new ActualField(randomInt));
		myID = (int) response[0];

		// 5
		lobbySpace.put(myID, myName);

		// response = lobbySpace.query(new FormalField(ServerInfo.class));
		// setNewServerInfo((ServerInfo)response[0]);

		System.out.println("You have joined the lobby");

		// Enter loop querying the lobby
		lobbyClient = new LobbyClient(lobbySpace, myID, this);
		lobbyClientThread = new Thread(lobbyClient);
		lobbyClientThread.start();

		initializeChatClient();
		return true;
	}

	public void initializeChatClient() {
		chatClient = new ChatClient(chatSpace, this, myID, chatModel);
		chatThread = new Thread(chatClient);
		chatThread.start();
	}

	public void AttemptDisconnect() {
		if (!disconnecting) {
			System.out.println("Attempting to disconnect");
			try {
				if (lobbySpace != null) {
					lobbySpace.put(myID, ClientToLobbyMessage.ClientDisconnect);
					disconnecting = true;
				} else {
					System.out.println("lobbySpace was null when attempting to disconnect");
				}
			} catch (InterruptedException e) {
				System.out.println("Error when attempting to disconnect");
				e.printStackTrace();
			}
		} else {
			System.out.println("Already attempting to disconnect");
		}
	}

	public boolean FinalizeDisconnect() {
		try {
			System.out.print("Finalizing disconnection, sending (" + myID + ", ClientDone) ...");
			lobbySpace.put(myID, ClientToLobbyMessage.ClientDone);
			System.out.println("sent");
		} catch (InterruptedException e) {
			System.out.println("Error when finalizing a disconnect");
			e.printStackTrace();
		}
		DisconnectFromGame();
		gameSpace = null;
		chatSpace = null;
		lobbySpace = null;
		myID = -1;
		serverInfo = new ServerInfo();
		serverInfo.playerList = new HashMap<Integer, PlayerServerInfo>();
		hostAddress = getHostAddress();
		lobbyClientThread = null;
		lobbyClient = null;
		disconnecting = false;

		viewManager.changeView("menu");
		return true;
	}
	
	public void InitializeGaming() {
		try {
			gameSpace = new RemoteSpace("tcp://" + hostAddress + ":9001/game?keep");		
		} catch (IOException e) {
			System.out.println("Error when trying to connect to the gameSpace");
			e.printStackTrace();
		}
		
		//Initialize input stuff here maybe add the gameControls class to gameView?
		
		try {
			gameState = (GameState) gameSpace.query(new FormalField(GameState.class))[0];
			gameSpace.put(myID, "Gaming initialized");
			
		} catch (InterruptedException e) {
			System.out.println("Error getting the gameState when initalizing Gaming");
			e.printStackTrace();
		}		
	}

	public void GameUpdate() {
		Object[] response;
		try {
			response = gameSpace.query(new FormalField(GameUpdate.class)); //get the gameUpdate
			
			float tickDiff = ((GameUpdate) response[0]).tick - (1 + gameState.tick); //Is the tick what we expect?
			if (tickDiff == 0) {
				Game.UpdateGameState(gameState, (GameUpdate) response[0]);
			} else {
				//We request the full gameState
				PlayerInput out = new PlayerInput();
				out.id = myID;
				out.playerActions = new ArrayList<ImmutablePair<PlayerAction, Float>>();
				out.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.RequestFullGamestate, tickDiff));
				gameSpace.put(out);
				gameSpace.get(new ActualField("New_game_state_put"));
				
				response = gameSpace.query(new FormalField(GameState.class)); //We query for the actual gamestate
				setNewGameState((GameState) response[0]);
			}
		} catch (InterruptedException e) {
			System.out.println("Client error while updating gameState");
			e.printStackTrace();
			return;
		}
		sendPlayerActions();
	}
	
	public void GameIsOver() {
		try {
			setNewGameState((GameState) gameSpace.query(new FormalField(GameState.class))[0]);
		} catch (InterruptedException e) {
			System.out.println("Error when querying for the gameState after game is won");
			e.printStackTrace();
		}
	}
	
	public void BackToLobby() {
		DisconnectFromGame();
		
		System.out.println("changing back to lobby");
		viewManager.changeView("lobby");
		ToggleReady();
	}

	private void DisconnectFromGame() {
		PlayerInput out = new PlayerInput();
		out.id = myID;
		out.playerActions = new ArrayList<ImmutablePair<PlayerAction, Float>>();
		out.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.Disconnect, 0f));
		try {
			gameSpace.put(out);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//gameState = null;
	}

	//This method updates all references to the client gameState
	private void setNewGameState(GameState newState) {
		this.gameState = newState;		
	}

	public void ToggleReady() {
		try {
			lobbySpace.put(myID, ClientToLobbyMessage.ClientToggleReady);
		} catch (InterruptedException e) {
			System.out.println("Error when toggling ready");
			e.printStackTrace();
		}
	}

	public boolean IsEveryoneReady() {
		for (PlayerServerInfo player : serverInfo.playerList.values()) {
			if (!player.ready)
				return false;
		}
		return true;
	}
	
	public void HostStartGame() {
		server.StartGame();
	}

	public void setNewServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		viewManager.updateView();
	}

	public void sendPlayerActions() {
		GameControls gameControls = ((GameView)viewManager.getView("gameView")).getGameControls();
		boolean left = gameControls.getLeft();
		boolean right = gameControls.getRight();
		
		PlayerInput out = new PlayerInput();
		out.id = myID;
		out.playerActions = new ArrayList<ImmutablePair<PlayerAction, Float>>();
		
		if(left) {
			float newAngle = gameState.players.get(myID).rotation - (gameState.deltaTime * GamePlay.TURNING_SPEED);
			out.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.Turn, newAngle));
		}
		
		if(right) {
			float newAngle = gameState.players.get(myID).rotation + (gameState.deltaTime * GamePlay.TURNING_SPEED);
			out.playerActions.add(new ImmutablePair<PlayerAction, Float>(PlayerAction.Turn, newAngle));
		}
		
		try {
			gameSpace.put(out);
		} catch (InterruptedException e) {
			System.out.println("Error when sending game inputs");
			e.printStackTrace();
		}
	}



	// ==========================================
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

	public String getClientMessage() {
		return server.getChatMessage();
	}

	public boolean getIsHost() {
		return isHost;
	}

	public ServerInfo getServerInfo() {
		return this.serverInfo;
	}

	public int getMyID() {
		return myID;
	}
}