package com.denyandconquer.common;

import com.denyandconquer.servers.GameController;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a multiplayer game room with player list, settings, and game state.
 */
public class GameRoom implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String roomId;
    private final String roomName;
    private final boolean isPrivate;
    private final String password;
    private final int maxPlayers;
    private final List<Player> playerList = new ArrayList<>();
    private boolean gameStarted = false;
    private GameController gameController;

    public GameRoom(String roomName, int maxPlayers, boolean isPrivate, String password) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.password = isPrivate ? password : null;
        this.gameController = new GameController(playerList);
    }

    private Color getUniqueColor(int index) {
        // Use distinct hues based on current number of players
        double hue = (index * 360.0 / maxPlayers) % 360;
        return Color.hsb(hue, 0.9, 0.9); // Bright, saturated color
    }

    private void assignColors() {
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            player.setColor(getUniqueColor(i)); // Assign unique color before adding
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isFull() {
        return playerList.size() >= maxPlayers;
    }

    public boolean hasStarted() {
        return gameStarted;
    }

    public List<Player> getPlayerList() {
        return Collections.unmodifiableList(playerList);
    }

    public boolean checkPassword(String input) {
        return !isPrivate || (password != null && password.equals(input));
    }

    public boolean addPlayer(Player player) {
        if (isFull() || playerList.contains(player) || gameStarted) return false;
        return playerList.add(player);
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
        // Optionally: auto-destroy room if empty and game not started
    }



    @Override
    public String toString() {
        return roomName + " (" + playerList.size() + "/" + maxPlayers + ")";
    }

    public GameController getGameController() {
        return gameController;
    }

    public void startGame() {
        if (!gameStarted && playerList.size() >= 2) {
            assignColors();
            this.gameStarted = true;
            this.gameController.updatePlayerList(playerList);
            gameController.startGame();
        }
    }

    public void endGame() {
        gameStarted = false;
    }

    public String getPassword() {
        return password;
    }

    public void updatePlayerList(List<Player> players) {
        for (Player player : players) {
            if (!playerList.contains(player)) {
                playerList.add(player);
            }
        }
    }
}
