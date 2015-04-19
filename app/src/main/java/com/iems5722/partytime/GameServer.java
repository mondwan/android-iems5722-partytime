package com.iems5722.partytime;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
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
    protected ArrayList<GamePlayer> players = null;

    // Reference for Kryonet server
    protected Server server;

    // Reference for Kryonet client
    protected Client client;

    // List of getters and setters
    public boolean isHost() {
        return this.isHost;
    }

    public ArrayList<GamePlayer> getPlayers() {
        return players;
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

    public void addPlayer(GamePlayer c) {
        this.players.add(c);
    }

    protected GameServer() {
        this.players = new ArrayList<>();
    }

    public static GameServer getInstance() {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    /**
     * Setup our server. Note that this implies that GameServer is running in host mode
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

        this.isHost = true;

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

    /**
     * Connect to other GameServer. Note that this implies that GameServer is running in non-host
     * mode and this is a blocking call.
     *
     * @param ipv4 String
     * @return boolean
     */
    public boolean connect(String ipv4) {
        boolean ret = true;

        Log.d(TAG, String.format("Connecting to GameServer with ip |%s|...", ipv4));

        // Instantiate Kryonet Client
        this.client = new Client();

        this.isHost = false;

        try {
            this.client.start();
            // TODO: initialize kryonet client
            this.client.connect(5000, ipv4, tcpPort, udpPort);

            Log.d(TAG, String.format("Able to connect to GameServer with ip |%s|", ipv4));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            ret = false;
        }

        return ret;
    }

    /**
     * Close our server
     */
    public void stop() {
        Log.d(TAG, "Close game server...");

        if (this.isHost) {
            // Stop the Kryonet server
            this.server.stop();
        } else {
            this.client.stop();
        }

        // Empty list of players
        this.players.clear();
    }
}
