package dk.dtu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.ImmutablePair;

//The gameplay class handles the different gameplay mechanics by adding to the gameUpdate and gameState in the game class
//All the methods should be static as this class only exists to make the Game class more readable
public class GamePlay {
	// Constants that define the gameplay
	final static float BASE_SPEED = 70; // In pixels/second
	final static float TURNING_SPEED = 2.5f; //In radians/second
	final static int MIN_TRAIL_SEGMENT = 2; // In pixels
	final static int PLAYER_SIZE = 6; // Radius in pixels
	final static int GAME_COUNTDOWN = 4; // Seconds before the game starts
	final static int TRAIL_WIDTH = 2; //Width of trail in pixels (when drawn as lines)
	final static int LEVEL_BORDER = 10; //Amount of pixels from the levels real edge, that we put the edge
	final static int WINNER_DELAY = 6; // Seconds the winners name i shown on screen before clients return to lobby

	static void HandleInput(GameState gameState, GameUpdate update, ArrayList<PlayerInput> playerInput) {
		for (int i = 0; i < playerInput.size(); i++) {
			int id = playerInput.get(i).id;
			PlayerInfo pInfo = update.playerUpdate.get(id);
			for (int j = 0; j < playerInput.get(i).playerActions.size(); j++) {
				float val = playerInput.get(i).playerActions.get(j).right;
				switch (playerInput.get(i).playerActions.get(j).left) {
				case Turn:
					pInfo.rotation = val;
					break;
					
				case HostPause:
					update.paused = true;
					break;
					
				case HostUnPause:
					update.paused = false;
					break;

				default:
					System.out.println(
							"No effect linked to the PlayerAction: " + playerInput.get(i).playerActions.get(j).left);
					break;
				}
			}
		}
	}

	// moves the players and adds to their trails
	static void HandleMovement(GameState gameState, GameUpdate update) {
		for (PlayerInfo info : gameState.players.values()) {
			if (info.alive) {
				
				float newX = (float) (info.x + update.deltaTime * BASE_SPEED * Math.cos(info.rotation));
				float newY = (float) (info.y + update.deltaTime * BASE_SPEED * Math.sin(info.rotation));

				int trailLength = info.trail.size();
				float trailSegmentLength = (float) Math.sqrt(Math.pow(newX - info.trail.get(trailLength-1).getLeft(), 2)
						+ Math.pow(newY - info.trail.get(trailLength-1).getRight(), 2));
				if (trailSegmentLength >= MIN_TRAIL_SEGMENT) {
					// Add the new position to the trail
					update.playerUpdate.get(info.id).trail.add(new ImmutablePair<Float, Float>(newX, newY));
				}

				update.playerUpdate.get(info.id).x = newX;
				update.playerUpdate.get(info.id).y = newY;
				//System.out.println("Handling movement for player: " + info.id + ". Old and new x: " + info.x + " " + newX);
			}
		}
	}
	
	public static int CheckForWinner(GameState gameState, GameUpdate update) {
		int aliveID = -1;
		for (PlayerInfo info : gameState.players.values()) {
			if(info.alive) {
				if(aliveID == -1) {
					aliveID = info.id;
				} else {
					return -1;
				}
			}
		}
		if(aliveID == -1) aliveID = 0;
		update.paused = true;
		return aliveID;
	}

	// Checks for collisions and kills players if necessary
	static void HandleCollisions(GameState gameState, GameUpdate update) {
		// We start by creating a 2d array of players and their traildata, sorted by the
		// x-value of each trail segment
		Coords xy[][] = ComputeTrails(gameState);

		// Then we check for collisions for each player
		for (PlayerInfo info : gameState.players.values()) {
			update.playerUpdate.get(info.id).alive = DoesCollide(info, gameState, xy);
		}
	}

	// Returns alive (T or F) depending on whether the player collides
	static boolean DoesCollide(PlayerInfo info, GameState gameState, Coords xy[][]) {
		// Are they already dead or out of bounds?
		if (!info.alive || info.x > gameState.levelX-GamePlay.LEVEL_BORDER || info.x < GamePlay.LEVEL_BORDER ||
				           info.y > gameState.levelY-GamePlay.LEVEL_BORDER || info.y < GamePlay.LEVEL_BORDER) {
			return false;
		}
		
		//If the tails are too small, they should not die
		if(xy[0].length == 0) return true;

		// Linear time algorithm
		int min = Math.round(info.x) - PLAYER_SIZE, max = Math.round(info.x) + PLAYER_SIZE;
		int minY = Math.round(info.y) - PLAYER_SIZE, maxY = Math.round(info.y) + PLAYER_SIZE;
		int minIndex = -1, maxIndex = -1;

		// For each players trail
		for (int i = 0; i < xy.length; i++) {
			// we get the range of x-values that could cause the player to collide
			for (int j = 0; j < xy[i].length; j++) {
				if (minIndex != -1 && xy[i][j].x >= min && xy[i][j].x <= max)
					minIndex = j;
				if (maxIndex != -1 && minIndex != -1 && xy[i][j].x > max) {
					maxIndex = j - 1;
					break;
				}
			}
			// No such x-value?
			if (minIndex == -1 || maxIndex == -1)
				break;

			// For each x-value, we check if the corresponsing y-value makes us collide
			for (int j = minIndex; j <= maxIndex; j++) {
				if (xy[i][j].y >= minY && xy[i][j].y <= maxY)
					return false;
			}
		}
		return true;
	}

	// Creates a two-dimensional array where the first dimension is players and the
	// second is the longest trail among them
	// The sorts the trail array according to the x component only
	private static Coords[][] ComputeTrails(GameState gameState) {
		int longestTrail = 0;
		for (PlayerInfo info : gameState.players.values()) {
			if (info.trail.size() > longestTrail)
				longestTrail = info.trail.size();
		}
		longestTrail -= PLAYER_SIZE; //Make sure spawning trails can't cause a death
		if(longestTrail < 0) longestTrail = 0;
		
		Coords xy[][] = new Coords[gameState.numberOfPlayers][longestTrail];

		int i = 0;
		for (PlayerInfo info : gameState.players.values()) {
			for (int j = 0; j < info.trail.size() - PLAYER_SIZE; j++) {
				xy[i][j] = new Coords(Math.round(info.trail.get(j).left), Math.round(info.trail.get(j).right));
			}

			int temp = info.trail.size() - PLAYER_SIZE;
			if(temp < 0) temp = 0;
			for (int j = temp; j < longestTrail; j++) {
				xy[i][j] = new Coords(-100, -100);
			}
			i++;
		}

		for (int j = 0; j < xy.length; j++) {
			Arrays.sort(xy[j]);
		}
		return xy;
	}
}

class Coords implements Comparable {
	public int x;
	public int y;

	Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Object o) {
		return x - ((Coords) o).x;
	}
}
