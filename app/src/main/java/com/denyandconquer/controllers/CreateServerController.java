package com.denyandconquer.controllers;

import com.denyandconquer.global_state.LoadingManager;
import com.denyandconquer.screens.Launcher;
import com.denyandconquer.server.GameClient;
import com.denyandconquer.server.GameServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

/**
 * Controller class for handling the creation of a server.
 * This class is responsible for managing the input fields and handling the creation server action.
 */
public class CreateServerController {
    private Launcher launcher;
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
    public CreateServerController(Launcher launcher, TextField nameField, TextField ipField, TextField portField) {
        this.launcher = launcher;
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
        Integer port = Integer.parseInt(portField.getText());

        // For debug
        System.out.println("Creating server: " + serverName + " at " + serverIP + " on port " + port);
        // TODO: Implement server creation logic then call Platform.runLater() to set loading to false
        new Thread(() -> {
            try {
                // Start the server
                GameServer server = new GameServer(serverIP, port);
                new Thread(server::startServer).start();
                Thread.sleep(2000);
                // Create a client for the user
                GameClient client = new GameClient(serverIP, port, launcher);
                client.start();
                launcher.setNetwork(server, client);
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