package com.denyandconquer.screens;

import com.denyandconquer.common.Player;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.GameRoomDTO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * GameRoomScene shows the players in a room before the game starts.
 * Supports leaving the room and starting the game.
 */
public class GameRoomScene {
    private final SceneController controller;
    private final GameRoomDTO room;
    private final ListView<Player> playerListView;
    private final Scene scene;

    public GameRoomScene(SceneController controller, GameRoomDTO room) {
        this.controller = controller;
        this.room = room;
        this.playerListView = new ListView<>();
        this.scene = createScene();
    }

    private Scene createScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label titleLabel = new Label("Room: " + room.getRoomName());

        Button startGameBtn = new Button("Start Game");
        Button leaveRoomBtn = new Button("Leave Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(playerListView, 0, 1, 3, 2);
        grid.add(startGameBtn, 3, 1);
        grid.add(leaveRoomBtn, 0, 4);

        // Start game → notify server
        startGameBtn.setOnAction(e -> {
            controller.getLauncher().getClient().sendStartGameRequest(room.getRoomId());
        });

        // Leave room → notify server
        leaveRoomBtn.setOnAction(e -> {
            controller.getLauncher().getClient().sendLeaveRoomRequest();
            controller.showLobbyScene(); // fallback in case no message received
        });

        updatePlayerList(room.getPlayerList());

        return new Scene(grid, 500, 350);
    }

    public Scene getScene() {
        return scene;
    }

    public void updatePlayerList(List<Player> players) {
        playerListView.getItems().setAll(players);
        playerListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                setText((empty || player == null) ? null : player.getName());
            }
        });
    }

    public void showWinnerDialog(Player winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("The game is over!");
        alert.setContentText("The winner is: " + winner.getName());
        alert.showAndWait();
    }


}
