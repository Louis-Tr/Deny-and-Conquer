package com.denyandconquer.controllers;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.net.DrawData;
import com.denyandconquer.net.TileClickData;
import com.denyandconquer.servers.GameClient;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Controls local game state and delegates player actions via GameClient.
 */
public class GameController {
    private final Board board;
    private final List<Player> players;
    private Player winner;
    private boolean isRunning;

    public GameController(List<Player> players) {
        this.board = new Board();
        this.players = players;
    }

    /** @return The full game board. */
    public Board getBoard() {
        return board;
    }

    /** @return List of all players. */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Handles drawing by delegating to the square.
     */
    public void handleDraw(Player player, DrawData data) {
        Square square = board.getSquare(data.getBoardX(), data.getBoardY());
        if (square != null) {
            Point2D p = data.getLocalPosition();
            square.draw(player, (int) p.getX(), (int) p.getY());
        }
    }

    /**
     * Handles releasing the square.
     */
    public void handleRelease(Player player, DrawData data) {
        Square square = board.getSquare(data.getBoardX(), data.getBoardY());
        if (square != null) {
            square.release();
        }
        winnerCheck();
    }

    /**
     * Handles player click to lock a tile.
     */
    public void handlePress(Player player, TileClickData data) {
        Square square = board.getSquare(data.getBoardX(), data.getBoardY());
        if (square != null) {
            square.pressBy(player);
        }
    }

    public void updatePlayerList(List<Player> playerList) {
        for (Player player : playerList) {
            if (!players.contains(player)) {
                players.add(player);
            }
        }
    }

    public void winnerCheck() {
        int remainingTiles = 0;
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.getSquare(x, y).getOwnedBy() == null) {
                    remainingTiles++;
                }
            }
        }

        for (Player player : players) {
            int maxOtherScore = players.stream()
                    .filter(p -> p != player)
                    .mapToInt(Player::getScore)
                    .max()
                    .orElse(0);

            // If player has a lead greater than all other players + all remaining tiles
            if (player.getScore() > maxOtherScore + remainingTiles) {
                winner = player;
                gameEnd();
                break;
            }
        }

        if (remainingTiles == 0) {
            gameEnd();
        }
    }

    private void gameEnd() {
        this.isRunning = false;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean getGameStatus() {
        return this.isRunning;
    }


    public void startGame() {
        // Initialize game state, shuffle tiles, etc.
        for (Player player : players) {
            player.resetScore();
        }
        this.isRunning = true;
        this.winner = null;
        this.board.reset();
    }
}
