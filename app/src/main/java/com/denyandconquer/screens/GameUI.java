package com.denyandconquer.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.controllers.GameController;

import javafx.scene.input.MouseEvent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static final int CELL_SIZE = 75; 
    private static final Color PLAYER_COLOR = Color.RED;
    private static final double FILL_THRESHOLD = 0.5;
    private static final int PEN_THICKNESS = 5;

    private Board board;
    private GameController gameController;
    private Player player1; // testing currently hardcoded
    private Player player2; // testing
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        board = new Board();
        GridPane gridPane = createGridPane();

        player1 = new Player(Color.RED);
        player2 = new Player(Color.BLUE);
        List<Player> playerList = new ArrayList<Player>();
        playerList.add(player1);
        playerList.add(player2);
        gameController = new GameController(playerList);

        for (int row = 0; row < Board.GRID_SIZE; row++) {
            for (int col = 0; col < Board.GRID_SIZE; col++) {
                Square square = board.grid[row][col];
                Pane cell = createCell(square);
                gridPane.add(cell, col, row);
            }
        }
        Scene scene = new Scene(gridPane, Board.GRID_SIZE * CELL_SIZE + 20, Board.GRID_SIZE * CELL_SIZE + 20);
        primaryStage.setTitle("Deny and Conquer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(0);
        gridPane.setVgap(0);
        return gridPane;
    }

    private Pane createCell(Square square) {
        Pane cell = new Pane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setStyle("-fx-background-color: white; -fx-border-color: black;");

        Canvas canvas = new Canvas(CELL_SIZE, CELL_SIZE);
        canvas.setClip(new Rectangle(0, 0, CELL_SIZE, CELL_SIZE));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(PEN_THICKNESS);
        cell.getChildren().add(canvas);

        // Associate the Square with the cell.
        cell.setUserData(square);

        DrawingState state = new DrawingState();

        cell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> handleMousePressed(cell, square, gc, event, state));
        cell.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> handleMouseDragged(cell, square, gc, event, state));
        cell.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> handleMouseReleased(cell, square, gc, event, state, player1)); //hard coded

        return cell;
    }

    private void handleMousePressed(Pane cell, Square square, GraphicsContext gc, MouseEvent event, DrawingState state) {
        if (!square.isLocked() && square.isWhite()) {
            square.lockSquare();
            square.setOwner(player1);
            cell.setStyle("-fx-background-color: white; -fx-border-color: " 
                          + toRgbString(PLAYER_COLOR) + "; -fx-border-width: 3;");
            state.lastX = event.getX();
            state.lastY = event.getY();
            state.currentInside = true;
            gc.beginPath();
            gc.moveTo(state.lastX, state.lastY);
            gc.setStroke(PLAYER_COLOR);
            gc.stroke();
            System.out.println("Started drawing in cell at " + square);
        }
    }

    private void handleMouseDragged(Pane cell, Square square, GraphicsContext gc, MouseEvent event, DrawingState state) {
        if (!square.isLocked()) {
            return;
        }
        if (square.isLocked()) {
            double currentX = event.getX();
            double currentY = event.getY();

            boolean isOutside = currentX < 0 || currentX > CELL_SIZE || currentY < 0 || currentY > CELL_SIZE;
            if (isOutside) {
                state.currentInside = false;
            } else {
                if (!state.currentInside) {
                    state.lastX = currentX;
                    state.lastY = currentY;
                    gc.moveTo(state.lastX, state.lastY);
                    state.currentInside = true;
                } else {
                    gc.lineTo(currentX, currentY);
                    gc.stroke();
                    double segmentLength = Math.hypot(currentX - state.lastX, currentY - state.lastY);
                    state.drawnLength += segmentLength;
                    state.lastX = currentX;
                    state.lastY = currentY;
                    System.out.println("Drawing");
                }
            }
        }
    }

    private void handleMouseReleased(Pane cell, Square square, GraphicsContext gc, MouseEvent event, DrawingState state, Player currentPlayer) {
        if (square.isLocked()) {
            double totalArea = CELL_SIZE * CELL_SIZE;
            double simulatedFilledArea = state.drawnLength * 2.5; // Factor to simulate area.
            double fillPercent = simulatedFilledArea / totalArea;
            System.out.println("Fill percent: " + fillPercent);

            if (fillPercent >= FILL_THRESHOLD) {
                square.setOwner(currentPlayer);
                square.setColor(player1.getColor()); 
                currentPlayer.incrementScore();
                board.decreaseEmptySquares();

                gc.clearRect(0, 0, CELL_SIZE, CELL_SIZE);
                gc.setFill(PLAYER_COLOR);
                gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
                System.out.println("Square captured by " + PLAYER_COLOR + " player!");

                checkForGameEnd();
            } else {
                gc.clearRect(0, 0, CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                square.resetToWhite();
                System.out.println("Drawing not sufficient. Cell reset.");
            }

            state.drawnLength = 0;
            square.unlockSquare();
        }
    }

    private void checkForGameEnd() {
        if (board.isGameComplete()) {
            List<Player> winners = gameController.determineWinner();
            
            StackPane endPane = new StackPane();
            endPane.setPrefSize(1000, 500);
            
            String winnerMessage;
            if (winners.size() == 1) {
                winnerMessage = "The " + winners.get(0).getColor() + " player won with " + winners.get(0).getScore() + " points!";
            } else {
                String tiedColors = winners.stream()
                                            .map(player -> player.getColor().toString())
                                            .collect(Collectors.joining(", "));
                winnerMessage = "It's a tie between: " + tiedColors + " with " + winners.get(0).getScore() + " points each!";
            }
            
            Text endText = new Text(winnerMessage);
            endText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            endPane.getChildren().add(endText);
            endPane.setAlignment(Pos.CENTER);

            Scene endScene = new Scene(endPane);
            primaryStage.setScene(endScene);
            primaryStage.show();
        }
    }


    private static class DrawingState {
        double lastX;
        double lastY;
        double drawnLength;
        boolean currentInside;
    }

    private String toRgbString(Color c) {
        return "rgb(" + (int)(c.getRed()*255) + "," + (int)(c.getGreen()*255) + "," + (int)(c.getBlue()*255) + ")";
    }
   

}

