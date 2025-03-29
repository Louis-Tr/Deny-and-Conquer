package com.denyandconquer.server;

import java.io.Serializable;

/**
 * The Player class represents a player in the game.
 * It contains the player's name and unique player number.
 * It is serializable.
 */
public class Player implements Serializable {
    private String name;
    private int playerNumber;

    public Player(int playerNumber) {
        this.name = "Player " + playerNumber;
        this.playerNumber = playerNumber;
    }

    public String getName() {
        return name;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public String toString() {
        return name;
    }
}
