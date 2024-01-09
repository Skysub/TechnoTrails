package dk.dtu;

import java.util.ArrayList;

public class Game {
	
	private int tps; //ticks per second
	private GameState gameState;
	
	Game(int tps, ArrayList<PlayerInfo> players){
		this.tps = tps;
		gameState = CreateGameState(players);
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
	GameState CreateGameState(ArrayList<PlayerInfo> players) {
		GameState freshState = new GameState();
		freshState.players = players;
		freshState.numberOfPlayers = players.size();
		return freshState;
	}
	
	GameState getGameState() {
		return gameState;
	}
	
}
