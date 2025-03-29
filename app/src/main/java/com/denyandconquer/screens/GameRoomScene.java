package com.denyandconquer.screens;

import com.denyandconquer.server.GameThread;
import com.denyandconquer.server.Player;
import com.denyandconquer.server.Room;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Represents the game room scene where players can see who is in the room.
 * Players can leave the room or start the game.
 */
public class GameRoomScene {
    private static ListView<Player> playerListView;
    private Room room;
    private Scene gameRoomScene;
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;

    /**
     * Initializes the game room UI and set up the scene.
     * @param launcher The main launcher that manages scenes
     * @param room The game room that players are in
     */
    public GameRoomScene(Launcher launcher, Room room) {
        this.launcher = launcher;
        playerListView = new ListView<>();
        playerListView.getItems().addAll(room.getPlayerList());
        this.room = room;

        // Set uup the grid layout
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        gameRoomScene = createGameRoomScene(room.getRoomName());
    }

    /**
     * Creates and sets up the game room scene UI.
     * @param name The name of the game room
     * @return A scene representing the game room
     */
    private Scene createGameRoomScene(String name) {
        // Label Title, create refresh button, back button
        titleLabel = new Label(name);
        Button startGameBtn = new Button("Start Game");
        Button leaveRoomButton = new Button("Leave Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(playerListView, 0,1, 3, 2);
        grid.add(startGameBtn, 3, 1);
        grid.add(leaveRoomButton, 0, 4);

        // Set action for start game button
        startGameBtn.setOnAction(e -> {
            System.out.println("Start Game clicked!");

//            Scene gameBoardScene;
//            gameClient.setScene(gameBoardScene);
        });

        // Set action for leave room button
        leaveRoomButton.setOnAction(e -> {
            launcher.getGameClient().sendLeaveRoomRequest(room.getRoomId());
        });

        // Create and return the scene
        Scene scene = new Scene(grid, 400, 300);
        return scene;
    }

    /**
     * Gets the scene for the game room.
     * @return The Scene of the game room UI
     */
    public Scene getRoomScene() {
        return gameRoomScene;
    }

    /**
     * Updates the player list in the UI when player enter/leave the room
     * @param list The updated list of players in the room
     */
    public void updateList(List<Player> list) {
        playerListView.getItems().clear();
        playerListView.getItems().addAll(list);

        // Display player names
        playerListView.setCellFactory(param -> new ListCell<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setText(null);
                } else {
                    setText(player.getName());
                }
            }
        });
    }
}
