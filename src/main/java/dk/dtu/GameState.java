package dk.dtu;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class GameState {
	int tick = -1;
	int numberOfPlayers = 0;
	HashMap<Integer, PlayerInfo> players;
	
	long gameTime = -1; //In ns
	boolean paused = true;
}

class PlayerInfo {
	int id = -1;
	boolean alive = true;
	float x = 0;
	float y = 0;
	float rotation = 0;
	ArrayList<ImmutablePair<Float, Float>> trail;
}

class GameUpdate {
	int tick = -1;
	PlayerInfo playerUpdate[];
	
	long gameTime = -1; //In ms
	boolean paused = true;
}

//Sent to the server by the client, with all relevant inputs 
class PlayerInput {
	int id = -1;
	HashMap<Integer, ImmutablePair<PlayerAction, Float>> playerActions;
}

//The types of actions a player can take
//Add more when the player/client should do more in the game
enum PlayerAction {
	DISCONNECT, //Disconnect reason as the float: 0=client_shutdown, 1=user_disconnected
	REQUEST_PAUSE,
	DECLARE_READY,
	DECLARE_NOT_READY,
	REQUEST_FULL_GAMESTATE,
	
	TURN, //Float is new direction
}
