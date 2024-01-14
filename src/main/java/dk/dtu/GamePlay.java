package dk.dtu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.ImmutablePair;

//The gameplay class handles the different gameplay mechanics by adding to the gameUpdate and gameState in the game class
//All the methods should be static as this class only exists to make the Game class more readable
public class GamePlay {
	// Constants that define the gameplay
	final static float BASE_SPEED = 20; // In pixels/second
	final static int MIN_TRAIL_SEGMENT = 2; // In pixels
	final static int PLAYER_SIZE = 4; // Radius in pixels

	static void HandleInput(GameState gameState, GameUpdate update, PlayerInput playerInput[]) {
		for (int i = 0; i < playerInput.length; i++) {
			int id = playerInput[i].id;
			PlayerInfo pInfo = update.playerUpdate.get(id);
			for (int j = 0; j < playerInput[i].playerActions.size(); j++) {
				float val = playerInput[i].playerActions.get(j).right;
				switch (playerInput[i].playerActions.get(j).left) {
				case Turn:
					pInfo.rotation = val;
					break;

				default:
					System.out.println(
							"No effect linked to the PlayerAction: " + playerInput[i].playerActions.get(j).left);
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
				float trailSegmentLength = (float) Math.sqrt(Math.pow(newX - info.trail.get(trailLength).getLeft(), 2)
						+ Math.pow(newY - info.trail.get(trailLength).getRight(), 2));
				if (trailSegmentLength >= MIN_TRAIL_SEGMENT) {
					// Add the new position to the trail
					update.playerUpdate.get(info.id).trail.add(new ImmutablePair<Float, Float>(newX, newY));
				}

				update.playerUpdate.get(info.id).x = newX;
				update.playerUpdate.get(info.id).x = newY;
			}
		}
	}

	// Checks for collisions and kills players if necessary
	static void HandleCollisions(GameState gameState, GameUpdate update) {
		//We start by creating a 2d array of players and their traildata, sorted by the x-value of each trail segment
		Coords xy[][] = ComputeTrails(gameState);

		//Then we check for collisions for each player
		for (PlayerInfo info : gameState.players.values()) {
			update.playerUpdate.get(info.id).alive = DoesCollide(info, gameState, xy);
		}
	}

	//Returns alive (T or F) depending on whether the player collides
	static boolean DoesCollide(PlayerInfo info, GameState gameState, Coords xy[][]) {
		//Are they already dead or out of bounds?
		if (!info.alive || info.x > gameState.levelX || info.x < 0 || info.y > gameState.levelY || info.y < 0) {
			return false;
		}

		// Linear time algorithm
		int min = Math.round(info.x) - PLAYER_SIZE, max = Math.round(info.x) + PLAYER_SIZE;
		int minY = Math.round(info.y) - PLAYER_SIZE, maxY = Math.round(info.y) + PLAYER_SIZE;
		int minIndex = -1, maxIndex = -1;

		//For each players trail
		for (int i = 0; i < xy.length; i++) {
			//we get the range of x-values that could cause the player to collide
			for (int j = 0; j < xy[i].length; j++) {
				if (minIndex != -1 && xy[i][j].x >= min)
					minIndex = j;
				if (maxIndex != -1 && xy[i][j].x > max) {
					minIndex = j;
					break;
				}
			}
			//No such x-value?
			if (minIndex == -1 || maxIndex == -1)
				break;

			//For each x-value, we check if the corresponsing y-value makes us collide
			for (int j = minIndex; j < maxIndex; j++) {
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
		;
		for (PlayerInfo info : gameState.players.values()) {
			if (info.trail.size() > longestTrail)
				longestTrail = info.trail.size();
		}

		Coords xy[][] = new Coords[gameState.numberOfPlayers][longestTrail];

		int i = 0;
		for (PlayerInfo info : gameState.players.values()) {
			for (int j = 0; j < info.trail.size(); j++) {
				xy[i][j] = new Coords(Math.round(info.trail.get(j).left), Math.round(info.trail.get(j).right));
			}

			for (int j = info.trail.size(); j < longestTrail; j++) {
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
