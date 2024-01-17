package dk.dtu;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import java.util.HashMap;

public class GameServer implements Runnable {
	Game game;
	Space gameSpace;
	Space lobbySpace;
	long tickRate;
	boolean shuttingDown = false;

	public GameServer(Game game, Space gameSpace, Space lobbySpace, int tickRate) {
		this.game = game;
		this.gameSpace = gameSpace;
		this.lobbySpace = lobbySpace;
		this.tickRate = tickRate;
	}

	@Override
	public void run() {
		try {
			gameSpace.put("game server ready");
			Thread.sleep(50 + GamePlay.GAME_COUNTDOWN * 1000, 0);
		} catch (Exception e) {
			System.out.println("Error while starting the game server");
			e.printStackTrace();
		}

		while (true) {
			long timeAtStart = System.nanoTime();

			GameUpdate update = game.Tick(); // The main event!

			try {
				gameSpace.getp(new FormalField(GameUpdate.class));
				gameSpace.put(update);

				for (int id : game.getServerInfo().playerList.keySet()) {
					lobbySpace.put(id, LobbyToClientMessage.GameUpdate);
				}

				if (game.getGameState().winner != -1) {
					System.out.println("Winner is id: " + game.getGameState().winner);
					
					gameSpace.getp(new FormalField(GameState.class));
					gameSpace.put(game.getGameState());
					Thread.sleep(5); // Wait a moment before announcing the winner
					lobbySpace.put(game.getGameState().winner, ClientToLobbyMessage.IncrementScore);
					for (int id : game.getServerInfo().playerList.keySet()) {
						lobbySpace.put(id, LobbyToClientMessage.AnnounceWinner);
					}
					shuttingDown = true;
				}
				
				if(shuttingDown) {
					gameSpace.get(new ActualField("game empty"));
					System.out.println("Game empty, shutting down gameserver");
					lobbySpace.put(-1, ClientToLobbyMessage.HostEndGame);
					return;
				}

			} catch (InterruptedException e) {
				System.out.println("Error while trying to update the gameState in the gameSpace");
				e.printStackTrace();
			}

			// Now we sleep until the next tick should start
			long remaining = (timeAtStart + 1000000000 / tickRate) - System.nanoTime();
			if (remaining > 10000) {
				long millis = remaining / 1000000;
				int nanos = (int) (remaining - (millis * 1000000));
				try {
					// System.out.println("GameServer thread going to sleep for " + millis + "ms and
					// " + nanos + "ns");
					Thread.sleep(millis, nanos);
				} catch (InterruptedException e) {
					System.out.println("Tried sleeping to wait for next tick, got exception");
					e.printStackTrace();
				}
				;
			}
		}
	}
}