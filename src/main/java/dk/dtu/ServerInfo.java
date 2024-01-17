package dk.dtu;

import java.util.HashMap;

public class ServerInfo {
    public HashMap<Integer, PlayerServerInfo> playerList = new HashMap<Integer, PlayerServerInfo>();
    public int tps = -1;
}

class PlayerServerInfo {
    String name = "no_name";
    Boolean ready = false;
    int score = 0;
    
    PlayerServerInfo(String name){
    	this.name = name;
    }
}



enum LobbyToClientMessage {
	LobbyStart,
	LobbyUpdate,
	LobbyGameStart,
	LobbyBackToLobby,
	LobbyShutdown,
	LobbySaysGoodbye, 
	Done,
	
	// Game Stuff
	GameUpdate, 
	AnnounceWinner,
}

enum ClientToLobbyMessage {
	ClientJoin,
	ClientDisconnect,
	ClientToggleReady,
	ClientDone, 
	HostEndGame, 
	IncrementScore,
}

enum ChatMessageServer{
	ChatStart,
	ChatJoin,
    ChatLeave,
    ChatMessage,
    ChatShutdown,
	ChatNotification,
}
