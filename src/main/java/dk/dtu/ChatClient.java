package dk.dtu;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jspace.*;

public class ChatClient implements Runnable {
     Space chatSpace;
     DefaultTableModel chatModel;
     Client client;
     int myID;
     boolean running = true; // Flag to control the thread

    public ChatClient(Space chatSpace, Client client, int myID, DefaultTableModel chatModel) {
        this.chatSpace = chatSpace;
        this.client = client;
        this.myID = myID;
        this.chatModel = chatModel;
    }

    public void stop() {
        running = false; // Set the flag to stop the thread
    }

    @Override
    public void run() {
        while (running) { // Check the flag to decide whether to continue running
            try {
                // Listen for messages from the server
                Object[] message = chatSpace.query(new FormalField(Integer.class), new FormalField(String.class));
                int senderId = (int) message[0];
                String content = (String) message[1];

                // Update chat model with the received message
                SwingUtilities.invokeLater(() -> {
                    //chatModel.setRowCount(0);
                    chatModel.addRow(new Object[] { "Player " + senderId, content });
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                return; // Exit the thread when an exception occurs
            }
        }
    }
}
