package dk.dtu;

import java.util.HashMap;

public class ServerInfo {
    public HashMap<Integer, PlayerServerInfo> playerList = new HashMap<Integer, PlayerServerInfo>();
    public int tps = -1;

	public String view;

	public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}

class PlayerServerInfo {
    String name = "no_name";
    Boolean ready = false;
    
    PlayerServerInfo(String name){
    	this.name = name;
    }
}



enum LobbyToClientMessage {
	LobbyStart,
	LobbyUpdate,
	LobbyGameStart,
	LobbyShutdown,
	LobbySaysGoodbye, 
	Done,
	
	// Game Stuff
	GameUpdate,
}

enum ClientToLobbyMessage {
	ClientJoin,
	ClientDisconnect,
	ClientToggleReady,
	ClientDone,
}

enum ChatMessageServer{
	ChatStart,
	ChatJoin,
    ChatLeave,
    ChatMessage,
    ChatShutdown,
	ChatNotification,
}
