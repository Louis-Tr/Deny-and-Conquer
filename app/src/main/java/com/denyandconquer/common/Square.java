package com.denyandconquer.common;

import org.apache.commons.lang3.ObjectUtils.Null;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Square {
    private Point2D position; // location in board
    private Player currentPlayerColor; // who colored in this square.
    private Color color;
    private Boolean isLocked;
    private Player owner;

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Square(Point2D inputPosition) {
        this.position = inputPosition;
        this.currentPlayerColor = null;
        this.color = Color.WHITE;
        this.isLocked = false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Player getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public boolean isWhite() {
        return color.equals(Color.WHITE); 
    }

    public void lockSquare() {
        isLocked = true;
    }

    public void unlockSquare() {
        isLocked = false;
    }

    public void completeDrawing(Player player) {} // if percent > 50% then player owns square

    public void resetToWhite() {
        this.color = Color.WHITE;
    }

    
}
