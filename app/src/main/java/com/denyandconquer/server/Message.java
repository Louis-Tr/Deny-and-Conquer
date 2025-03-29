package com.denyandconquer.server;

import java.io.Serializable;

/**
 * The Message class is used to sum up data that is sent between network.
 * It contains the message type, data, and other related information.
 * It is serializable.
 */
public class Message  implements Serializable {

    public enum Type {
        CREATE_ROOM,
        JOIN_ROOM,
        LEAVE_ROOM,
        ENTER_ROOM,
        ROOM_LIST,
        PLAYER_LIST,
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
