package com.denyandconquer.net;

import com.denyandconquer.common.Player;

import java.io.Serializable;

public class CreateRoomRequest implements Serializable {
    private final String roomName;
    private final int maxPlayers;
    private final boolean isPrivate;
    private final String password;
    private final Player owner;

    public CreateRoomRequest(String roomName, int maxPlayers, boolean isPrivate, String password, Player owner) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.password = password;
        this.owner = owner;
    }

    public String getRoomName() { return roomName; }
    public int getMaxPlayers() { return maxPlayers; }
    public boolean isPrivate() { return isPrivate; }
    public String getPassword() { return password; }
    public Player getOwner() { return owner; }
}

