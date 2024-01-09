package dk.dtu;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class ServerInfo {

	int lastID = 0;
    public ArrayList<ImmutablePair<Integer, String>> playerList = new ArrayList<>();
    public int tps;

    // create list of names and id's
    public int addPlayer(String name) {
    	playerList.add(new ImmutablePair<>(lastID++, name));
    	return lastID;
    }
    
    public void printPlayers() {
    	// print list of names and id's
        for (ImmutablePair<Integer, String> i : playerList) {
            System.out.println("ID "+i.getLeft()+" : "+i.getRight());
        }
    }
}