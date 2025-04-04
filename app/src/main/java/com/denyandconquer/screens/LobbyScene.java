package com.denyandconquer.screens;

import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.GameRoomDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * LobbyScene displays available game rooms and allows players to join or create a room.
 */
public class LobbyScene {
    private final Scene scene;
    private final SceneController controller;
    private ObservableList<GameRoomDTO> roomList = FXCollections.observableArrayList();
    private ListView<GameRoomDTO> roomListView = new ListView<>(roomList);

    public LobbyScene(SceneController controller) {
        this.controller = controller;
        this.scene = createLobbyScene();
    }

    private Scene createLobbyScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label titleLabel = new Label("Available Game Rooms");
        Button createRoomBtn = new Button("Create Room");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(roomListView, 0, 1, 3, 2);
        grid.add(createRoomBtn, 3, 1);

        roomListView.setOnMouseClicked(e -> {
            GameRoomDTO selectedRoom = roomListView.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                if (selectedRoom.isPrivate()) {
                    showPasswordDialog(selectedRoom);
                } else {
                    controller.getLauncher().getClient().sendJoinRoomRequest(selectedRoom.getRoomId(), null);
                }
            }
        });

        roomListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GameRoomDTO room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getRoomName() + " (Players: " +
                            room.getCurrentPlayers() + "/" + room.getMaxPlayers() + ")" +
                            (room.isPrivate() ? " 🔒" : ""));
                }
            }
        });

        createRoomBtn.setOnAction(e -> showCreateRoomDialog());

        return new Scene(grid, 400, 300);
    }

    /**
     * Displays a pop-up dialog to create a room with room name, player count, and optional password.
     */
    private void showCreateRoomDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(controller.getPrimaryStage());
        dialog.setTitle("Create Room");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField roomNameField = new TextField();
        PasswordField passwordField = new PasswordField();
        passwordField.setDisable(true); // initially disabled

        ChoiceBox<Integer> playerCountChoice = new ChoiceBox<>();
        playerCountChoice.getItems().addAll(2, 3, 4);
        playerCountChoice.setValue(2);

        CheckBox privateCheckBox = new CheckBox("Private Room");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        grid.add(new Label("Room Name:"), 0, 0);
        grid.add(roomNameField, 1, 0);

        grid.add(new Label("Max Players:"), 0, 1);
        grid.add(playerCountChoice, 1, 1);

        grid.add(privateCheckBox, 0, 2, 2, 1);

        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);

        Button createBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");

        grid.add(errorLabel, 0, 4, 2, 1);
        grid.add(createBtn, 0, 5);
        grid.add(cancelBtn, 1, 5);

        // Toggle password field based on private checkbox
        privateCheckBox.setOnAction(e -> {
            passwordField.setDisable(!privateCheckBox.isSelected());
        });

        createBtn.setOnAction(e -> {
            String roomName = roomNameField.getText().trim();
            int maxPlayers = playerCountChoice.getValue();
            boolean isPrivate = privateCheckBox.isSelected();
            String password = isPrivate ? passwordField.getText().trim() : null;

            if (roomName.isEmpty()) {
                errorLabel.setText("Room name cannot be empty.");
                return;
            }

            if (isPrivate && (password == null || password.isEmpty())) {
                errorLabel.setText("Password is required for private rooms.");
                return;
            }

            controller.getLauncher().getClient().sendCreateRoomRequest(roomName, maxPlayers, isPrivate, password);
            dialog.close();
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene dialogScene = new Scene(new VBox(grid));
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Displays a password prompt for joining a private room.
     */
    private void showPasswordDialog(GameRoomDTO room) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(controller.getPrimaryStage());
        dialog.setTitle("Enter Room Password");

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        PasswordField passwordField = new PasswordField();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button joinBtn = new Button("Join");
        Button cancelBtn = new Button("Cancel");

        joinBtn.setOnAction(e -> {
            String password = passwordField.getText().trim();
            if (password.isEmpty()) {
                errorLabel.setText("Password cannot be empty.");
            } else {
                controller.getLauncher().getClient().sendJoinRoomRequest(room.getRoomId(), password);
                dialog.close();
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        box.getChildren().addAll(new Label("Password:"), passwordField, errorLabel, new HBox(10, joinBtn, cancelBtn));

        Scene dialogScene = new Scene(box);
        dialog.setScene(dialogScene);
        dialog.show();
    }



    public Scene getScene() {
        return scene;
    }

    public void updateList(List<GameRoomDTO> list) {
        if (list == null) list = new ArrayList<>();
        roomList.setAll(list);
    }
}
