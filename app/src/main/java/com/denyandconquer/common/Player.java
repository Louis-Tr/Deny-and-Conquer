package com.denyandconquer.common;


import javafx.scene.paint.Color;

public class Player {
    private String name;
    private Color color;
    private Integer coloredSquareCount; // score
    private Boolean isDrawing;

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Integer getColoredSquareCount() {
        return coloredSquareCount;
    }

    public Boolean getIsDrawing() {
        return isDrawing;
    }

    public void setIsDrawing(Boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.coloredSquareCount = 0;
        this.isDrawing = false;
    }

    public void incrementScore() {
        this.coloredSquareCount++;
    }
}
