package com.denyandconquer.servers;

import com.denyandconquer.common.GameRoom;
import com.denyandconquer.common.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all game rooms in the lobby.
 */
public class LobbyManager {
    private final Map<String, GameRoom> roomMap = new ConcurrentHashMap<>(); // roomId → GameRoom
    private final Set<String> playerNames = ConcurrentHashMap.newKeySet();   // to enforce unique names

    /** Creates a new game room if name is unique. */
    public GameRoom createRoom(String roomName, int maxPlayers, boolean isPrivate, String password) {
        System.out.println("Creating room: " + roomName);
        // Prevent duplicate room names
        if (roomMap.values().stream().anyMatch(r -> r.getRoomName().equalsIgnoreCase(roomName))) {
            return null;
        }

        GameRoom room = new GameRoom(roomName, maxPlayers, isPrivate, password);
        roomMap.put(room.getRoomId(), room);
        return room;
    }

    /** Returns all rooms (for client list updates). */
    public List<GameRoom> getAllRooms() {
        return new ArrayList<>(roomMap.values());
    }

    /** Attempts to join a room by ID. */
    public boolean joinRoom(String roomId, Player player, String password) {
        GameRoom room = roomMap.get(roomId);
        if (room == null || !room.checkPassword(password)) return false;
        if (!isNameUnique(player.getName())) return false;

        boolean added = room.addPlayer(player);
        if (added) {
            playerNames.add(player.getName());
        }
        return added;
    }

    /** Removes a player from a room. */
    public void leaveRoom(String roomId, Player player) {
        GameRoom room = roomMap.get(roomId);
        if (room != null) {
            room.removePlayer(player);
            playerNames.remove(player.getName());

            // Optionally clean up empty rooms
            if (room.getPlayerList().isEmpty() && !room.hasStarted()) {
                roomMap.remove(roomId);
            }
        }
    }

    /** Get room by ID. */
    public GameRoom getRoom(String roomId) {
        return roomMap.get(roomId);
    }

    /** Get room by Name. */
    public GameRoom getRoomByName(String roomName) {
        return roomMap.values().stream()
                .filter(room -> room.getRoomName().equalsIgnoreCase(roomName))
                .findFirst()
                .orElse(null);
    }

    /** Checks if a player name is already taken (globally). */
    public boolean isNameUnique(String name) {
        return !playerNames.contains(name);
    }

    /** Adds a player name manually (used on connection before joining). */
    public boolean registerPlayerName(String name) {
        if (!isNameUnique(name)) return false;
        playerNames.add(name);
        return true;
    }

    /** Removes a player name manually (used on disconnect). */
    public void unregisterPlayerName(String name) {
        playerNames.remove(name);
    }
}
