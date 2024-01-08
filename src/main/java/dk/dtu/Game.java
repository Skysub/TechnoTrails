package dk.dtu;

public class Game {
	
	Game(){
	}
	
	//The game simulates a tick and handles all the game mechanics
	GameState Step(GameState oldState) {
		
		return null;
	}
	
	//Creates a fresh game
	GameState CreateGameState(int players) {
		GameState freshState = new GameState();
		freshState.players.add(new PlayerInfo());
		return freshState;
	}
}
