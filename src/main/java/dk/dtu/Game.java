package dk.dtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Game {

	private int tps; // ticks per second
	private GameState gameState;
	private ArrayList<PlayerInput> playerInput = new ArrayList<PlayerInput>();;
	private ServerInfo info;

	Game(ServerInfo info) {
		this.tps = info.tps;
		this.info = info;
		gameState = CreateGameState(info.playerList);
	}

	// The game simulates a tick and handles all the game mechanics
	GameUpdate Tick() {
		GameUpdate update = new GameUpdate();
		update.tick = gameState.tick++;

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
			update.playerUpdate.put(k, new PlayerInfo());
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

			oldInfo.trail.addAll(updatedInfo.trail);
		}
		return oldState;
	}
	
	public void addPlayerInput(PlayerInput input) {
		playerInput.add(input);
	}

	GameState StartGame() {
		// Spillet startes, skal implementeres (og designes)

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
		return freshState;
	}

	GameState getGameState() {
		return gameState;
	}
	
	ServerInfo getServerInfo() {
		return info;
	}
}
