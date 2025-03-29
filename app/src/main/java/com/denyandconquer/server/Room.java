package com.denyandconquer.server;

import java.io.Serializable;
import java.util.*;

/**
 * The Room class represents a game room.
 * It contains room name, unique room id, and players in the room.
 * It manages adding and removing players.
 * It checks if the room is full.
 * It is serializable.
 */
public class Room implements Serializable{
    private static final long serialVersionUID = 1L;
    private String roomName;
    private int roomId;
    private List<Player> playerList;
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
    public List<Player> getPlayers() {
        return playerList;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public void removePlayer(Player player) {
        playerList.remove(player);
    }
    public boolean isFull() {
        return playerList.size() >= maxPlayers;
    }
    public boolean addPlayer(Player player) {
        boolean flag = false;
        if(playerList.size() < maxPlayers) {
            playerList.add(player);
            flag = true;
        }
        return flag;
    }
    public List<Player> getPlayerList() {
        return playerList;
    }
}
