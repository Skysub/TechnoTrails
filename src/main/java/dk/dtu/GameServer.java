package dk.dtu;

import org.jspace.FormalField;
import org.jspace.Space;
import java.util.HashMap;

public class GameServer implements Runnable {
	Game game;
	Space gameSpace;
	Space lobbySpace;
	long tickRate;

	public GameServer(Game game, Space gameSpace, Space lobbySpace, int tickRate) {
		this.game = game;
		this.gameSpace = gameSpace;
		this.lobbySpace = lobbySpace;
		this.tickRate = tickRate;
	}

	@Override
	public void run() {
		while (true) {
			long timeAtStart = System.nanoTime();

			GameUpdate update = game.Tick();
			
			try {
				gameSpace.get(new FormalField(GameUpdate.class));
				gameSpace.put(update);
				
				for (int id : game.getServerInfo().playerList.keySet()) {
					lobbySpace.put(id, LobbyToClientMessage.LobbyUpdate);
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