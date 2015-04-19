package com.iems5722.partytime;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    // Local device IP;
    protected String localIP;

    // Determine whether server is active or not
    protected boolean isGameServerActive = false;

    // Predefine list of username
    protected ArrayList<String> listOfUsername = null;

    // Predefine player icon resources
    protected ArrayList<Integer> playerIconResource = null;

    // A handler for us to received message from GameServer
    protected Handler myHandler;

    // A handler for us to reply message to Activity
    protected Handler activityHandler;

    // Thread pool for communications
    protected final ThreadPoolExecutor networkCallsThreadPool;

    // Communication work queue
    protected final BlockingQueue<Runnable> networkCallsQueue;

    // Define # of cores
    protected static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the amount of time an idle thread will wait for a task before terminating
    protected static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    protected final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Define MAX_PLAYERS
    public static final int MAX_PLAYERS = 4;

    // List of ACTION_CODE for handlers from Activity
    public static final int JOIN_HOST_REQUEST = 2;
    public static final int LEAVE_HOST_REQUEST = 3;
    public static final int GET_PLAYER_LIST_REQUEST = 4;
    public static final int JOIN_HOST_RESPONSE = 5;
    public static final int UPDATE_PLAYER_LIST_NOTIFICATION = 6;
    public static final int KICKED_NOTIFICATION = 7;
    public static final int SERVER_DOWN_NOTFICATION = 8;

    // Define classes which will be transmitted back and forth in Kryonet network
    public static class JoinHostRequest {
        public String requestIP;
    }

    public static class LeaveHostRequest {
        public String requestIP;
    }

    public static class GetPlayerListRequest {
        //TODO work out properties here
    }

    public static class JoinHostResponse {
        boolean isSuccess;
    }

    public static class UpdatePlayerListNotification {
        public ArrayList<GamePlayer> players;
    }

    public static class KickedNotification {
        //TODO work out properties here
    }

    public static class ServerDownNotification {
        //TODO work out properties here
    }

    /**
     * Singleton implementation
     */
    protected GameController() {
        // Define myHandler
        this.myHandler = new Handler() {
            /**
             * A handler for GameServer to forward message to us
             *
             * @param inputMessage Message
             */
            @Override
            public void handleMessage(Message inputMessage) {
                Object obj = inputMessage.obj;
                if (activityHandler != null) {
                    switch (inputMessage.what) {
                        case GameServer.ON_CLIENT_DISCONNECTED:
                            if (obj instanceof UpdatePlayerListNotification) {
                                activityHandler.obtainMessage(UPDATE_PLAYER_LIST_NOTIFICATION, obj);
                            }
                            break;
                        case GameServer.ON_SERVER_DISCONNECTED:
                            if (obj instanceof ServerDownNotification) {
                                activityHandler.obtainMessage(SERVER_DOWN_NOTFICATION, obj);
                            }
                            break;
                        case GameServer.ON_RECEIVED_MSG:
                            if (obj instanceof JoinHostRequest) {
                                activityHandler.obtainMessage(JOIN_HOST_REQUEST, obj);
                            } else if (obj instanceof LeaveHostRequest) {
                                activityHandler.obtainMessage(LEAVE_HOST_REQUEST, obj);
                            } else if (obj instanceof GetPlayerListRequest) {
                                activityHandler.obtainMessage(GET_PLAYER_LIST_REQUEST, obj);
                            } else if (obj instanceof JoinHostResponse) {
                                activityHandler.obtainMessage(JOIN_HOST_RESPONSE, obj);
                            } else if (obj instanceof UpdatePlayerListNotification) {
                                activityHandler.obtainMessage(UPDATE_PLAYER_LIST_NOTIFICATION, obj);
                            } else if (obj instanceof KickedNotification) {
                                activityHandler.obtainMessage(KICKED_NOTIFICATION, obj);
                            }
                            break;
                    }
                }
            }
        };

        // Instantiate a gameServer instance
        this.gs = GameServer.getInstance();

        // Hard code list of username
        this.listOfUsername = new ArrayList<>();
        this.listOfUsername.add("勇者仁傑");
        this.listOfUsername.add("法師歌莉");
        this.listOfUsername.add("舞者結他他");
        this.listOfUsername.add("八神太一");

        // Hard code list of image resource
        this.playerIconResource = new ArrayList<>();
        this.playerIconResource.add(R.mipmap.p1_icon);
        this.playerIconResource.add(R.mipmap.p2_icon);
        this.playerIconResource.add(R.mipmap.p3_icon);
        this.playerIconResource.add(R.mipmap.p4_icon);

        // Instantiate thread pool for network calls
        // Define timeunit to be seconds
        this.KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Instantiate networkCallsQueue
        this.networkCallsQueue = new LinkedBlockingQueue<>();

        // Instantiate a thread pool
        this.networkCallsThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                this.networkCallsQueue
        );
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
        boolean ret = this.gs.setup(ipv4, this.myHandler);

        if (ret) {
            // Active gameServer
            this.isGameServerActive = true;

            // Server is also a player...
            GamePlayer p = this.createGameClient(ipv4);

            // Append server to player list
            this.gs.addPlayer(p);
        }

        return ret;
    }

    /**
     * API for activities connect to the GameServer.
     * <p/>
     * Note that this method implies the caller is not the host of the gameServer and this is an
     * ASYNC call. Therefore, caller must be provided a handler for GameController to reply the
     * status of connection
     *
     * @param ipv4 final String
     */
    public void connectToGameServer(final String ipv4) {
        final GameController self = GameController.this;

        this.networkCallsThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // Connect to given ipv4
                boolean ret = self.gs.connect(ipv4, self.myHandler);
                Message msg;

                // Determine the status of the connection
                if (ret) {
                    // Able to connect to ipv4
                    self.isGameServerActive = true;

                    // Send a join host request to the server
                    JoinHostRequest req = new JoinHostRequest();
                    req.requestIP = self.localIP;
                    self.gs.sendMessageToServer(req);
                } else {
                    // Unable to connect to ipv4
                    self.isGameServerActive = false;

                    // Create a failure join host response
                    JoinHostResponse obj = new JoinHostResponse();
                    obj.isSuccess = false;

                    // Attach to the message
                    msg = self.activityHandler.obtainMessage(JOIN_HOST_RESPONSE, obj);

                    // Send message back to the caller activity
                    msg.sendToTarget();
                }
            }
        });
    }

    /**
     * API for closing the GameServer
     */
    public void stopGameServer() {
        if (this.isGameServerActive) {
            this.isGameServerActive = false;
            this.gs.stop();
            this.cancelHandler();
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
     * Set local device IP address
     *
     * @param ipv4 String
     */
    public void setLocalIP(String ipv4) {
        this.localIP = ipv4;
    }

    /**
     * API for activities register the handler
     *
     * @param handler Handler
     */
    public void registerHandler(Handler handler) {
        this.activityHandler = handler;
    }

    /**
     * API for activities cancel the handler
     */
    public void cancelHandler() {
        this.activityHandler = null;
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
    public GamePlayer createGameClient(String ipv4) {
        // Get list of players currently
        ArrayList<GamePlayer> players = this.gs.getPlayers();

        // Fetching username
        int index = players.size();
        String username = this.listOfUsername.get(index);

        // Instantiating GameClient
        GamePlayer ret = new GamePlayer(ipv4, username);
        return ret;
    }

    /**
     * API for fetching player list from GameServer
     *
     * @return ArrayList\<GameClient\>
     */
    public ArrayList<GamePlayer> getPlayerList() {
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

        int index = this.listOfUsername.indexOf(username);

        if (index == -1) {
            Log.e(TAG, String.format("Username |%s| cannot map to resource id", username));
            ret = this.playerIconResource.get(this.playerIconResource.size() - 1);
        } else {
            ret = this.playerIconResource.get(index);
        }

        return ret;
    }
}
