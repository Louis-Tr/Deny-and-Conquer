package com.denyandconquer.common;

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

    /**
     * Returns the square at (x, y), or null if out of bounds.
     */
    public Square getSquare(int x, int y) {
        lock.readLock().lock();
        try {
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return null;
            return grid[x][y];
        } finally {
            lock.readLock().unlock();
        }
    }

    public Square getSquare(Double position) {
        return getSquare((int) position.getX(), (int) position.getY());
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
}
