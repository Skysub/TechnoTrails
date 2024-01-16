package dk.dtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.Space;

public class Game {

	private int tps; // ticks per second
	private GameState gameState;
	private ArrayList<PlayerInput> playerInput = new ArrayList<PlayerInput>();;
	private ServerInfo info;
	private Space gameSpace;

	Game(ServerInfo info, Space gameSpace) {
		this.tps = info.tps;
		this.info = info;
		this.gameSpace = gameSpace;
		gameState = CreateGameState(info.playerList);
	}

	// The game simulates a tick and handles all the game mechanics
	GameUpdate Tick() {
		GameUpdate update = new GameUpdate();
		update.tick = gameState.tick++;
		//System.out.println("Now processing tick " + update.tick);

		// If the game is paused, nothing about the game should be updated
		if (gameState.paused && !CheckForUnpause()) {
				UpdateGameState(gameState, update);
				return update;		
		}

		// Updates the gametime and calculates the difference to use in game calculation
		long timeNow = System.nanoTime();
		float deltaTime = (timeNow - gameState.gameTime) / 1000000000; // Difference in time in seconds
		update.gameTime = timeNow;
		// newState.gameTime = timeNow;

		// If the difference in time since the last update is more than 250ms then
		// update according to the tps not actual elapsed time
		// This ensures proper gameplay even if the server has a massive lag spike or if
		// the game has been paused
		if (deltaTime > 0.25d) {
			deltaTime = (1f / tps);
		}
		update.deltaTime = deltaTime;

		update.playerUpdate = new HashMap<Integer, PlayerInfo>();
		for (int k : gameState.players.keySet()) {
			PlayerInfo newPInfo = new PlayerInfo();
			newPInfo.id = k;
			newPInfo.trail = new ArrayList< ImmutablePair<Float, Float>>();
			update.playerUpdate.put(k, newPInfo);
			update.playerUpdate.get(k).id = k;
		}

		// Handle all aspects of the game here
		GamePlay.HandleInput(gameState, update, playerInput);
		playerInput = new ArrayList<PlayerInput>();
		
		GamePlay.HandleCollisions(gameState, update);
		GamePlay.HandleMovement(gameState, update);

		UpdateGameState(gameState, update);

		return update;
	}

	// Updates the old game state with the update obtained from the server
	public static GameState UpdateGameState(GameState oldState, GameUpdate update) {
		oldState.tick = update.tick;	
		oldState.paused = update.paused;
		if(update.paused) { //If the game is paused we don't update anything
			oldState.deltaTime = (1f / oldState.tps);
			return oldState; 
		}
		
		oldState.deltaTime = update.deltaTime;
		oldState.gameTime = update.gameTime;
		
		for (Map.Entry<Integer, PlayerInfo> entry : update.playerUpdate.entrySet()) {
			PlayerInfo updatedInfo = entry.getValue();
			PlayerInfo oldInfo = oldState.players.get(entry.getKey());

			// Update player information
			if (updatedInfo.x != -1) oldInfo.x = updatedInfo.x;
			if (updatedInfo.y != -1) oldInfo.y = updatedInfo.y;
			if (updatedInfo.rotation != -1) oldInfo.rotation = updatedInfo.rotation;
			oldInfo.alive = updatedInfo.alive;

			System.out.println(updatedInfo.trail);
			oldInfo.trail.addAll(updatedInfo.trail);
		}
		return oldState;
	}
	
	public void addPlayerInput(PlayerInput input) {
		playerInput.add(input);
	}

	GameState StartGame() {
		// Spillet startes
		gameState = CreateGameState(info.playerList);
		
		//Places the players around the level in a circle around the center facing in
		int placed = 0;
		int temp = gameState.levelX;
		if(gameState.levelX > temp) temp = gameState.levelX;
		float spawnDistance = temp/2 * 0.8f;
		int middleX = gameState.levelX/2;
		int middleY = gameState.levelY/2;
		
		for (PlayerInfo player : gameState.players.values()) {
			placed++;
			double angle = (double) (Math.PI * 2f * ((double)placed/(double)gameState.numberOfPlayers));
			player.x = middleX + spawnDistance * (float) Math.cos(angle);
			player.y = middleY + spawnDistance * (float) Math.sin(angle);		
			player.trail.add(new ImmutablePair<Float, Float>(player.x, player.y));
			
			player.rotation = (float) (angle + Math.PI);
		}
		return gameState;
	}
	
	private boolean CheckForUnpause() {
		for (PlayerInput input : playerInput) {
			for (ImmutablePair<PlayerAction, Float> action : input.playerActions) {
				if(action.left == PlayerAction.HostUnPause) {
					return true;
				}
			}
		}
		return false;		
	}

	// Creates a fresh game
	GameState CreateGameState(HashMap<Integer, PlayerServerInfo> playerList) {
		GameState freshState = new GameState();
		freshState.players = new HashMap<Integer, PlayerInfo>();

		// Adds players to the game in the form of playerInfo objects
		PlayerInfo pInfo;
		for (int i : playerList.keySet()) {
			pInfo = new PlayerInfo();
			pInfo.id = i;
			pInfo.trail = new ArrayList<ImmutablePair<Float, Float>>();
			freshState.players.put(pInfo.id, pInfo);
		}
		freshState.numberOfPlayers = playerList.size();
		freshState.tick = 0;
		freshState.deltaTime = (1f/info.tps);
		freshState.gameTime = 0;
		return freshState;
	}

	GameState getGameState() {
		return gameState;
	}
	
	ServerInfo getServerInfo() {
		return info;
	}
}
