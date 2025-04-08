package com.denyandconquer.common;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Square {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final double FILL_THRESHOLD = 0.5;
    private static final int BRUSH_SIZE = 7;

    private final int row;
    private final int col;
    private final Canvas canvas;
    private boolean[][] filledGrid = new boolean[WIDTH][HEIGHT];
    private int filledCount = 0;
    private javafx.geometry.Point2D lastDrawnPoint = null;

    private Player lockedBy;
    private Player ownedBy;
    private boolean isLocked = false;
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
    public boolean pressBy(Player player) {
        if (lockedBy == null && ownedBy == null) {
            System.out.println("Square pressed by " + player.getName());
            lock(player);
            this.isLocked = true;
            return true;
        }
        return false;
    }

    public boolean draw(Player player, double x, double y) {
        System.out.println("Square drawn by " + player.getName() + " at (" + x + ", " + y + ")");
        if (lockedBy != player) return false;

        int px = (int) x;
        int py = (int) y;

        if (px < 0 || px >= WIDTH || py < 0 || py >= HEIGHT) return false;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(player.getColor());

        boolean drew = false;

        if (lastDrawnPoint == null) {
            drew |= fillPixel(gc, px, py);
        } else {
            int lx = (int) lastDrawnPoint.getX();
            int ly = (int) lastDrawnPoint.getY();
            drew |= drawLine(gc, lx, ly, px, py);
        }

        lastDrawnPoint = new Point2D(px, py);
        return drew;
    }



    public boolean release(Player player) {
        if (lockedBy == player) {
            lastDrawnPoint = null;

            if (calculateFillPercentage() >= FILL_THRESHOLD) {
                ownedBy = lockedBy;
                lockedBy = null;
                baseColor = ownedBy.getColor();
                fillCanvas(baseColor);
                player.incrementScore();
            } else {
                reset();
            }

            return true; // Something changed in either case
        }

        return false; // Player didn't have the lock
    }

    public boolean isCanvas(Canvas c) {
        return this.canvas == c;
    }


    public void reset() {
        lockedBy = null;
        ownedBy = null;
        isLocked = false;
        baseColor = Color.WHITE;
        filledCount = 0;
        filledGrid = new boolean[WIDTH][HEIGHT];
        lastDrawnPoint = null;
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

        drawBorder(gc);
    }


    private void fillCanvas(Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawBorder(gc);
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
    public Color getColor() { return baseColor; }

    private boolean fillPixel(GraphicsContext gc, int x, int y) {
        boolean drew = false;

        for (int dx = 0; dx < BRUSH_SIZE; dx++) {
            for (int dy = 0; dy < BRUSH_SIZE; dy++) {
                int px = x + dx;
                int py = y + dy;

                if (px >= 0 && px < WIDTH && py >= 0 && py < HEIGHT && !filledGrid[px][py]) {
                    gc.fillRect(px, py, 1, 1);
                    filledGrid[px][py] = true;
                    filledCount++;
                    drew = true;
                }
            }
        }

        return drew;
    }


    private boolean drawLine(GraphicsContext gc, int x0, int y0, int x1, int y1) {
        boolean drew = false;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            drew |= fillPixel(gc, x0, y0);
            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }

        return drew;
    }

    private void drawBorder(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, WIDTH, HEIGHT);
    }


    public boolean isLock() {
        return isLocked;
    }
}
