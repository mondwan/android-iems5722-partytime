package com.iems5722.partytime;

/**
 * A class holds information about a player
 */
public class GamePlayer {
    protected String username;
    protected String ip;
    protected int position;
    protected int scores;

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    // Create an empty constructor required by Kryonet
    public GamePlayer() {

    }

    public GamePlayer(String ip, String username, int pos) {
        this.ip = ip;
        this.username = username;
        this.position = pos;
        this.scores = 0;
    }
}
