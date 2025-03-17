package com.denyandconquer.common;

public class Board {
    Square[][] grid; // 8 x 8 board
    int emptySquareCount; // 64 total squares

    public Board() {
        this.emptySquareCount = 64; 
    }

    public Boolean isGameComplete() {
        if (emptySquareCount == 0) {
            return true;
        }
        return false;
    }

    public void determineWinner() {

    }
}
