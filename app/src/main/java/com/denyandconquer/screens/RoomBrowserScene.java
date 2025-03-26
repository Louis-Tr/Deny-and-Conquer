package com.denyandconquer.screens;

import com.denyandconquer.server.Room;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import java.util.List;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RoomBrowserScene {
    private static ListView<Room> roomListView;
    private Scene roomBrowserScene;
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public RoomBrowserScene(Launcher launcher) {
        this.launcher = launcher;
        roomListView = new ListView<>();
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        roomBrowserScene = createRoomBrowserScene();
    }

    public Scene getRoomBrowserScene() {
        return roomBrowserScene;
    }
    private Scene createRoomBrowserScene() {

        // Label Title, create refresh button, back button
        titleLabel = new Label("Available Game Rooms");
        Button createRoomBtn = new Button("Create Room");
        Button backButton = new Button("Back");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(roomListView, 0,1, 3, 2);
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

    public void updateList(List<Room> list) {
        roomListView.getItems().clear();
        roomListView.getItems().addAll(list);

        // Display room details
        roomListView.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getRoomName() + " (Players: " + room.getPlayers().size() + "/" + room.getMaxPlayers() + ")");
                }
            }
        });

    }

}
