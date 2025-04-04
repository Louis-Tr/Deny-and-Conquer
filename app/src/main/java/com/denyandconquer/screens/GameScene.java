package com.denyandconquer.screens;

import com.denyandconquer.common.Board;
import com.denyandconquer.controllers.GameClientController;
import javafx.embed.swing.SwingFXUtils;
import com.denyandconquer.controllers.GameController;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.BoardUpdateListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

/**
 * Game scene displaying the board and players with their scores.
 * Supports drawing with player-colored pen cursor.
 */
public class GameScene extends Scene implements BoardUpdateListener {

    private SceneController sceneController;
    private GameClientController controller;
    private final Canvas boardCanvas = new Canvas(400, 400);
    private final VBox playerListBox = new VBox(10);

    private static final int TILE_SIZE = 50;
    private static final int BOARD_SIZE = 8;

    /**
     * Constructs the game scene with board and player list.
     * @param sceneController The game controller providing game state.
     */
    public GameScene(SceneController sceneController) {
        super(new BorderPane(), 600, 450);
        this.sceneController = sceneController;
        this.controller = sceneController.getGameController();

        BorderPane root = (BorderPane) getRoot();
        root.setPadding(new Insets(10));

        // Center: game board
        drawBoard();
        boardCanvas.setOnMouseMoved(e -> boardCanvas.setCursor(Cursor.DEFAULT));
        boardCanvas.setOnMouseEntered(e -> boardCanvas.setCursor(Cursor.DEFAULT));
        boardCanvas.setOnMouseExited(e -> boardCanvas.setCursor(Cursor.DEFAULT));
        boardCanvas.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));
        boardCanvas.setOnMouseDragged(e -> handleDrawing(e.getX(), e.getY()));
        boardCanvas.setOnMouseReleased(e ->  handleRelease(e.getX(), e.getY()));

        StackPane boardPane = new StackPane(boardCanvas);
        root.setCenter(boardPane);

        // Right: player score panel
        playerListBox.setPadding(new Insets(10));
        playerListBox.setPrefWidth(180);
        updatePlayerPanel();
        root.setRight(playerListBox);
    }

    public Scene getScene() {
        return this;
    }

    private void handleRelease(double x, double y) {
        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);
        controller.releaseTile(tileX, tileY);

    }

    private void handleClick(double x, double y) {
        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);
        Point2D localPos = new Point2D(x % TILE_SIZE, y % TILE_SIZE);

        controller.clickTile(tileX, tileY, localPos);
    }

    /**
     * Draws the board based on each square's color and outlines.
     */
    private void drawBoard() {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        Board board = controller.getBoard();

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                Square square = board.getSquare(x, y);
                // Fill the square with its base color
                Color fill = (Color) square.getFxColor();
                gc.setFill(fill);
                gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Overlay the square's canvas (the drawing)
                // Convert AWT BufferedImage to JavaFX Image
                javafx.scene.image.Image squareImage = SwingFXUtils.toFXImage(square.getCanvas(), null);
                gc.drawImage(squareImage, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Draw grid outline
                gc.setStroke(Color.GRAY);
                gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Updates the player list UI showing name and current score.
     */
    private void updatePlayerPanel() {
        playerListBox.getChildren().clear();
        Label header = new Label("Players");
        header.setFont(Font.font(16));
        playerListBox.getChildren().add(header);

        for (Player player : controller.getPlayers()) {
            Label label = new Label(player.getName() + ": " + player.getScore());
            label.setTextFill(player.getFxColor());
            playerListBox.getChildren().add(label);
        }
    }

    /**
     * Handles drawing on the board when the mouse is dragged.
     * @param x X position on canvas
     * @param y Y position on canvas
     */
    private void handleDrawing(double x, double y) {
        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);
        int localX = (int) (x % TILE_SIZE);
        int localY = (int) (y % TILE_SIZE);

        Player localPlayer = controller.getLocalPlayer();
        Board board = controller.getBoard();
        Square square = board.getSquare(tileX, tileY);

        if (square != null && square.getLockedBy() == localPlayer) {
            square.draw(localPlayer, localX, localY);
            drawBoard();
            controller.drawOnTile(tileX, tileY, new javafx.geometry.Point2D(localX, localY));
        }
    }

    @Override
    public void onSquareUpdated(Square square) {
        Platform.runLater(() -> {
            drawBoard();
            updatePlayerPanel();
        });
    }
}
