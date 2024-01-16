package dk.dtu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.jspace.FormalField;

public class GameChatPanel extends JPanel {
    static final int SCREEN_HEIGHT = 720;
    static final int SCREEN_WIDTH = 1280;
    DefaultTableModel chatModel;
    JTable chatTable;
    public JScrollPane chatPanel;
    private JTextField chatField;
    Client client;
    
    GridBagConstraints gbc = new GridBagConstraints();

    public GameChatPanel(Client client) {
        this.client=client;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(200, 190));
        chatModel = getClientChatModel();

        String[] CHAT_COLUMNS = { "Chat" };
        chatModel = new DefaultTableModel(CHAT_COLUMNS, 0) {
            private static final long serialVersionUID = -1792645556686553938L;

            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }
        };

        chatTable = new JTable(chatModel);
        chatTable.setRowHeight(20);
        chatPanel = new JScrollPane(chatTable);
        chatPanel.setPreferredSize(new Dimension(200, 300));
		chatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		chatPanel.getViewport().setBackground(new Color(0, 76, 153));
        gbc.gridy = 0;
        add(chatPanel, gbc);

        chatField = new JTextField();
		chatField.setPreferredSize(new Dimension(200, 25));
		Border border = BorderFactory.createLineBorder(Color.WHITE, 2);
		chatField.setBorder(border);
		chatField.setFocusable(true);
		chatField.addKeyListener(new MyKeyAdapter());
        gbc.gridy = 1;
        add(chatField, gbc);
        updateChatModel();
		revalidate();
    }

        String message;

        public class MyKeyAdapter extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    message = chatField.getText();
                    chatField.setText("");
                    try {
                        client.getClientChatSpace().put(client.getName(), message);
                        updateChatModel();
                        } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        public DefaultTableModel getClientChatModel(){
            return client.chatModel;
        }

// Create a method to periodically check for new chat messages and update the chat model
public void updateChatModel() {
    Timer timer = new Timer(10, new ActionListener() { // Adjust the interval as needed
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Query for new chat messages

                List<Object[]> chatMessages = client.getClientChatSpace().queryAll(
                        new FormalField(String.class),
                        new FormalField(String.class));

                // Update the chat model with new messages
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // Update the model only with new messages
                        if (chatMessages.size() != chatModel.getRowCount()) {
                            for (int i = chatModel.getRowCount(); i < chatMessages.size(); i++) {
                                Object[] chatm = chatMessages.get(i);
                                chatModel.addRow(new Object[] { chatm[0] + ": " + chatm[1] });
                            }
                        }
                    }
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    });
    timer.setRepeats(true);
    timer.start();
}


}
