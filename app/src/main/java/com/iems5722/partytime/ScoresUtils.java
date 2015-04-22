package com.iems5722.partytime;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by chan on 4/22/2015.
 */
public class ScoresUtils {

    protected GameController gameController = null;
    private String TAG = "";
    public ScoresUtils(String TAG){
        this.TAG = TAG;
    }

    public int scoresUpdate(int diff) {

        int scores = 0;
        gameController = GameController.getInstance();
        final GameController.UpdateScoresRequest req = new GameController.UpdateScoresRequest();
        final GameController.UpdateScoresResponse res = new GameController.UpdateScoresResponse();
        if (gameController.isHost()) {
            GamePlayer player = gameController.getGamePlayer(gameController.localIP);
            res.scores = player.getScores() + diff;
            res.requestIP = gameController.localIP;
            res.serverIP = gameController.getServerIP();
            gameController.sendMsg(res);
            gameController.setGamePlayerScores(res.serverIP, res.scores);
            Log.d(TAG, String.format("Scores Update(Server self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
            scores = res.scores;
        } else {
            GamePlayer player = gameController.getGamePlayer(gameController.localIP);
            req.scores = player.getScores() + diff;
            req.requestIP = gameController.localIP;
            gameController.sendMsg(req);
            gameController.setGamePlayerScores(req.requestIP, req.scores);
            Log.d(TAG, String.format("Scores Update(Client self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
            scores = req.scores;
        }
        return scores;
    }

    public ArrayList<GamePlayer> getPlayerList(){
        gameController = GameController.getInstance();
        return gameController.getPlayerList();
    }

    public int getMyScores(){
        return scoresUpdate(0);
    }
}
