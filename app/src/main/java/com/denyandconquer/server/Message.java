package com.denyandconquer.server;

import java.io.Serializable;
public class Message  implements Serializable {

    public enum Type {
        CREATE_ROOM,
        JOIN_ROOM,
        LEAVE_ROOM,
        REFRESH_ROOM,
        REFRESH_PLAYER,
        START_GAME,
        CHECK_SQUARE,
        DRAW_SQUARE,
        FREE_SQUARE
    }

    private Type type;
    private Object data;
    private Player player;
    private int maxPlayers = 0;
    private String roomName;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
    public Message(Type type, Object data, Player player) {
        this.type = type;
        this.data = data;
        this.player = player;
    }
    public Message(Type type, Object data, String roomName, Player player, int maxPlayers) {
        this.type = type;
        this.data = data;
        this.roomName = roomName;
        this.player = player;
        this.maxPlayers = maxPlayers;
    }
    public Message(Type type, String roomName, Player player, int maxPlayers) {
        this.type = type;
        this.roomName = roomName;
        this.player = player;
        this.maxPlayers = maxPlayers;
    }

    public Type getType() {
        return type;
    }
    public Object getData() {
        return data;
    }
    public Player getPlayer() {
        return player;
    }

    public String getRoomName() {
        return roomName;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
}
