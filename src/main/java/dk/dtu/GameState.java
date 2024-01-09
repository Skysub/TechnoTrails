package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

public class GameState {
	int numberOfPlayers = 0;
	ArrayList<PlayerInfo> players;
	
	int gameTime; //In ms
	boolean paused = true;
}

class PlayerInfo {
	int id = -1;
	String name = "noName";
	float x = 50;
	float y = 50;
	float rotation = 0;
	ArrayList<Pair<Float, Float>> trail;
}
