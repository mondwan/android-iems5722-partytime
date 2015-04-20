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

    // Define a player list
    protected final ArrayList<GamePlayer> playerList;

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
        public boolean isSuccess;
        public String requestIP;
        public String serverIP;
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
        // Instantiate playerList
        this.playerList = new ArrayList<>();

        // Define myHandler
        this.myHandler = new Handler() {
            /**
             * A handler for GameServer to forward message to us
             *
             * @param inputMessage Message
             */
            @Override
            public void handleMessage(Message inputMessage) {
                // Setup a reference for this handler to access our activity
                final GameController self = GameController.this;

                // Setup a reference for the message payload
                final Object obj = inputMessage.obj;

                // A message reference will be used later on
                Message msg;

                // A updateNotification reference
                final UpdatePlayerListNotification updatePlayerListNotification =
                        new UpdatePlayerListNotification();

                // Determine type of the input message
                switch (inputMessage.what) {
                    case GameServer.ON_CLIENT_DISCONNECTED:
                        String clientIpv4 = (String) obj;
                        self.removePlayer(clientIpv4);

                        updatePlayerListNotification.players = self.playerList;

                        msg = activityHandler.obtainMessage(
                                UPDATE_PLAYER_LIST_NOTIFICATION,
                                updatePlayerListNotification
                        );
                        msg.sendToTarget();

                        // Broadcast UPDATE_PLAYER_LIST_NOTIFICATION
                        // Start a new thread to broadcast messages to peers
                        self.networkCallsThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                self.gs.broadcastMessage(updatePlayerListNotification);
                            }
                        });
                        break;
                    case GameServer.ON_SERVER_DISCONNECTED:
                        // Forward the message to the registered handler
                        msg = activityHandler.obtainMessage(SERVER_DOWN_NOTFICATION);
                        msg.sendToTarget();

                        // Stop our client here since server has been shutdown
                        self.stopGameServer();
                        break;
                    case GameServer.ON_RECEIVED_MSG:
                        if (obj instanceof JoinHostRequest) {
                            // Classify the object to JoinHostRequest
                            JoinHostRequest req = (JoinHostRequest) obj;

                            // Logging
                            Log.d(
                                    TAG,
                                    String.format(
                                            "Join host request from |%s| received",
                                            req.requestIP
                                    )
                            );

                            // Try to add player
                            boolean status = self.addPlayer(req.requestIP);

                            // Create a JoinHostResponse message
                            final JoinHostResponse res = new JoinHostResponse();
                            res.isSuccess = status;
                            res.requestIP = req.requestIP;
                            res.serverIP = self.getServerIP();

                            // Forward the message to the register activity
                            msg = activityHandler.obtainMessage(JOIN_HOST_RESPONSE, res);
                            msg.sendToTarget();

                            // Send a notification about updating a player list
                            if (res.isSuccess) {
                                updatePlayerListNotification.players = self.playerList;
                                msg = activityHandler.obtainMessage(
                                        UPDATE_PLAYER_LIST_NOTIFICATION,
                                        updatePlayerListNotification
                                );
                                msg.sendToTarget();
                            }

                            // Start a new thread to broadcast messages to peers
                            self.networkCallsThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    // Broadcast the JoinHostResponse
                                    self.gs.broadcastMessage(res);

                                    // Broadcast playerList update only if JoinHost is success
                                    if (res.isSuccess) {
                                        updatePlayerListNotification.players = self.playerList;
                                        self.gs.broadcastMessage(updatePlayerListNotification);
                                    }
                                }
                            });
                        } else if (obj instanceof LeaveHostRequest) {
                            activityHandler.obtainMessage(LEAVE_HOST_REQUEST, obj);
                        } else if (obj instanceof GetPlayerListRequest) {
                            activityHandler.obtainMessage(GET_PLAYER_LIST_REQUEST, obj);
                        } else if (obj instanceof JoinHostResponse) {
                            // Cast to JoinHostResponse
                            JoinHostResponse res = (JoinHostResponse) obj;

                            Log.d(
                                    TAG,
                                    String.format(
                                            "Join host request from |%s| received",
                                            res.requestIP
                                    )
                            );

                            // Forward message to the registered activity
                            msg = activityHandler.obtainMessage(JOIN_HOST_RESPONSE, obj);
                            msg.sendToTarget();
                        } else if (obj instanceof UpdatePlayerListNotification) {
                            Log.d(TAG, "UpdatePlayerList notification received");

                            // Update our player list
                            updatePlayerListNotification.players =
                                    ((UpdatePlayerListNotification) obj).players;

                            // Destroy the original one
                            self.playerList.clear();

                            // Add elements in the request
                            self.playerList.addAll(updatePlayerListNotification.players);

                            // Forward message to register activity
                            msg = activityHandler.obtainMessage(
                                    UPDATE_PLAYER_LIST_NOTIFICATION,
                                    updatePlayerListNotification
                            );
                            msg.sendToTarget();
                        } else if (obj instanceof KickedNotification) {
                            activityHandler.obtainMessage(KICKED_NOTIFICATION, obj);
                        }
                        break;
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
            this.addPlayer(ipv4);
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
                    obj.requestIP = self.localIP;
                    obj.serverIP = ipv4;

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
            // Disable game server status
            this.isGameServerActive = false;

            // Stop the game server
            this.gs.stop();

            // Cancel handler from activity
            this.cancelHandler();

            // Empty player list
            this.playerList.clear();
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
     * API which remove a player from the playList
     *
     * @param ipv4 String
     */
    public void removePlayer(String ipv4) {
        ArrayList<GamePlayer> tmp = new ArrayList<>();

        for (GamePlayer player : this.playerList) {
            if (!player.getIp().equals(ipv4)) {
                tmp.add(player);
            }
        }

        this.playerList.clear();
        this.playerList.addAll(tmp);
    }

    /**
     * API which creates a GamePlayer for GameServer
     *
     * @param ipv4 String
     * @return boolean
     */
    public boolean addPlayer(String ipv4) {
        boolean ret = false;

        // Get # of player
        int numOfPlayer = this.playerList.size();

        if (numOfPlayer < MAX_PLAYERS) {
            // Player position starts from zero
            int position = 0;

            // Assume ordering equals to the player position
            for (GamePlayer player : this.playerList) {
                // Check out whether position has been occupied or not
                if (player.position == position) {
                    position++;
                } else {
                    break;
                }
            }

            // Fetch username
            String username = this.listOfUsername.get(position);

            // Instantiating GameClient
            GamePlayer player = new GamePlayer(ipv4, username, position);

            // Add a player to the playerList
            this.playerList.add(position, player);

            ret = true;
        }

        return ret;
    }

    /**
     * API for fetching player list from GameServer
     *
     * @return ArrayList\<GameClient\>
     */
    public ArrayList<GamePlayer> getPlayerList() {
        return this.playerList;
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
