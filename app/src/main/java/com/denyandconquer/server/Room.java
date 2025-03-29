package com.denyandconquer.server;

import java.io.Serializable;
import java.util.*;

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
            for(Player p:playerList){
                System.out.println(p.getName());
            }
            flag = true;
        }
        return flag;
    }
    public List<Player> getPlayerList() {
        return playerList;
    }
}
