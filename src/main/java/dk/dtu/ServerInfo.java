package dk.dtu;

import java.util.HashMap;

public class ServerInfo {
    public HashMap<Integer, String> playerList = new HashMap<Integer, String>();
    public int tps = -1;
}

enum LobbyToClientMessage {
	LobbyStart,
	LobbyUpdate,
	LobbyGameStart,
	LobbyShutdown,
	LobbySaysGoodbye,
}

enum ClientToLobbyMessage {
	ClientJoin,
	ClientDisconnect,
	ClientToggleReady,
}

enum ChatMessageServer{
	ChatStart,
	ChatJoin,
    ChatLeave,
    ChatMessage,
    ChatShutdown,
	ChatNotification,
}
