package com.iems5722.partytime;

import android.util.Log;

import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Besides holding information for communicating with peers (other players), it also provides
 * common functions for communications between server and client.
 */
public class GameServer {
    private static final String TAG = GameServer.class.getClass().getSimpleName();

    // Singleton implementation
    protected static GameServer instance = null;

    // Define which IP address we are going to setup a server
    protected String serverIP;

    // Define ports we are going to setup a server
    protected static final int tcpPort = 54555;
    protected static final int udpPort = 54777;

    // Define local machine is the host or not
    protected boolean isHost = false;

    // Define a list for holding GameClient
    protected ArrayList<GameClient> players = null;

    // Reference for Kryonet server
    protected Server server;

    // List of getters and setters
    public boolean isHost() {
        return this.isHost;
    }

    public ArrayList<GameClient> getPlayers() {
        return players;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void addPlayer(GameClient c) {
        this.players.add(c);
    }

    protected GameServer() {
        this.players = new ArrayList<GameClient>();
    }

    public static GameServer getInstance() {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    /**
     * Setup our server.
     *
     * @param ip string
     * @return boolean
     */

    public boolean setup(String ip) {
        boolean ret = true;

        // Save server ip
        this.serverIP = ip;

        Log.d(TAG, String.format("Setup server with ip |%s|...", ip));

        // Instantiate Kryonet server
        this.server = new Server();

        // TODO: initialize kryonet server

        try {
            this.server.bind(this.getTcpPort(), this.getUdpPort());
            this.server.start();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            ret = false;
        }

        return ret;
    }
}
