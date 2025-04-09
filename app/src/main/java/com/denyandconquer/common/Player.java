package com.denyandconquer.common;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String colorHex; // hex string like "#FFFFFF"
    private transient Color color; // not serialized
    private int score;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        setColor(Color.RED);
    }

    // --- Color Handling ---

    public Color getColor() {
        if (color == null && colorHex != null) {
            color = Color.web(colorHex);
        }
        return color;
    }

    public void setColor(Color color) {
        this.color = color != null ? color : Color.WHITE;
        this.colorHex = toHex(this.color);
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColorHex(String hex) {
        this.colorHex = hex;
        this.color = Color.web(hex);
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
        setColor(Color.WHITE);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Player other = (Player) obj;
        return name != null && colorHex != null &&
                name.equals(other.name) && colorHex.equals(other.colorHex);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (colorHex != null ? colorHex.hashCode() : 0);
        return result;
    }


}
