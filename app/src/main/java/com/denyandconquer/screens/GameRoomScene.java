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

public class GameRoomScene {
    private static ListView<Player> playerListView;
    private Scene gameRoomScene;
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;

    public GameRoomScene(Launcher launcher, Room room) {
        this.launcher = launcher;
        playerListView = new ListView<>();
        playerListView.getItems().addAll(room.getPlayerList());
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        gameRoomScene = createGameRoomScene(room.getRoomName());
    }

    private Scene createGameRoomScene(String name) {
        // Label Title, create refresh button, back button
        titleLabel = new Label(name);
        Button startGameBtn = new Button("Start Game");
        Button leaveRoomButton = new Button("Leave Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(playerListView, 0,1, 3, 2);
        grid.add(startGameBtn, 3, 1);
        grid.add(leaveRoomButton, 0, 4);

        startGameBtn.setOnAction(e -> {
            System.out.println("Start Game clicked!");

//            Scene gameBoardScene;
//            gameClient.setScene(gameBoardScene);
        });

        leaveRoomButton.setOnAction(e -> {
            launcher.setScene(launcher.getRoomBrowserScene());
        });

        Scene scene = new Scene(grid, 400, 300);

        return scene;
    }

    public Scene getRoomScene() {
        return gameRoomScene;
    }

    public void updateList(List<Player> list) {
        playerListView.getItems().clear();
        playerListView.getItems().addAll(list);

        // Display player list
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
