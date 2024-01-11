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
        views = new HashMap<String, View>(); //The viewManager needs a reference to the views

        //Creating the views and adding them to the viewPanel
        lobby = new Lobby(this, client);
        menu = new Menu(this, client);
        gameView = new GameView(this, client);
		viewPanel.add(lobby, "lobby");
        viewPanel.add(menu, "menu");
        viewPanel.add(gameView, "gameView");
        views.put("lobby", lobby);
        views.put("menu", menu);
        views.put("gameView", gameView);


        add(viewPanel);
        changeView("menu");
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
        
    //Changes the current view to the one specified
    void changeView(String s) {
    	views.get(currentView).whenExiting();
    	viewLayout.show(viewPanel, s);
    	currentView = s;
    	views.get(s).whenEntering(); //The views get to know when they get changed to
    }
    String getCurrentView() {
    	return currentView;
    }
}


