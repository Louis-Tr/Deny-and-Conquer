package com.denyandconquer.screens;

import com.denyandconquer.server.GameClient;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ListViewScene {
    private static ListView<String> listView = new ListView<>();
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public ListViewScene(Launcher launcher) {
        this.launcher = launcher;
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
    }
    public Scene getRoomBrowserScene () {

        // Label Title, create refresh button, back button
        titleLabel = new Label("Available Game Rooms");
        Button createRoomBtn = new Button("Create Room");
        Button backButton = new Button("Back");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(listView, 0,1, 3, 2);
        grid.add(createRoomBtn, 3, 1);
        grid.add(backButton, 0, 4);

        // Actions
        /** To do
         * Selected Room -> Enter the room
         * */

        // Set up room selection
//        roomListView.setOnMouseClicked(e -> {
//            String seletedRoom = roomListView.getSelectionModel().getSelectedItem();
//            if (seletedRoom != null) {
//                joinRoom(seletedRoom, serverAddress, port, onRoomJoined);
//            }
//        });
//
//        requestRoomList(serverAddress, port);

        createRoomBtn.setOnAction(e -> {
            System.out.println("Create Room clicked!");

            Scene createRoomScene = new InputScene().getCreateRoomScene(launcher);
            launcher.setScene(createRoomScene);
        });

        backButton.setOnAction(e -> {
            launcher.setScene(launcher.getLaucherScene());
        });

        Scene scene = new Scene(grid, 400, 300);

        return scene;
    }

//    private static void requestRoomList(String serverAddress, int port) {
//        new Thread(() -> {
//            tr
//        }).start();
//    }

    public Scene getRoomScene() {
        // Label Title, create refresh button, back button
        titleLabel = new Label("Game Rooms");
        Button startGameBtn = new Button("Start Game");
        Button leaveRoomButton = new Button("Leave Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(listView, 0,1, 3, 2);
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
}
