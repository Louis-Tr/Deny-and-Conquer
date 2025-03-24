package com.denyandconquer.controllers;

import com.denyandconquer.server.GameClient;
import com.denyandconquer.server.Message;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;


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

        gameClient.createRoom(roomName, playerNumber);
    }
}
