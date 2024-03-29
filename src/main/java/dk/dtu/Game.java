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
	private HashMap<Integer, TrailInfo> trailInfo;
	static final boolean DEBUG_measureTime = false;
	private int timeCalc = 0;
	private long totalHandleMovementTime = 0;
	private long totalHandleInputTime = 0;
	private long totalHandleCollisionsTime = 0;
	private long totalCheckForWinnersTime = 0;
	private long totalUpdateGameStateTime = 0;
	private int lastFullGameStateTick = 0;
	

	Game(ServerInfo info, Space gameSpace) {
		this.tps = info.tps;
		this.info = info;
		this.gameSpace = gameSpace;
		gameState = CreateGameState(info.playerList);
	}

	// The game simulates a tick and handles all the game mechanics
	GameUpdate Tick() {
		GameUpdate update = new GameUpdate();
		update.tick = gameState.tick + 1;
		// System.out.println("Now processing tick " + update.tick);

		// If the game is paused, nothing about the game should be updated
		if (gameState.paused && !CheckForUnpause()) {
			update.paused = true;
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
		if (deltaTime > (2f / tps) || deltaTime < (1f / tps)) {
			deltaTime = (1f / tps);
		}

		
		update.deltaTime = deltaTime;

		update.playerUpdate = new HashMap<Integer, PlayerInfo>();
		for (int k : gameState.players.keySet()) {
			PlayerInfo newPInfo = new PlayerInfo();
			newPInfo.id = k;
			newPInfo.trail = new ArrayList<ImmutablePair<Float, Float>>();
			update.playerUpdate.put(k, newPInfo);
			update.playerUpdate.get(k).id = k;
		}
		
		// Handle all aspects of the game here

		if (DEBUG_measureTime) {
			long time;
			time = System.nanoTime();
			GamePlay.HandleInput(gameState, update, playerInput);
			playerInput = new ArrayList<PlayerInput>();
			totalHandleInputTime += (System.nanoTime()-time)/1000;

			time = System.nanoTime();
			GamePlay.HandleCollisions(gameState, update);
			totalHandleCollisionsTime += (System.nanoTime()-time)/1000;
			
			time = System.nanoTime();
			GamePlay.HandleMovement(gameState, update, trailInfo);	
			totalHandleMovementTime += (System.nanoTime()-time)/1000;
			
			time = System.nanoTime();
			gameState.winner = GamePlay.CheckForWinner(gameState, update);
			totalCheckForWinnersTime += (System.nanoTime()-time)/1000;

			time = System.nanoTime();
			UpdateGameState(gameState, update);
			totalUpdateGameStateTime += (System.nanoTime()-time)/1000;
			timeCalc++;
			
			System.out.println("Tick: " + update.tick);
			System.out.println("Average time for handling input: + " + (totalHandleInputTime/timeCalc) + " us");
			System.out.println("Average time for handling collisions: + " + (totalHandleCollisionsTime/timeCalc) + " us");
			System.out.println("Average time for handling mvement: + " + (totalHandleMovementTime/timeCalc) + " us");
			System.out.println("Average time checking for winners: + " + (totalCheckForWinnersTime/timeCalc) + " us");
			System.out.println("Average time for updating the gamestate: + " + (totalUpdateGameStateTime/timeCalc) + " us");
			System.out.println("Average time for them all: + " + ((totalHandleInputTime+totalUpdateGameStateTime+totalCheckForWinnersTime+totalHandleCollisionsTime+totalHandleMovementTime)/timeCalc) + " us");
			System.out.println();
		} else {
			// Handle all aspects of the game here
			GamePlay.HandleInput(gameState, update, playerInput);
			playerInput = new ArrayList<PlayerInput>();

			GamePlay.HandleCollisions(gameState, update);
			GamePlay.HandleMovement(gameState, update, trailInfo);	
			gameState.winner = GamePlay.CheckForWinner(gameState, update);

			UpdateGameState(gameState, update);
		}


		return update;
	}

	// Updates the old game state with the update obtained from the server
	public static GameState UpdateGameState(GameState oldState, GameUpdate update) {
		oldState.tick = update.tick;
		oldState.paused = update.paused;
		if (update.paused) { // If the game is paused we don't update anything
			oldState.deltaTime = (1f / oldState.tps);
			return oldState;
		}

		oldState.deltaTime = update.deltaTime;
		oldState.gameTime = update.gameTime;

		for (Map.Entry<Integer, PlayerInfo> entry : update.playerUpdate.entrySet()) {
			PlayerInfo updatedInfo = entry.getValue();
			PlayerInfo oldInfo = oldState.players.get(entry.getKey());

			// System.out.println("Tick: " + update.tick + ", players old and new x coord: "
			// + oldInfo.x + " " + updatedInfo.x);
			// Update player information
			if (updatedInfo.x != -1)
				oldInfo.x = updatedInfo.x;
			if (updatedInfo.y != -1)
				oldInfo.y = updatedInfo.y;
			if (updatedInfo.rotation != -1)
				oldInfo.rotation = updatedInfo.rotation;
			oldInfo.alive = updatedInfo.alive;
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
		trailInfo = new HashMap<Integer, TrailInfo>();

		// Places the players around the level in a circle around the center facing in
		int placed = 0;
		int temp = gameState.levelY;
		if (gameState.levelY > temp)
			temp = gameState.levelY;
		float spawnDistance = temp / 2 * 0.8f;
		int middleX = gameState.levelX / 2;
		int middleY = gameState.levelY / 2;

		
		for (PlayerInfo player : gameState.players.values()) {
			placed++;
			double angle = (double) (Math.PI * 2f * ((double) placed / (double) gameState.numberOfPlayers));
			player.x = middleX + spawnDistance * (float) Math.cos(angle);
			player.y = middleY + spawnDistance * (float) Math.sin(angle);
			player.trail.add(new ImmutablePair<Float, Float>(player.x, player.y));

			player.rotation = (float) (angle + Math.PI);
			
			trailInfo.put(player.id, new TrailInfo());
		}
		return gameState;
	}

	private boolean CheckForUnpause() {
		for (PlayerInput input : playerInput) {
			for (ImmutablePair<PlayerAction, Float> action : input.playerActions) {
				if (action.left == PlayerAction.HostUnPause) {
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
		freshState.tps = info.tps;
		freshState.deltaTime = (1f / info.tps);
		freshState.gameTime = 0;
		return freshState;
	}

	GameState getGameState() {
		return gameState;
	}

	ServerInfo getServerInfo() {
		return info;
	}

	public int getLastFullGameStateTick() {
		return lastFullGameStateTick;
	}

	public void setLastFullGameStateTick(int tick) {
		lastFullGameStateTick = tick;
		
	}
}
