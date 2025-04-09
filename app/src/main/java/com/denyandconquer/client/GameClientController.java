package com.denyandconquer.client;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.net.MouseData;
import javafx.scene.input.MouseEvent;

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

    public void updatePlayerList(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    public void mouseAction(MouseEvent e) {
        client.sendMouseAction(e);
    }

    /**
     * Handles mouse actions on the game board.
     * @param data
     */
    public boolean handleMouseAction(Player player, MouseData data) {
        Square square = board.getSquare(data.getRow(), data.getCol());
        if (square == null) return false;
        System.out.println("Player " + player.getName() + " performed action: " + data.getAction() + " on square: " + square + "player color: " + player.getColor());

        switch (data.getAction()) {
            case PRESS -> {
                System.out.println("[Client] player color: " + player.getColorHex());
                System.out.println("[Client] local player color: " + localPlayer.getColorHex());
                boolean samePlayer = player.getColorHex().equals(localPlayer.getColorHex());
                System.out.println("Same? - " + samePlayer);
                return board.pressBy(player, data.getRow(), data.getCol(), samePlayer);
            }
            case DRAG -> {
                return square.draw(player, data.getX(), data.getY());
            }
            case RELEASE -> {
                return board.release(player, data.isFilled());
            }
            default -> {
                return false;
            }
        }
    }
}
