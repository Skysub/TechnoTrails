package dk.dtu;

import org.jspace.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ChatClient implements Runnable {
    private Space chatSpace;
    private int myID;
    private Client client;
    private DefaultTableModel chatModel;
    private boolean running = true;

    public ChatClient(Space chatSpace, int myID, Client client, DefaultTableModel chatModel) {
        this.chatSpace = chatSpace;
        this.myID = myID;
        this.client = client;
        this.chatModel = chatModel;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object[] tuple = chatSpace.get(new FormalField(String.class), new FormalField(String.class));
                String sender = (String) tuple[0];
                String message = (String) tuple[1];

                SwingUtilities.invokeLater(() -> {
                    chatModel.addRow(new Object[] { sender + ": " + message });
                });
            } catch (InterruptedException e) {
                handleInterruptedException();
            }
        }
    }

    private void handleInterruptedException() {
        System.out.println("Connection to chat space lost. Attempting to reconnect...");
        try {
            // Implement a reconnection strategy here
            // For example, try to reconnect to the chatSpace
            // This is a simple example; you might need a more robust approach
            Thread.sleep(5000); // Wait for 5 seconds before retrying
            // Reconnect to chatSpace (you'll need to modify this based on your space setup)
            // chatSpace = new RemoteSpace("tcp://.../chatSpace?keep");
        } catch (InterruptedException ex) {
            System.out.println("Reconnection attempt interrupted.");
            running = false; // Stop the thread if reconnection is interrupted
        }
    }

    // Method to stop the ChatClient thread
    public void stop() {
        running = false;
    }
}
