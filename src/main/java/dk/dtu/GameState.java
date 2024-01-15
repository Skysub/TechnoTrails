package dk.dtu;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class GameState {
	int tick = -1;
	int tps = -1;
	int numberOfPlayers = 0;
	int levelX = 800, levelY = 600;
	HashMap<Integer, PlayerInfo> players;
	
	long gameTime = -1; //In ns
	boolean paused = true;
}

class GameUpdate {
	int tick = -1;
	float deltaTime = -1;
	HashMap<Integer, PlayerInfo> playerUpdate;
	
	long gameTime = -1; //In ms
	boolean paused = true;
}

//a value being -1 indicates that hasn't been set
class PlayerInfo {
	int id = -1;
	boolean alive = true;
	float x = 50;
	float y = 50;
	float rotation = -1;
	ArrayList<ImmutablePair<Float, Float>> trail;
}

//Sent to the server by the client, with all relevant inputs 
class PlayerInput {
	int id = -1;
	ArrayList<ImmutablePair<PlayerAction, Float>> playerActions;
}

//The types of actions a player can take
//Add more when the player/client should do more in the game
enum PlayerAction {
	Disconnect,
	RequestPause,
	RequestFullGamestate,
	HostPause,
	HostEndGame,
	
	Turn, //Float is new direction
}
