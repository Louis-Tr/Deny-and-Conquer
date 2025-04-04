package com.denyandconquer.servers;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.net.MouseData;


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
        System.out.println("GameController initialized");
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
     * Handles mouse actions on the game board.
     * @param data
     */
    public boolean handleMouseAction(Player player, MouseData data) {
        System.out.println("🖱️ Handling mouse action: " + data.getAction()
                + " by " + player.getName()
                + " at square (" + data.getRow() + ", " + data.getCol() + ")"
                + " local (" + data.getX() + ", " + data.getY() + ")");

        Square square = board.getSquare(data.getRow(), data.getCol());
        if (square == null) {
            System.out.println("⚠️ Square is null at row=" + data.getRow() + ", col=" + data.getCol());
            return false;
        }

        boolean result = switch (data.getAction()) {
            case PRESS -> board.pressBy(player, data.getRow(), data.getCol());
            case DRAG -> square.draw(player, data.getX(), data.getY());
            case RELEASE -> board.release(player);
            default -> false;
        };

        if (result) {
            System.out.println("✅ Square updated");
        }

        return result;
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
