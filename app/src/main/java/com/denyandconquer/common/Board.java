package com.denyandconquer.common;

import javafx.geometry.Point2D;

public class Board {
    public static final int GRID_SIZE = 1;
    public Square[][] grid; // 8 x 8 board
    public int emptySquares; // 64 total squares

    public Board() {
        this.grid = new Square[GRID_SIZE][GRID_SIZE];
        this.emptySquares = GRID_SIZE * GRID_SIZE;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                grid[row][col] = new Square(new Point2D(col, row));
            }
        }
    }

    public Boolean isGameComplete() {
        return emptySquares == 0;
    }

    public void decreaseEmptySquares() {
        emptySquares--;
    }



}
