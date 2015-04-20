package com.iems5722.partytime;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

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

    // Reference for Kryonet server
    protected Server server;

    // Reference for Kryonet client
    protected Client client;

    // A reference of a listener which will be attach to either the Server or Client depends on
    // the run mode we are going to run
    protected Listener listener;

    // List of getters and setters
    public boolean isHost() {
        return this.isHost;
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

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    // List of ACTION_CODE for handler from GameController
    public static final int ON_RECEIVED_MSG = 0;
    public static final int ON_SERVER_DISCONNECTED = 1;
    public static final int ON_CLIENT_DISCONNECTED = 2;

    // Holds an ipv4 reference for us to look up later on
    protected static class GameConnection extends Connection {
        public String ipv4;
    }

    protected GameServer() {
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
     * @param ip      string
     * @param handler Handler
     * @return boolean
     */
    public boolean setup(String ip, final Handler handler) {
        boolean ret = true;

        // Save server ip
        this.serverIP = ip;

        Log.d(TAG, String.format("Setup server with ip |%s|...", ip));

        // Instantiate Kryonet server
        this.server = new Server() {
            protected Connection newConnection() {
                return new GameConnection();
            }
        };

        // Define we are running as Host of the GameServer
        this.isHost = true;

        // Register classes we are going to send through the network
        this.register(this.server.getKryo());

        // Setup a listener
        this.listener = this.createKryonetServerListener(handler);

        // Register a listener so that we forward received Messages to GameController
        this.server.addListener(this.listener);

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
     * @param ipv4    String
     * @param handler Handler
     * @return boolean
     */
    public boolean connect(String ipv4, final Handler handler) {
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

            // Define the listener
            this.listener = this.createKryonetClientListener(handler);

            // Register a listener so that we forward received Messages to GameController
            this.client.addListener(this.listener);

            // Try to connect to given ipv4 with 5000 secs timeout
            this.client.connect(5000, ipv4, tcpPort, udpPort);

            // Set Server IPV4 for successful connection
            this.setServerIP(ipv4);

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


        // Since both of them have been implemented interface, classify them to instance for later
        // operation
        EndPoint instance = this.isHost ? this.server : this.client;

        // Detached the listener from the instance
        instance.removeListener(this.listener);

        // Set the lister to be null
        this.listener = null;

        // Stop the EndPoint operation
        instance.stop();

        // Stop all connections
        instance.close();

    }

    /**
     * API for GameController to send message to the server
     *
     * @param obj Object
     */
    public void sendMessageToServer(Object obj) {
        if (!this.isHost) {
            this.client.sendTCP(obj);
        }
    }

    /**
     * API for GameController to broadcast message to the clients
     *
     * @param obj Object
     */
    public void broadcastMessage(Object obj) {
        this.server.sendToAllTCP(obj);
    }

    /**
     * Helper method create a Listener for listening events from Kryonet's client
     *
     * @param handler A handler for GameServer to forward messages back to GameController
     */
    protected Listener createKryonetClientListener(final Handler handler) {
        return new Listener() {
            /**
             * Handler for receiving messages from peers
             * @param c Connection
             * @param obj Object. Message specific
             */
            public void received(Connection c, Object obj) {
                String ipv4 = c.getRemoteAddressTCP().getAddress().getHostAddress();
                Log.d(TAG, String.format("Client received message from ip |%s|", ipv4));

                Message msg = handler.obtainMessage(ON_RECEIVED_MSG, obj);
                msg.sendToTarget();
            }

            /**
             * Handler for server disconnection
             * @param c Connection
             */
            public void disconnected(Connection c) {
                Log.d(TAG, "Server disconnected");
                Message msg = handler.obtainMessage(ON_SERVER_DISCONNECTED);
                msg.sendToTarget();
            }
        };
    }

    /**
     * Helper method create a Listener for listening events from Kryonet's server
     *
     * @param handler A handler for GameServer to forward messages back to GameController
     */
    protected Listener createKryonetServerListener(final Handler handler) {
        return new Listener() {
            /**
             * Handler for handling incoming client connection
             * @param c Connection
             */
            public void connected(Connection c) {
                String ipv4 = c.getRemoteAddressTCP().getAddress().getHostAddress();
                Log.d(TAG, String.format("Connected IP |%s|", ipv4));

                GameConnection gameConnection = (GameConnection) c;
                gameConnection.ipv4 = ipv4;
            }

            /**
             * Handler for receiving messages from peers
             * @param c Connection
             * @param obj Object. Message specific
             */
            public void received(Connection c, Object obj) {
                String ipv4 = c.getRemoteAddressTCP().getAddress().getHostAddress();
                Log.d(TAG, String.format("Server received message from ip |%s|", ipv4));

                Message msg = handler.obtainMessage(ON_RECEIVED_MSG, obj);
                msg.sendToTarget();
            }

            /**
             * Handler for client disconnection
             * @param c Connection
             */
            public void disconnected(Connection c) {
                GameConnection gameConnection = (GameConnection) c;
                Log.d(TAG, String.format("Client |%s| disconnected", gameConnection.ipv4));

                Message msg = handler.obtainMessage(ON_CLIENT_DISCONNECTED, gameConnection.ipv4);
                msg.sendToTarget();
            }
        };
    }

    /**
     * A method registers all classes which be sent over the Kryonet network
     *
     * @param instance Kryo
     */
    protected void register(Kryo instance) {
        instance.register(GameController.JoinHostRequest.class);
        instance.register(GameController.GetPlayerListRequest.class);
        instance.register(GameController.JoinHostResponse.class);
        instance.register(GameController.UpdatePlayerListNotification.class);
        instance.register(GameController.KickedNotification.class);
        instance.register(GameController.ServerDownNotification.class);
        instance.register(java.util.ArrayList.class);
        instance.register(GamePlayer.class);
    }
}
