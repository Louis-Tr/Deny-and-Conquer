package com.denyandconquer.common;

import org.apache.commons.lang3.ObjectUtils.Null;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Square {
    private Point2D position; // location in board
    private Player currentPlayerColor; // who colored in this square.
    private Color color;
    private Boolean isLocked;
    private Integer filledPixels;

    public Square(Point2D inputPosition) {
        this.position = inputPosition;
        this.currentPlayerColor = null;
        this.color = Color.rgb(255, 255, 255);
        this.isLocked = false;
        this.filledPixels = 0;

    }

    public boolean isWhite() {
        Color white = Color.rgb(255, 255, 255);
        if (color.equals(white)) {
            return true;
        }

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
