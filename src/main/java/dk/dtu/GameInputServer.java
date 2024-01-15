package dk.dtu;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.FormalField;
import org.jspace.Space;

public class GameInputServer implements Runnable {
	Game game;
    Space gameSpace;


    public GameInputServer(Game game, Space gameSpace) {
        this.game = game;
        this.gameSpace = gameSpace;
    }
		
	@Override
	public void run() {
		try {
			gameSpace.put("GameInputServerReady");
		} catch (InterruptedException e) {
			System.out.println("Error in GameServer when starting up");
			e.printStackTrace();
			return;
		}

		while (true) {
			Object[] response;
			try {
				response = gameSpace.get(new FormalField(PlayerInput.class));
			} catch (InterruptedException e) {
				System.out.println("Error in gameServer when reading the gameSpace");
				e.printStackTrace();
				continue;
			}

			PlayerInput input = (PlayerInput) response[0];
			for (ImmutablePair<PlayerAction, Float> action : input.playerActions) {
				try {
					switch (action.left) {
					case Shutdown:
						gameSpace.put("InputServer Done");
						return;
						
					case RequestFullGamestate:
						gameSpace.getp(new FormalField(GameState.class));
						gameSpace.put(game.getGameState());
						gameSpace.put("New_game_state_put");
						break;

					default:
						System.out.println("GameServer hasn't implemented a specific response for the PlayerAction: "
								+ action.left + "Which is probably fine.");
						break;
					}
				} catch (Exception e) {
					System.out.println("Error in GameServer when handling a PlayerAction: "
							+ action.left);
				}
			}
			game.addPlayerInput(input);

		}

	}

}