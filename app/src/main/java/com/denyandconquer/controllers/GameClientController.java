package com.denyandconquer.controllers;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.servers.GameClient;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.List;

/**
 * Client-side controller that sends input to the server via GameClient.
 */
public class GameClientController {
    private final GameClient client;
    private final Board board;
    private final List<Player> players;
    private final Player localPlayer;

    public GameClientController(GameClient client, Board board, List<Player> players, Player localPlayer) {
        this.client = client;
        this.board = board;
        this.players = players;
        this.localPlayer = localPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void clickTile(int x, int y, Point2D localPosition) {
        client.sendLockRequest(x, y, localPosition);
    }

    public void drawOnTile(int x, int y, Point2D localPosition) {
        client.sendDrawAction(x, y, localPosition);
    }

    public void releaseTile(int x, int y) {
        client.sendReleaseRequest(x, y);
    }
}
