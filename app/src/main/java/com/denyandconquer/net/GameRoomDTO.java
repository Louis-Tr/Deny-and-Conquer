package com.denyandconquer.net;

import com.denyandconquer.common.Player;
import com.denyandconquer.servers.GameRoom;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable data-only version of GameRoom for client use.
 */
public class GameRoomDTO implements Serializable {
    private final String roomId;
    private final String roomName;
    private final boolean isPrivate;
    private final int maxPlayers;
    private final int currentPlayers;
    private final boolean gameStarted;
    private final List<Player> playerList;

    public GameRoomDTO(GameRoom room) {
        this.roomId = room.getRoomId();
        this.roomName = room.getRoomName();
        this.isPrivate = room.isPrivate();
        this.maxPlayers = room.getMaxPlayers();
        this.currentPlayers = room.getPlayerList().size();
        this.gameStarted = room.hasStarted();
        this.playerList = room.getPlayerList();
    }

    // Getters
    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public boolean isPrivate() { return isPrivate; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getCurrentPlayers() { return currentPlayers; }
    public boolean isGameStarted() { return gameStarted; }
    public List<Player> getPlayerList() { return playerList; }

    @Override
    public String toString() {
        return roomName + " (" + currentPlayers + "/" + maxPlayers + ")" + (isPrivate ? " 🔒" : "");
    }
}
