package dk.dtu;

import org.jspace.*;

public class ChatServer implements Runnable {
    private Space chatSpace;
    private Server server;
    private ServerInfo info;

    public ChatServer(Space chatSpace, ServerInfo info, Server server) {
        this.chatSpace = chatSpace;
        this.info = info;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            chatSpace.put(ChatMessageServer.ChatStart);
        } catch (InterruptedException e) {
            System.out.println("Error in ChatServer when starting up");
            e.printStackTrace();
        }

        while (true) {
            Object[] response;
            try {
                // Assuming you have defined playerId and content as fields
                response = chatSpace.get(new FormalField(Integer.class), new FormalField(ChatMessageServer.class));
            } catch (InterruptedException e) {
                System.out.println("Error in LobbyServer when reading the lobbySpace");
                e.printStackTrace();
                continue;
            }
            int playerId = (int) response[0];
            String content = (String) response[2];
            try {
                switch ((ChatMessageServer) response[1]) {
                    case ChatJoin:
                        broadcastMessage(playerId, content);
                        break;

                    case ChatLeave:
                        broadcastNotification(playerId + " has left the chat.");
                        break;

                    case ChatMessage:
                        broadcastMessage(playerId, content);
                        break;

                    case ChatShutdown:
                        // Handle chat server shutdown
                        broadcastNotification("Chat server is shutting down.");
                        break;

                    case ChatNotification:
                        // Handle chat notification
                        broadcastNotification(content);
                        break;

                    default:
					System.out.println(
					"ChatServer hasn't implemented a response for the ChatServerMessage: " + (ChatMessageServer) response[1]);
					break;
                }
            } catch (InterruptedException e) {
                System.out.println("Chat server interrupted: " + e.getMessage());
            }
        }
    }

    public void broadcastMessage(Integer playerId, String message) throws InterruptedException {
        String formattedMessage = info.playerList.get(playerId) + ": " + message;
        chatSpace.put("broadcast", ChatMessageServer.ChatMessage, formattedMessage);
    }

    public void broadcastNotification(String notification) throws InterruptedException {
        chatSpace.put("broadcast", ChatMessageServer.ChatNotification, notification);
    }
}
