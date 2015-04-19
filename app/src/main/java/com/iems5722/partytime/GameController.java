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

    // Determine whether server is active or not
    protected boolean isGameServerActive = false;

    // Define MAX_PLAYERS
    public static final int MAX_PLAYERS = 4;

    // Predefine usernames
    protected ArrayList<String> usernames = null;

    // Predefine player icon resources
    protected ArrayList<Integer> playerIconResouce = null;

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

    // Define list of ACTION_CODE for android.os.handler
    public static final int CONNECT_GAMESERVER_FAIL = -1;
    public static final int CONNECT_GAMESERVER_SUCCESS = 0;

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
        boolean ret = this.gs.setup(ipv4);

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
     * @param ipv4    final String
     * @param handler final Handler
     */
    public void connectToGameServer(final String ipv4, final Handler handler) {
        final GameController self = GameController.this;

        this.networkCallsThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // Connect to given ipv4
                boolean ret = self.gs.connect(ipv4);
                Message msg;

                // Determine the status of the connection
                if (ret) {
                    // Able to connect to ipv4
                    self.isGameServerActive = true;
                    msg = handler.obtainMessage(CONNECT_GAMESERVER_SUCCESS);
                } else {
                    // Unable to connect to ipv4
                    self.isGameServerActive = false;
                    msg = handler.obtainMessage(CONNECT_GAMESERVER_FAIL);
                }

                // Send message back to the caller activity
                msg.sendToTarget();
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
    public GamePlayer createGameClient(String ipv4) {
        // Get list of players currently
        ArrayList<GamePlayer> players = this.gs.getPlayers();

        // Fetching username
        int index = players.size();
        String username = this.usernames.get(index);

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
