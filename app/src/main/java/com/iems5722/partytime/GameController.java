package com.iems5722.partytime;

import android.util.Log;

import java.util.ArrayList;

/**
 * Middle man between GameServer and Activity.
 * <p/>
 * GameServer responses for codes dealing with Kryonet network while this controller take care codes
 * dealing with game specific logic and acting as middle man between Activity and GameServer
 */
public class GameController {
    private static final String TAG = GameController.class.getClass().getSimpleName();

    // Singleton instance
    protected static GameController instance = null;

    // Reference for the game server
    protected GameServer gs;

    // Determine whether server is active or not
    protected boolean isGameServerActive = false;

    // Define MAX_PLAYERS
    public static int MAX_PLAYERS = 4;

    // Predefine usernames
    protected ArrayList<String> usernames = null;

    // Predefine player icon resources
    protected ArrayList<Integer> playerIconResouce = null;

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

        // Hard code list of image resource
        this.playerIconResouce = new ArrayList<>();
        this.playerIconResouce.add(R.mipmap.p1_icon);
        this.playerIconResouce.add(R.mipmap.p2_icon);
        this.playerIconResouce.add(R.mipmap.p3_icon);
        this.playerIconResouce.add(R.mipmap.p4_icon);
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
     * API for activities create and initialize GamerServer. Note that this method implies the
     * caller to be the host of the gameServer
     *
     * @param ipv4 string
     * @return boolean
     */
    public boolean createGameServer(String ipv4) {
        boolean ret = this.gs.setup(ipv4);

        if (ret) {
            // Active gameServer
            this.isGameServerActive = true;

            // Define gameServer to be host
            this.gs.setHost(true);

            // Server is also a player...
            GameClient p = this.createGameClient(ipv4);

            // Append server to player list
            this.gs.addPlayer(p);
        }

        return ret;
    }

    /**
     * API for activities connect to the GameServer. Note that this method implies the caller is
     * not the host of the gameServer
     *
     * @param ipv4 String
     * @return boolean
     */
    public boolean connectToGameServer(String ipv4) {
        //TODO: write codes for connection
        return false;
    }

    /**
     * API for closing the GameServer
     */
    public void stopGameServer() {
        if (this.isGameServerActive) {
            this.isGameServerActive = false;
            this.gs.stop();
        }
    }

    /**
     * API for activity fetching IP of the GameServer
     *
     * @return String
     */
    public String getServerIP() {
        return this.gs.getServerIP();
    }

    /**
     * API for activity fetching whether we are the of the GameServer or not
     *
     * @return boolean
     */
    public boolean isHost() {
        return this.gs.isHost();
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

    /**
     * API for fetching player list from GameServer
     *
     * @return ArrayList\<GameClient\>
     */
    public ArrayList<GameClient> getPlayerList() {
        return this.gs.getPlayers();
    }

    /**
     * API for mapping username to player icon resource ID
     *
     * @param username String
     * @return int
     */
    public int getPlayerIconResource(String username) {
        int ret;

        int index = this.usernames.indexOf(username);

        if (index == -1) {
            Log.e(TAG, String.format("Username |%s| cannot map to resource id", username));
            ret = this.playerIconResouce.get(this.playerIconResouce.size() - 1);
        } else {
            ret = this.playerIconResouce.get(index);
        }

        return ret;
    }
}
