package dk.dtu;

import java.util.HashMap;

public class ServerInfo {
    public HashMap<Integer, String> playerList = new HashMap<Integer, String>();
    public int tps;
}

enum LobbyMessage {
	LobbyStart,
	ClientJoin,
	LobbyUpdate,
	ClientDisconnect,
	ClientToggleReady,
	LobbyGameStart,
	LobbyShutdown,
}