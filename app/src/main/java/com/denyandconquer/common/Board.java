package com.denyandconquer.common;

import javafx.scene.canvas.Canvas;

import java.awt.geom.Point2D.Double;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Board {
    private static final int SIZE = 8;
    private final Square[][] grid = new Square[SIZE][SIZE];
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Board() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Square(x, y);
            }
        }
    }

    public Square getSquareByCanvas(Canvas canvas) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Square square = grid[row][col];
                if (square.getCanvas() == canvas) return square;
            }
        }
        return null;
    }

    /**
     * Returns the square at canvas pixel (x, y), or null if out of bounds.
     */
    public Square getSquare(double x, double y) {
        lock.readLock().lock();
        try {
            int col = (int) (x / Square.WIDTH);
            int row = (int) (y / Square.HEIGHT);
            if (col < 0 || col >= SIZE || row < 0 || row >= SIZE) return null;
            return grid[row][col];
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns the square at (row, col), or null if out of bounds.
     */
    public Square getSquare(int row, int col) {
        lock.readLock().lock();
        try {
            if (col < 0 || col >= SIZE || row < 0 || row >= SIZE) return null;
            return grid[row][col];
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Returns the board size (width and height).
     */
    public int getSize() {
        return SIZE;
    }

    public void reset() {
        for (Square[] square : grid) {
            for (Square each : square) {
                each.reset();
            }
        }
    }


    public boolean release(Player player) {
        for (Square[] square : grid) {
            for (Square each : square) {
                if(each.getLockedBy() == player) {
                    return each.release(player);
                }
            }
        }
        return false;
    }

    public boolean pressBy(Player player, int row, int col) {
        for (Square[] square : grid) {
            for (Square each : square) {
                if (each.getLockedBy() == player) {
                    return false;
                }
            }
        }
        Square square = getSquare(row, col);
        if (!square.isLock()) {
            square.pressBy(player);
            return true;
        } else {
            return false;
        }
    }
}
