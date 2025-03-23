package com.denyandconquer.screens;

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
    Stage primaryStage;
    GridPane grid;
    Label titleLabel;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public ListViewScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
    }
    public Scene getRoomBrowserScene (Runnable onBack) {

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

            Scene createRoomScene = new InputScene().getCreateRoomScene(() -> primaryStage.setScene(getRoomBrowserScene(onBack)));
            primaryStage.setScene(createRoomScene);
        });

        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
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
//            primaryStage.setScene(gameBoardScene);
        });

        leaveRoomButton.setOnAction(e -> {
            System.out.println("Leave Room clicked!");
//            Scene roomBrowserScene = getRoomBrowserScene(onBack);
//            primaryStage.setScene(roomBrowserScene);
        });

        Scene scene = new Scene(grid, 400, 300);

        return scene;
    }
}
