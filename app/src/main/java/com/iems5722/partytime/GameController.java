package com.iems5722.partytime;

import java.util.ArrayList;

/**
 * Middle man between GameServer and Activity.
 * <p/>
 * GameServer responses for codes dealing with Kryonet network while this controller take care codes
 * dealing with game specific logic and acting as middle man between Activity and GameServer
 */
public class GameController {
    // Singleton instance
    protected static GameController instance = null;

    // Reference for the game server
    protected GameServer gs;

    // Define MAX_PLAYERS
    protected int MAX_PLAYERS = 4;

    // Predefine usernames
    protected ArrayList<String> usernames = null;

    /**
     * Singleton implementation
     */
    protected GameController() {
        // Instantiate a gameServer instance
        this.gs = GameServer.getInstance();

        // Hard code list of username
        this.usernames = new ArrayList<>();
        this.usernames.add("勇者仁傑");
        this.usernames.add("法師歌莉");
        this.usernames.add("舞者結他他");
        this.usernames.add("八神太一");
    }

    /**
     * API for fetching the singleton instance
     *
     * @return GameController
     */
    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    /**
     * API for activities creates and initialize GamerServer
     *
     * @param ipv4 string
     * @return boolean
     * @note This method implies the caller to be the host of the gameServer
     */
    public boolean createGameServer(String ipv4) {
        boolean ret = this.gs.setup(ipv4);

        // Server is also a player...
        GameClient p = this.createGameClient(ipv4);

        // Append server to player list
        this.gs.addPlayer(p);

        return ret;
    }

    /**
     * API which creates a GameClient for GameServer
     *
     * @param ipv4 String
     * @return GameClient
     */
    public GameClient createGameClient(String ipv4) {
        // Get list of players currently
        ArrayList<GameClient> players = this.gs.getPlayers();

        // Fetching username
        int index = players.size();
        String username = this.usernames.get(index);

        // Instantiating GameClient
        GameClient ret = new GameClient(ipv4, username);
        return ret;
    }
}