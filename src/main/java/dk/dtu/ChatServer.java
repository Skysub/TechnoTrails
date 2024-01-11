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
            while (!Thread.currentThread().isInterrupted()) {
                // Retrieve the next message in the chat space
                Object[] tuple = chatSpace.get(new FormalField(Integer.class), new FormalField(ChatMessageServer.class), new FormalField(String.class));

                Integer playerId = (Integer) tuple[0];
                ChatMessageServer messageTag = (ChatMessageServer) tuple[1];
                String content = (String) tuple[2];

                switch (messageTag) {
                    case ChatMessage:
                        // Handle regular chat message
                        broadcastMessage(playerId, content);
                        break;
                    case ChatJoin:
                        // Handle client joining chat
                        broadcastNotification(playerId + " has joined the chat.");
                        break;
                    case ChatLeave:
                        // Handle client leaving chat
                        broadcastNotification(playerId + " has left the chat.");
                        break;
                    case ChatShutdown:
                        // Handle chat server shutdown
                        broadcastNotification("Chat server is shutting down.");
                        break;
                    default:
                        // Handle unknown message types
                        System.out.println("Unknown chat message type received: " + messageTag);
                        break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Chat server interrupted: " + e.getMessage());
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
