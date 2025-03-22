package com.denyandconquer.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class RoomBrowserScene {
    private static ListView<String> roomListView = new ListView<>();
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public Scene getRoomBrowserScene (Runnable onBack) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Label Title, create refresh button, back button
        Label titleLabel = new Label("Available Game Rooms");
        Button refreshButton = new Button("Refresh Rooms");
        Button backButton = new Button("Back");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(refreshButton, 2, 0);
        grid.add(roomListView, 0,1, 3, 2);
        grid.add(backButton, 0, 4);

        // Actions
        /** To do
         * Refresh Action -> update room list and reload
         * Selected Room -> Enter the room
         * */
//        refreshButton.setOnAction(e -> requestRoomList(serverAddress, port));

        // Set up room selection
//        roomListView.setOnMouseClicked(e -> {
//            String seletedRoom = roomListView.getSelectionModel().getSelectedItem();
//            if (seletedRoom != null) {
//                joinRoom(seletedRoom, serverAddress, port, onRoomJoined);
//            }
//        });
//
//        requestRoomList(serverAddress, port);

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
}
