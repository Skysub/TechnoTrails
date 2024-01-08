package dk.dtu;

import java.util.ArrayList;
import java.util.List;

public class ServerInfo {

    public static List<String> nameList = new ArrayList<>();

    // create list of names
    public static void addName(String name) {
        nameList.add(name);

        // print list of names
        for (String i : nameList) {
            System.out.println(i);
        }

    }
}