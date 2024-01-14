package dk.dtu;

import org.jspace.Space;
import java.util.HashMap;

public class GameServer implements Runnable {
    ServerInfo serverInfo;
    Space gameSpace;
    int tickRate;


    public GameServer(ServerInfo serverInfo, Space gameSpace, int tickRate) {
        this.serverInfo = serverInfo;
        this.gameSpace = gameSpace;
        this.tickRate = tickRate;
    }


    @Override
    public void run() {

        
    }

}