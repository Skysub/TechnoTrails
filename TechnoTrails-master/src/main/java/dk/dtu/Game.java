package dk.dtu;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Game {
	
	private int tps; //ticks per second
	private GameState gameState;
	
	Game(ServerInfo info){
		this.tps = info.tps;
		gameState = CreateGameState(info.playerList);
	}
	
	//The game simulates a tick and handles all the game mechanics
	GameUpdate Tick(ServerInfo serverInfo, PlayerInput playerInput[]) {
		GameUpdate update = new GameUpdate();
		update.tick = gameState.tick++;
		
		//If the game is paused, nothing about the game should be updated
		if(gameState.paused) {
			return update;
		}
		GameState newState = new GameState();
		
		//Updates the gametime and calculates the difference to use in game calculation
		long timeNow = System.nanoTime();
		double deltaTime = (timeNow - gameState.gameTime) / 1000000000; //Difference in time in seconds
		update.gameTime = timeNow;
		newState.gameTime = timeNow;
		
		//If the difference in time since the last update is more than 250ms then update according to the tps not actual elapsed time
		//This ensures proper gameplay even if the server has a massive lag spike or if the game has been paused
		if(deltaTime > 0.25d) {
			deltaTime = (1d/tps);
		}
		
		update.playerUpdate = new HashMap<Integer, PlayerInfo>();
		for (int k : gameState.players.keySet()) {
			update.playerUpdate.put(k, new PlayerInfo());
			update.playerUpdate.get(k).id = k;
		}
		
		//Handle all aspects of the game here
		GamePlay.HandleInput(gameState, newState, update, playerInput);
		
		UpdateGameState(gameState, update);
		
		return update;
	}
	
	//Updates the old gamestate whith the update obtained from the server
	GameState UpdateGameState(GameState oldState, GameUpdate update) {
		return null; //Not yet implemented
	}
	
	GameState StartGame() {
		//Spillet startes, skal implementeres (og designes)
		
		return gameState;
	}
	
	//Creates a fresh game
	GameState CreateGameState(HashMap<Integer, String> playerList) {
		GameState freshState = new GameState();
		freshState.players = new HashMap<Integer, PlayerInfo>();
		
		//Adds players to the game in the form of playerInfo objects
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
	
}