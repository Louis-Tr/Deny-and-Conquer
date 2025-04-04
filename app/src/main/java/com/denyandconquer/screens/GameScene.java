package com.denyandconquer.screens;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.controllers.GameClientController;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.BoardUpdateListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameScene extends Scene implements BoardUpdateListener {

    private static final int TILE_SIZE = 50;
    private static final int BOARD_SIZE = 8;

    private final SceneController sceneController;
    private final GameClientController controller;
    private final GridPane boardGrid = new GridPane();
    private final VBox playerListBox = new VBox(10);

    public GameScene(SceneController sceneController) {
        super(new BorderPane(), 600, 450);
        this.sceneController = sceneController;
        this.controller = sceneController.getGameController();

        BorderPane root = (BorderPane) getRoot();
        root.setPadding(new Insets(10));

        // Build the board UI
        buildBoardUI();
        root.setCenter(boardGrid);

        // Player score panel
        playerListBox.setPadding(new Insets(10));
        playerListBox.setPrefWidth(180);
        updatePlayerPanel();
        root.setRight(playerListBox);
    }

    private void buildBoardUI() {
        boardGrid.setHgap(0);
        boardGrid.setVgap(0);
        Board board = controller.getBoard();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Square square = board.getSquare(row, col);
                Canvas canvas = square.getCanvas();
                canvas.setOnMousePressed(e -> handleClick(e));
                canvas.setOnMouseDragged(e -> handleDrawing(e));
                canvas.setOnMouseReleased(e -> handleRelease(e));
                canvas.setCursor(Cursor.CROSSHAIR);
                boardGrid.add(canvas, col, row);
            }
        }
    }

    private void handleClick(MouseEvent e) {
        Point2D localPos = new Point2D(e.getX(), e.getY());
        controller.clickTile(row, col, localPos);
    }

    private void handleDrawing(MouseEvent e) {
        int localX = (int) e.getX();
        int localY = (int) e.getY();

        Player localPlayer = controller.getLocalPlayer();
        Square square = controller.getBoard().getSquare(row, col);

        if (square != null && square.getLockedBy() == localPlayer) {
            square.draw(localPlayer, localX, localY);
            controller.drawOnTile(row, col, new Point2D(localX, localY));
        }
    }

    private void handleRelease(int row, int col) {
        controller.releaseTile(row, col);
    }

    private void updatePlayerPanel() {
        playerListBox.getChildren().clear();
        Label header = new Label("Players");
        header.setFont(Font.font(16));
        playerListBox.getChildren().add(header);

        for (Player player : controller.getPlayers()) {
            Label label = new Label(player.getName() + ": " + player.getScore());
            label.setTextFill(player.getColor());
            playerListBox.getChildren().add(label);
        }
    }

    @Override
    public void onSquareUpdated(Square square) {
        Platform.runLater(() -> {
            updatePlayerPanel();
        });
    }

    public Scene getScene() {
        return this;
    }
}
