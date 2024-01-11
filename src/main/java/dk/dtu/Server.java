package dk.dtu;

import java.util.ArrayList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jspace.FormalField;
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
        try {
            if (lobbySpace != null) {
                lobbySpace.put("shutdown");
            }
            if (chatSpace != null) {
                chatSpace.put("shutdown");
            }
            // Add any additional cleanup or shutdown procedures here
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lobbySpace = null;
            chatSpace = null;
            info = null;
        }
    }

    ServerInfo getInfo() {
        return info;
    }

    public String getChatMessage() {
        
        try {
           Object[] t = chatSpace.getp(new FormalField(String.class), new FormalField(String.class));
           return t[0] + ": " + t[1];
        
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }
    public Space getChatSpace() {
        return chatSpace;
    }
}
