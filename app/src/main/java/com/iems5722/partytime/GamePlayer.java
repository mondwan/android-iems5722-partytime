package com.iems5722.partytime;

/**
 * A class holds information about a player
 */
public class GamePlayer {
    protected String username;
    protected String ip;

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public GamePlayer(String ip, String username) {
        this.ip = ip;
        this.username = username;
    }
}
