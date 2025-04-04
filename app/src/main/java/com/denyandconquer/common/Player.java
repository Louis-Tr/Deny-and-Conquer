package com.denyandconquer.common;

import javafx.scene.paint.Color;

public class Player {

    private String name;
    private Color color;
    private int score;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.color = Color.WHITE; // Default color
    }

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color != null ? color : Color.WHITE;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void reset() {
        resetScore();
        this.color = Color.WHITE;
    }

    @Override
    public String toString() {
        return name;
    }
}
