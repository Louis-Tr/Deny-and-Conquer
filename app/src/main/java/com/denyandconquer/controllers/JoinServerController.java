package com.denyandconquer.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

/**
 * The JoinServerController class handles the logic for joining an existing server.
 * It retrieves input from UI components and processes the join server request.
 */
public class JoinServerController {
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
    public JoinServerController(TextField usernameField, TextField ipField, TextField portField) {
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
        String port = portField.getText();
        // For debug
        System.out.println("Joining server as " + username + " at " + serverIP + " on port " + port);
        // TODO: Implement server connection logic
    }
}
