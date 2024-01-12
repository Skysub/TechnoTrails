package dk.dtu;

import java.util.HashMap;

public class ServerInfo {
    public HashMap<Integer, PlayerServerInfo> playerList = new HashMap<Integer, PlayerServerInfo>();
    public int tps = -1;
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
