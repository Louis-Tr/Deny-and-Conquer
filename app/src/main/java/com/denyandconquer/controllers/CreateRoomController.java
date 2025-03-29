package com.denyandconquer.controllers;

import com.denyandconquer.server.GameClient;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * The CreateRoomController class handles the user input for creating a new game room.
 * It listens for the create room event and sends a request to the server.
 */
public class CreateRoomController {

    private TextField nameField;
    private ChoiceBox<Integer> playerChoiceBox;
    GameClient gameClient;

    public CreateRoomController(TextField nameField, ChoiceBox<Integer> playerChoiceBox, GameClient gameClient) {
        this.nameField = nameField;
        this.playerChoiceBox = playerChoiceBox;
        this.gameClient = gameClient;
    }

    public void handleCreateRoom(ActionEvent event) {
        String roomName = nameField.getText();
        int playerNumber = playerChoiceBox.getValue();

        gameClient.sendCreateRoomRequest(roomName, playerNumber);
    }
}
