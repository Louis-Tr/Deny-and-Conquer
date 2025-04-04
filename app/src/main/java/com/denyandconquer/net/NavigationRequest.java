package com.denyandconquer.net;

import java.io.Serializable;

public class NavigationRequest implements Serializable {
    private final String roomID;
    private final String password;
    private final int maxPlayers;
    private final String roomName;

    // --- Constructors ---

    // Join room by ID only
    public NavigationRequest(String roomID) {
        this.roomID = roomID;
        this.password = null;
        this.maxPlayers = -1;
        this.roomName = null;
    }

    // Join room by ID + password
    public NavigationRequest(String roomID, String password) {
        this.roomID = roomID;
        this.password = password;
        this.maxPlayers = -1;
        this.roomName = null;
    }

    // Join room by ID + max players (rare use case)
    public NavigationRequest(String roomID, int maxPlayers) {
        this.roomID = roomID;
        this.password = null;
        this.maxPlayers = maxPlayers;
        this.roomName = null;
    }

    // Join room by ID + password + max players
    public NavigationRequest(String roomID, String password, int maxPlayers) {
        this.roomID = roomID;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.roomName = null;
    }

    // --- NEW --- Create room with name and max players
    public NavigationRequest(String roomName, int maxPlayers, String password) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.password = password;
        this.roomID = null;
    }

    // --- Getters ---

    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    // --- Optional Checks ---

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    public boolean hasMaxPlayers() {
        return maxPlayers > 0;
    }

    public boolean hasRoomName() {
        return roomName != null && !roomName.isEmpty();
    }
}
