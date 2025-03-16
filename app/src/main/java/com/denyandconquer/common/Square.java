package com.denyandconquer.common;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Square {
    private Point2D position;
    private Player owner; // who colored in this square.
    private Color color;
    private Boolean isLocked;
    private Integer filledPixels;
    private Integer SQUARE_SIZE;

    public Square(Point2D inputPosition) {
        position = inputPosition;
    }

    public boolean isWhite() {

        return false;
    }

    public void lockSquare() {
        isLocked = true;
    }

    public void unlockSquare() {
        isLocked = false;
    }

    public Float calculateFillPercent() { return 0.0F; }

    public void completeDrawing(Player player) {} // if percent > 50% then player owns square

    public void resetToWhite() {}

    
}
