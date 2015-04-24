package com.iems5722.partytime;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Define common methods for dealing with scoring
 */
public class ScoresUtils {

    protected GameController gameController = null;
    private String TAG = "";

    public ScoresUtils(String TAG) {
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

    public ArrayList<GamePlayer> getPlayerList() {
        gameController = GameController.getInstance();
        return gameController.getPlayerList();
    }

    public ArrayList<String> getSortedScoreText() {
        ArrayList<String> scoreList = new ArrayList<String>();
        ArrayList<GamePlayer> gamePlayers = getPlayerList();

        // Sort the gamePlayer list with the score
        Collections.sort(gamePlayers, new Comparator<GamePlayer>() {
            @Override
            public int compare(GamePlayer lhs, GamePlayer rhs) {
                return rhs.getScores() - lhs.getScores();
            }
        });

        for (int i = 0; i < gamePlayers.size(); i++) {
            scoreList.add(gamePlayers.get(i).getUsername()
                    + ": " + gamePlayers.get(i).getScores());
        }
        return scoreList;
    }
}
