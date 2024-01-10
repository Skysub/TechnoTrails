package dk.dtu;

//The gameplay class handles the different gameplay mechanics by adding to the gameUpdate and gameState in the game class
//All the methods should be static as this class only exists to make the Game class more readable
public class GamePlay {
	//Constants that define the gameplay
	final static float baseSpeed = 50;

	static void HandleInput(GameState gameState, GameState newState, GameUpdate update, PlayerInput playerInput[]) {
		for (int i = 0; i < playerInput.length; i++) {
			//Right now, rotation is the only real thing someone can do in the game
			
		}
	}
}
