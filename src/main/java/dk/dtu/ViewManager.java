package dk.dtu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ViewManager extends JFrame {

	static final int SCREEN_HEIGHT = 720;
	static final int SCREEN_WIDTH = 1280;
	String currentView = "menu";
	JPanel viewPanel;
	Menu menu;
	Lobby lobby;
	CardLayout viewLayout = new CardLayout();;
    
    public ViewManager() {
        setTitle("Tehcno Trails");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        
        viewPanel = new JPanel();
        viewPanel.setLayout(viewLayout);
        viewPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
              
        //Creating the views and adding them to the viewPanel
        menu = new Menu(this);
        lobby = new Lobby(this);
        viewPanel.add(menu, "menu");
        viewPanel.add(lobby, "lobby");

        add(viewPanel);
        changeView("menu");
        
        System.out.println(viewLayout.preferredLayoutSize(menu).toString());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
        
    //Changes the current view to the one specified
    void changeView(String s) {
    	viewLayout.show(viewPanel, s);
    	currentView = s;
    }
    
    String getCurrentView() {
    	return currentView;
    }
}

