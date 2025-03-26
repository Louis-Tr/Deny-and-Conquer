package com.denyandconquer.controllers;

import com.denyandconquer.global_state.LoadingManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

/**
 * Controller class for handling the creation of a server.
 * This class is responsible for managing the input fields and handling the creation server action.
 */
public class CreateServerController {
    private TextField nameField;
    private TextField ipField;
    private TextField portField;

    /**
     * Constructs a CreateServerController with the specified input fields.
     *
     * @param nameField the TextField for the server name
     * @param ipField   the TextField for the server IP
     * @param portField the TextField for the server port
     */
    public CreateServerController(TextField nameField, TextField ipField, TextField portField) {
        this.nameField = nameField;
        this.ipField = ipField;
        this.portField = portField;
    }

    /**
     * Handles the action event for creating a server.
     * This method retrieves the input values and initiates the server creation process.
     *
     * @param event the ActionEvent triggered by the create server button
     */
    public void handleCreateServer(ActionEvent event) {
        String serverName = nameField.getText();
        String serverIP = ipField.getText();
        String port = portField.getText();

        // For debug
        System.out.println("Creating server: " + serverName + " at " + serverIP + " on port " + port);
        // TODO: Implement server creation logic then call Platform.runLater() to set loading to false
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                LoadingManager.setLoading(false);
                System.out.println("Loading finished. Navigating to the game screen...");
            });
        }).start();
    }
}