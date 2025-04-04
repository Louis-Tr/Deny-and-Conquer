package com.denyandconquer.net;

import com.denyandconquer.common.Player;
import javafx.geometry.Point2D;

import java.io.Serializable;

public class TileClickData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Player player;
    private final int boardX;
    private final int boardY;
    private final double localX;
    private final double localY;

    public TileClickData(Player player, int boardX, int boardY, Point2D localPosition) {
        this.player = player;
        this.boardX = boardX;
        this.boardY = boardY;
        if (localPosition != null) {
            this.localX = localPosition.getX();
            this.localY = localPosition.getY();
        } else {
            this.localX = 0;
            this.localY = 0;
        }
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

    public double getLocalX() {
        return localX;
    }

    public double getLocalY() {
        return localY;
    }

    public Point2D getLocalPosition() {
        return new Point2D(localX, localY);
    }

    @Override
    public String toString() {
        return "TileClickData{" +
                "playerName='" + player.getName() + '\'' +
                ", boardX=" + boardX +
                ", boardY=" + boardY +
                ", localX=" + localX +
                ", localY=" + localY +
                '}';
    }
}
