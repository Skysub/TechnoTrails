package dk.dtu;

import java.util.ArrayList;

public class Game {
	
	
	Game(){
	}
	
	//The game simulates a tick and handles all the game mechanics
	GameState Step(GameState oldState) {
		GameState newState = new GameState();
		
		if(!oldState.paused) {
			
		}
		
		return null;
	}
	
	//Creates a fresh game
	GameState CreateGameState(ArrayList<PlayerInfo> players) {
		GameState freshState = new GameState();
		freshState.players = players;
		freshState.numberOfPlayers = players.size();
		return freshState;
	}
}
