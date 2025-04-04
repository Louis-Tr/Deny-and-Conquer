package com.denyandconquer.common;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.io.Serializable;

public abstract class SquareAbstract implements Serializable {
    protected Point2D position;
    protected Player lockedBy;
    protected Player ownedBy;
    protected Color color;

    protected static final int WIDTH = 50;
    protected static final int HEIGHT = 50;
    protected static final double FILL_THRESHOLD = 0.8;

    public SquareAbstract(Point2D position) {
        this.position = position;
        this.color = Color.WHITE;
    }

    // --- Abstract methods to be implemented by subclasses ---

    public abstract boolean lock(Player player);

    public abstract boolean unlock();

    public abstract void draw(Player player, int x, int y);

    public abstract void release();

    public abstract void reset();

    public abstract double calculateFillPercentage(Color color);

    public abstract void clearCanvas();

    // --- Common accessors ---

    public Point2D getPosition() { return position; }

    public Player getLockedBy() { return lockedBy; }

    public Player getOwnedBy() { return ownedBy; }

    public Color getColor() { return color; }
}
