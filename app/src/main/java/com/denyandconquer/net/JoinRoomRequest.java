package com.denyandconquer.net;

import com.denyandconquer.common.Player;

import java.io.Serializable;

public class JoinRoomRequest implements Serializable {
    private final Player player;
    private final String roomId;

    public JoinRoomRequest(Player player, String roomId) {
        this.player = player;
        this.roomId = roomId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "playerId='" + player + '\'' +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}
