package com.denyandconquer.net;

import com.denyandconquer.common.Player;
import javafx.geometry.Point2D;
import java.io.Serializable;

public class DrawData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Player player;
    private final int boardX;
    private final int boardY;
    /**
     * Constructs a DrawData instance.
     *
     * @param player        the player who drew on the tile
     * @param boardX        the x-coordinate of the tile on the board
     * @param boardY        the y-coordinate of the tile on the board
     */
    public DrawData(Player player, int boardX, int boardY) {
        this.player = player;
        this.boardX = boardX;
        this.boardY = boardY;
    }

    public Player getPlayer() {
        return player;
    }

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    /**
     * Returns a JavaFX Point2D constructed from the stored local coordinates.
     * This method is for convenience in the UI code.
     *
     * @return a new Point2D representing the local drawing position.
     */
    public Point2D getLocalPosition() {
        return new Point2D(localX, localY);
    }
}
