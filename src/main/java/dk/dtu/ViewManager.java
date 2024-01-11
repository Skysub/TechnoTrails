package dk.dtu;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewManager extends JFrame {

	static final int SCREEN_HEIGHT = 720;
	static final int SCREEN_WIDTH = 1280;
	String currentView = "menu";
	JPanel viewPanel;
	Menu menu;
	Lobby lobby;
    GameView gameView;
	CardLayout viewLayout = new CardLayout();;
	Client client;
	HashMap<String, View> views;
    
    public ViewManager() {
        setTitle("Tehcno Trails");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        
        viewPanel = new JPanel();
        viewPanel.setLayout(viewLayout);
        viewPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        
        //Creating the client model
        client = new Client();
        views = new HashMap<String, View>();

        //Creating the views and adding them to the viewPanel
        lobby = new Lobby(this, client);
        menu = new Menu(this, client);
		viewPanel.add(lobby, "lobby");
        viewPanel.add(menu, "menu");
        views.put("lobby", lobby);
        views.put("menu", menu);
        //viewPanel.add(gameView, "gameView");

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
    	views.get(s).whenEntering();
    }
    String getCurrentView() {
    	return currentView;
    }
}


