package com.denyandconquer.screens;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.client.GameClientController;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.BoardUpdateListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class GameScene extends Scene implements BoardUpdateListener {

    private static final int TILE_SIZE = 50;
    private static final int BOARD_SIZE = 8;

    private final SceneController sceneController;
    private final GameClientController controller;
    private final GridPane boardGrid = new GridPane();
    private final VBox playerListBox = new VBox(0);

    public GameScene(SceneController sceneController) {
        super(new BorderPane(), 1000, 800);
        this.sceneController = sceneController;
        this.controller = sceneController.getGameController();

        BorderPane root = (BorderPane) getRoot();
        root.setPadding(new Insets(10));

        // Build the board UI
        buildBoardUI();


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

        // Populate the board grid with squares
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Square square = board.getSquare(row, col);
                boardGrid.add(square.getCanvas(), col, row);
            }
        }

        // Create transparent overlay canvas
        Canvas overlay = new Canvas(BOARD_SIZE * Square.WIDTH, BOARD_SIZE * Square.HEIGHT);
        overlay.setPickOnBounds(true);              // Use layout bounds for hit testing
        overlay.setMouseTransparent(false);         // Capture mouse events
        overlay.setOpacity(0);                      // Fully transparent

        // Bind overlay dimensions to boardGrid dimensions to ensure they are always in sync
        overlay.widthProperty().bind(boardGrid.widthProperty());
        overlay.heightProperty().bind(boardGrid.heightProperty());

        // Set mouse event handlers and cursor
        overlay.setOnMousePressed(controller::mouseAction);
        overlay.setOnMouseDragged(controller::mouseAction);
        overlay.setOnMouseReleased(controller::mouseAction);
        overlay.setCursor(Cursor.CROSSHAIR);
        System.out.println("🎯 Cursor set to: " + overlay.getCursor());

        // Create a StackPane to layer the board grid and the overlay canvas
        StackPane layered = new StackPane();
        layered.getChildren().addAll(boardGrid, overlay);

        // Set the layered StackPane as the center of the root BorderPane
        ((BorderPane) getRoot()).setCenter(layered);
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
        Platform.runLater(this::updatePlayerPanel);
    }

    public Scene getScene() {
        return this;
    }

}
