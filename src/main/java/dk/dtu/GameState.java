package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class GameState {
	int tick = -1;
	int numberOfPlayers = 0;
	ArrayList<PlayerInfo> players;
	
	int gameTime = -1; //In ms
	boolean paused = true;
}

class PlayerInfo {
	int id = -1;
	float x = 0;
	float y = 0;
	float rotation = 0;
	ArrayList<ImmutablePair<Float, Float>> trail;
}
