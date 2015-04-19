package com.iems5722.partytime;

import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
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

    // Define classes which will be transmitted back and forth in Kryonet network
    public static class JoinHostRequest {
        //TODO work out properties here
    }

    public static class LeaveHostRequest {
        //TODO work out properties here
    }

    public static class GetPlayerListRequest {
        //TODO work out properties here
    }

    public static class JoinHostResponse {
        //TODO work out properties here
    }

    public static class UpdatePlayerListNotification {
        //TODO work out properties here
    }

    public static class KickedNotification {
        //TODO work out properties here
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

        // Define we are running as Host of the GameServer
        this.isHost = true;

        // Register classes we are going to send through the network
        this.register(this.server.getKryo());

        try {
            // Try to occupy the ports from system
            this.server.bind(tcpPort, udpPort);

            // It create a thread for itself to keep calling update() internally
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

        // Define we are running as client of the GameServer
        this.isHost = false;

        try {
            // It create a thread for itself to keep calling update() internally
            this.client.start();

            // Register classes we are going to send through the network
            this.register(this.client.getKryo());

            // Try to connect to given ipv4 with 5000 secs timeout
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

    /**
     * A method registers all classes which be sent over the Kryonet network
     *
     * @param instance Kryo
     */
    protected void register(Kryo instance) {
        instance.register(JoinHostRequest.class);
        instance.register(LeaveHostRequest.class);
        instance.register(GetPlayerListRequest.class);
        instance.register(JoinHostResponse.class);
        instance.register(UpdatePlayerListNotification.class);
        instance.register(KickedNotification.class);
    }
}
