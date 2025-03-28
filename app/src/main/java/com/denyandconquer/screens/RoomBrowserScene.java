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

/**
 * The RoomBrowserScene class displays a list of available game rooms
 * and allow players to join an existing room or create a new room.
 */
public class RoomBrowserScene {
    private static ListView<Room> roomListView;
    private Scene roomBrowserScene;
    private Launcher launcher;
    GridPane grid;
    Label titleLabel;

    /**
     * Init RoomBrowserScene
     * @param launcher used for scene transitions
     *                 and client calling to send message to server
     */
    public RoomBrowserScene(Launcher launcher) {
        this.launcher = launcher;
        roomListView = new ListView<>();
        grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        roomBrowserScene = createRoomBrowserScene();
    }

    /**
     * Creates the UI layout and event handlers for the room browser scene.
     * @return the room browser scene object.
     */
    private Scene createRoomBrowserScene() {

        // Label Title, create refresh button, back button
        titleLabel = new Label("Available Game Rooms");
        Button createRoomBtn = new Button("Create Room");
        Button backButton = new Button("Back");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(roomListView, 0,1, 3, 2);
        grid.add(createRoomBtn, 3, 1);
        grid.add(backButton, 0, 4);

        // Event listener for room selection to join
        roomListView.setOnMouseClicked(e -> {
            Room seletedRoom = roomListView.getSelectionModel().getSelectedItem();
            if (seletedRoom != null) {
                System.out.println("Join Room clicked!");
                joinRoom(seletedRoom);
            }
        });

        // Event listener for room creation
        createRoomBtn.setOnAction(e -> {
            System.out.println("Create Room clicked!");
            Scene createRoomScene = new InputScene().getCreateRoomScene(launcher);
            launcher.setScene(createRoomScene);
        });

        // Event listener for navigating back to launcher scene
        backButton.setOnAction(e -> {
            launcher.setScene(launcher.getLaucherScene());
        });

        Scene scene = new Scene(grid, 400, 300);

        return scene;
    }

    /**
     * Returns the scene for the room browser
     * @return The scene object
     */
    public Scene getRoomBrowserScene() {
        return roomBrowserScene;
    }

    /**
     * Sends a request to join the selected room.
     * @param seletedRoom the room that player wants to join.
     */
    private void joinRoom(Room seletedRoom) {
        launcher.getGameClient().sendJoinRoomRequest(seletedRoom.getRoomId());
    }

    /**
     * Updates the ListView with the latest list of available rooms.
     * @param list the list of available rooms.
     */
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
