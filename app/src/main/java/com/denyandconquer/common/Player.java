package com.denyandconquer.common;


import javafx.scene.paint.Color;

public class Player {
    private Color color;
    private Integer score;  
    private Boolean isDrawing;

    public Color getColor() {
        return color;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getIsDrawing() {
        return isDrawing;
    }

    public void setIsDrawing(Boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public Player() {
        this.color = null;
        this.score = 0;
        this.isDrawing = false;
    }

    public Player(Player player) {
        this.color = player.color;
        this.score = player.score;
        this.isDrawing = player.isDrawing;
    }

    public Player(Color color) {
        this.color = color;
        this.score = 0;
        this.isDrawing = false;
    }

    public void incrementScore() {
        this.score++;
    }
}
