package com.denyandconquer.net;

import com.denyandconquer.common.Player;

import java.io.Serializable;

public class JoinRoomWithPasswordRequest implements Serializable {
    private final Player player;
    private final String roomId;
    private final String password;

    public JoinRoomWithPasswordRequest(Player player, String roomId, String password) {
        this.player = player;
        this.roomId = roomId;
        this.password = password;
    }

    public Player getPlayer() {
        return player;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "JoinRoomWithPasswordRequest{" +
                "player=" + player +
                ", roomId='" + roomId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
