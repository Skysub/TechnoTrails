package dk.dtu;

import org.jspace.*;

import java.util.ArrayList;
import java.util.List;

public class ChatServer implements Runnable {
    Space chatSpace;
    ServerInfo serverInfo;
    Server server;
    private List<Space> clientChatSpaces; // List of chat spaces for connected clients

    public ChatServer(Space chatSpace, ServerInfo serverInfo, Server server ) {
        this.chatSpace = chatSpace;
        this.serverInfo = serverInfo;
        this.server = server;
        this.clientChatSpaces = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Receive chat messages
                Object[] message = chatSpace.query(new FormalField(Integer.class), new FormalField(String.class));
                int senderId = (int) message[0];
                String content = (String) message[1];

                // Broadcast message to all connected clients
                broadcastMessage(senderId, content);

            } catch (InterruptedException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }
    }

    // Method to add a new client's chat space to the list
    public void addClientChatSpace(Space clientChatSpace) {
        clientChatSpaces.add(clientChatSpace);
    }

    private void broadcastMessage(int senderId, String message) throws InterruptedException {
        // Iterate through all connected clients' chat spaces and send the message
        for (Space clientChatSpace : clientChatSpaces) {
            clientChatSpace.put(senderId, message);
        }
    }
}
