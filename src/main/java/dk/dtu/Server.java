package dk.dtu;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Server {

    ServerInfo info;
	int lastID = 0;
    Space lobbySpace;
    Space chatSpace;
    static final int defaultTickRate = 60;
    String hostAddress = "localhost";
    
    LobbyServer lobbyServer;

    Server() {
        info = new ServerInfo();
        info.tps = defaultTickRate;
        info.playerList = new HashMap<Integer, String>();
    }

    public void createLobby(String hostName) {
        addPlayer(hostName);
        SpaceRepository repository = new SpaceRepository();
        repository.addGate("tcp://" + hostAddress + ":9001/?keep");
        lobbySpace = new SequentialSpace();
        repository.add("lobby", lobbySpace);
        chatSpace = new SequentialSpace();
        repository.add("chat", chatSpace);
        
        lobbyServer = new LobbyServer(lobbySpace, info);

        //Needs to be refactored to match the new lobby protocol
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
    
    // create list of names and id's
    public int addPlayer(String name) {
    	info.playerList.put(lastID++, name);
    	return lastID;
    }
    
    public void printPlayers() {
    	// print list of names and id's
        for (Entry<Integer, String> i : info.playerList.entrySet()) {
            System.out.println("ID "+i.getKey()+" : "+i.getValue());
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
