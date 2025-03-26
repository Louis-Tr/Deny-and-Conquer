package com.denyandconquer.server;

import java.io.Serializable;
import java.util.*;

public class Room implements Serializable{
    private static final long serialVersionUID = 1L;
    private String roomName;
    private int roomId;
    private List<GameThread> playerList;
    private int maxPlayers;

    public Room(String roomName, int roomId, int maxPlayers) {
        this.roomName = roomName;
        this.roomId = roomId;
        this.maxPlayers = maxPlayers;
        this.playerList = new ArrayList<>();
    }
    public String getRoomName() {
        return roomName;
    }
    public int getRoomId() {
        return roomId;
    }
    public List<GameThread> getPlayers() {
        return playerList;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public void removePlayer(GameThread player) {
        playerList.remove(player);
    }
    public boolean isFull(GameThread player) {
        return playerList.size() >= maxPlayers;
    }
    public boolean addPlayer(GameThread player) {
        boolean flag = false;
        if(playerList.size() < maxPlayers) {
            playerList.add(player);
            flag = true;
        }
        return flag;
    }
    public List<GameThread> getPlayerList() {
        return playerList;
    }
}
