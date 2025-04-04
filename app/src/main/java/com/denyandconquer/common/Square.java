package com.denyandconquer.common;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Square {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final double FILL_THRESHOLD = 0.8;

    private final int row;
    private final int col;
    private final Canvas canvas;
    private boolean[][] filledGrid = new boolean[WIDTH][HEIGHT];
    private int filledCount = 0;

    private Player lockedBy;
    private Player ownedBy;
    private Color baseColor;

    // --- Constructor ---
    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.baseColor = Color.WHITE;
        clearCanvas();
    }

    // --- Game Actions ---
    public void pressBy(Player player) {
        if (lockedBy == null) {
            lock(player);
        }
    }

    public void draw(Player player, double x, double y) {
        if (lockedBy != null && lockedBy.equals(player)) {
            int px = (int) x;
            int py = (int) y;

            if (px >= 0 && px < WIDTH && py >= 0 && py < HEIGHT && !filledGrid[px][py]) {
                filledGrid[px][py] = true;
                filledCount++;

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(player.getColor());
                gc.fillRect(px, py, 2, 2); // still render to canvas
            }
        }
    }

    public void release() {
        if (lockedBy != null) {
            if (calculateFillPercentage() >= FILL_THRESHOLD) {
                ownedBy = lockedBy;
                baseColor = ownedBy.getColor();
                fillCanvas(baseColor);
            } else {
                unlock();
            }
        }
    }

    public void reset() {
        lockedBy = null;
        ownedBy = null;
        baseColor = Color.WHITE;
        clearCanvas();
    }

    // --- Private Helpers ---
    private void lock(Player player) {
        lockedBy = player;
        baseColor = player.getColor();
    }

    private void unlock() {
        lockedBy = null;
        baseColor = Color.WHITE;
        clearCanvas();
    }

    private void clearCanvas() {
        filledGrid = new boolean[WIDTH][HEIGHT];
        filledCount = 0;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void fillCanvas(Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private double calculateFillPercentage() {
        return (double) filledCount / (WIDTH * HEIGHT);
    }


    // --- Getters ---
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Canvas getCanvas() { return canvas; }
    public Player getLockedBy() { return lockedBy; }
    public Player getOwnedBy() { return ownedBy; }
}
