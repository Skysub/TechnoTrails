package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Game {
	
	private int tps; //ticks per second
	private GameState gameState;
	
	Game(ServerInfo info){
		this.tps = info.tps;
		gameState = CreateGameState(info.playerList);
	}
	
	//The game simulates a tick and handles all the game mechanics
	GameUpdate Tick(ServerInfo serverInfo) {
		GameUpdate update = new GameUpdate();
		update.tick = gameState.tick++;
		
		//If the game is paused, nothing about the game should be updated
		if(gameState.paused) {
			return update;
		}
		GameState newState = new GameState();
		
		//Handle all aspects of the game here
		
		return update;
	}
	
	GameState StartGame() {
		//Spillet startes, skal implementeres (og designes)
		
		return gameState;
	}
	
	//Creates a fresh game
	GameState CreateGameState(ArrayList<ImmutablePair<Integer, String>> playerList) {
		GameState freshState = new GameState();
		freshState.players = new ArrayList<PlayerInfo>();
		PlayerInfo temp;
		ImmutablePair<Integer, String> player;
		for (int i = 0; i < playerList.size(); i++) {
			temp = new PlayerInfo();
			player = playerList.get(i);
			temp.id = player.getKey();
			temp.trail = new ArrayList<ImmutablePair<Float, Float>>();
		}
		freshState.numberOfPlayers = playerList.size();
		return freshState;
	}
	
	GameState getGameState() {
		return gameState;
	}
	
}
