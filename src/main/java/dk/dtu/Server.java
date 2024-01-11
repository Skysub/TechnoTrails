package dk.dtu;

import java.util.ArrayList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Server {

    ServerInfo info;
    Space lobbySpace;
    Space chatSpace;
    static final int defaultTickRate = 60;
    String hostAddress = "localhost";

    Server() {
        info = new ServerInfo();
        info.tps = defaultTickRate;
        info.playerList = new ArrayList<ImmutablePair<Integer, String>>();
    }

    public void createLobby(String hostName) {
        info.addPlayer(hostName);
        SpaceRepository repository = new SpaceRepository();
        repository.addGate("tcp://" + hostAddress + ":9001/?keep");
        lobbySpace = new SequentialSpace();
        repository.add("lobby", lobbySpace);
        chatSpace = new SequentialSpace();
        repository.add("chat", chatSpace);

        try {
            lobbySpace.put(hostName);
            System.out.println("You have created and joined the lobby");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void kill() {
        // Add server shutdown logic here
    }

    ServerInfo getInfo() {
        return info;
    }
}
