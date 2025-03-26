package com.denyandconquer.screens;

import com.denyandconquer.server.Room;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameRoomScene {
    private static ListView<Room> roomListView;
    private Scene gameRoomScene;
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public GameRoomScene(Launcher launcher, String name) {
        this.launcher = launcher;
        roomListView = new ListView<>();
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        gameRoomScene = createGameRoomScene(name);
    }

    private Scene createGameRoomScene(String name) {
        // Label Title, create refresh button, back button
        titleLabel = new Label(name);
        Button startGameBtn = new Button("Start Game");
        Button leaveRoomButton = new Button("Leave Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(roomListView, 0,1, 3, 2);
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
}
