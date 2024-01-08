package dk.dtu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;

public class Menu extends JPanel{

    JButton joinButton = new JButton("JOIN GAME");
    JButton hostButton = new JButton("HOST GAME");

    private JTextField textField;
    ViewManager viewManager;


    public Menu(ViewManager viewManager)  {
    	this.viewManager = viewManager;
    	setBounds(viewManager.getBounds());
    	setPreferredSize(new Dimension(viewManager.getWidth(), viewManager.getHeight()));
    	initMenu();
    }
    
    void initMenu(){
    	
            // Add a JLabel for instruction
            JLabel instructionLabel = new JLabel("Name:");
            instructionLabel.setBounds(235, 505, 120, 20);
            instructionLabel.setForeground(Color.white);
            add(instructionLabel);

            // Add a JTextField for input
            textField = new JTextField();
            textField.setBounds(viewManager.getWidth() / 2 - 60, 500, 120, 40);
            Border border = BorderFactory.createLineBorder(Color.GRAY);
            textField.setBorder(border);
            add(textField);

        hostButton.setBounds(viewManager.getWidth() / 2 - 60, 250, 120, 50);
        hostButton.setForeground(Color.white);
        hostButton.setBackground(new Color(0, 76, 153));
        hostButton.setOpaque(true);
        hostButton.setBorderPainted(false);
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewManager.changeView("lobby");
            }
        });

        joinButton.setBounds(viewManager.getWidth() / 2 - 60, 330, 120, 50);
        joinButton.setForeground(Color.white);
        joinButton.setBackground(new Color(0, 76, 153));
        joinButton.setOpaque(true);
        joinButton.setBorderPainted(false);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Join Game Button");
            }
        });
        add(hostButton);
        add(joinButton);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(102, 178, 255));
        g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());
        g.setColor(new Color(0, 76, 153));
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics metric = getFontMetrics(g.getFont());
        g.drawString("Techno Trails", (viewManager.getWidth() - metric.stringWidth("Techno Trails")) / 2, 200);
    }

    
    /*public void exitView(BoardPanel board) {
        board.remove(hostButton);
        board.remove(joinButton);
    }*/

}
