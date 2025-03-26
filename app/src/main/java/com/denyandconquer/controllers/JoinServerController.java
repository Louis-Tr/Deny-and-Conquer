package com.denyandconquer.controllers;

import com.denyandconquer.global_state.LoadingManager;
import com.denyandconquer.screens.Launcher;
import com.denyandconquer.server.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

/**
 * The JoinServerController class handles the logic for joining an existing server.
 * It retrieves input from UI components and processes the join server request.
 */
public class JoinServerController {
    private Launcher launcher;
    private TextField usernameField;
    private TextField ipField;
    private TextField portField;

    /**
     * Constructs a new JoinServerController with the specified input fields.
     *
     * @param usernameField the TextField for entering the username
     * @param ipField       the TextField for entering the server IP address
     * @param portField     the TextField for entering the server port
     */
    public JoinServerController(Launcher launcher, TextField usernameField, TextField ipField, TextField portField) {
        this.launcher = launcher;
        this.usernameField = usernameField;
        this.ipField = ipField;
        this.portField = portField;
    }

    /**
     * Handles the join server action.
     * <p>
     * This method retrieves the user's input from the username, server IP, and port fields,
     * then prints the details for debugging purposes. Actual server connection logic should be
     * implemented where indicated.
     * </p>
     *
     * @param event the ActionEvent triggered by clicking the "Join Server" button
     */
    public void handleJoinServer(ActionEvent event) {
        String username = usernameField.getText();
        String serverIP = ipField.getText();
        Integer port = Integer.parseInt(portField.getText());
        // For debug
        System.out.println("Joining server as " + username + " at " + serverIP + " on port " + port);
        // TODO: Implement server connection logic then call Platform.runLater() to set loading to false
        new Thread(() -> {
            try {
                // Create a client for the user
                GameClient client = new GameClient(serverIP, port, launcher);
                client.start();
                launcher.setNetwork(null, client);
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
