package com.denyandconquer.net;

import com.denyandconquer.common.Player;

import java.io.Serializable;

public class MouseData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;
    private final double x;
    private final double y;
    private final MouseAction action;
    private Player player;
    private boolean filled;

    public MouseData(int row, int col, double x, double y, MouseAction action) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public MouseAction getAction() {
        return action;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
