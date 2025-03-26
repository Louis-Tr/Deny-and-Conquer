package com.denyandconquer.screens;

import com.denyandconquer.controllers.CreateRoomController;
import com.denyandconquer.server.GameClient;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import com.denyandconquer.global_state.LoadingManager;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.denyandconquer.controllers.CreateServerController;
import com.denyandconquer.controllers.JoinServerController;

public class InputScene {
    Label titleLabel;

    /**
     * Creates and returns a Scene for creating a new server.
     * Ensures all inputs are valid before proceeding.
     */
    public Scene getCreateServerScene(Runnable onBack, Runnable toLoading, Launcher launcher) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Server Name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label ipLabel = new Label("Server IP:");
        TextField ipField = new TextField();
        grid.add(ipLabel, 0, 1);
        grid.add(ipField, 1, 1);

        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 4);

        Button createButton = new Button("Create Server");
        grid.add(createButton, 1, 3);

        Button backButton = new Button("Back");
        grid.add(backButton, 0, 3);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        CreateServerController controller = new CreateServerController(launcher, nameField, ipField, portField);

        createButton.setOnAction(e -> {
            if (isValidInput(nameField, ipField, portField, errorLabel)) {
                LoadingManager.setLoading(true);
                toLoading.run(); // Navigate to loading screen
                controller.handleCreateServer(e);
            }
        });

        return new Scene(grid, 400, 300);
    }

    /**
     * Creates and returns a Scene for joining an existing server.
     * Ensures all inputs are valid before proceeding.
     */
    public Scene getJoinServerScene(Runnable onBack, Runnable toLoading, Launcher launcher) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label ipLabel = new Label("Server IP:");
        TextField ipField = new TextField();
        grid.add(ipLabel, 0, 1);
        grid.add(ipField, 1, 1);

        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 4);

        Button joinButton = new Button("Join Server");
        grid.add(joinButton, 1, 3);

        Button backButton = new Button("Back");
        grid.add(backButton, 0, 3);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        JoinServerController controller = new JoinServerController(launcher, usernameField, ipField, portField);

        joinButton.setOnAction(e -> {
            if (isValidInput(usernameField, ipField, portField, errorLabel)) {
                LoadingManager.setLoading(true);
                toLoading.run(); // Navigate to loading screen
                controller.handleJoinServer(e);
            }
        });

        return new Scene(grid, 400, 300);
    }

    public Scene getCreateRoomScene(Launcher launcher) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Server IP Input
        Label nameLabel = new Label("Room Name: ");
        TextField nameField = new TextField();
        grid.add(nameLabel,0,0);
        grid.add(nameField, 2, 0);

        // Player number Input
        Label numberLabel = new Label("Number of Players: ");
        ChoiceBox<Integer> playerChoiceBox = new ChoiceBox<>();
        playerChoiceBox.getItems().addAll(2, 3, 4);
        playerChoiceBox.setValue(2);

        grid.add(numberLabel, 0, 1);
        grid.add(playerChoiceBox, 2, 1);

        // Create room Button
        Button createButton = new Button("Create Room");
        grid.add(createButton, 2, 3);
        // Create the controller and set the event handler
        CreateRoomController controller = new CreateRoomController(nameField, playerChoiceBox, launcher.getGameClient());
        createButton.setOnAction(controller::handleCreateRoom);

        // Back Button
        Button backButton = new Button("Back");
        grid.add(backButton, 0, 3);
        backButton.setOnAction(e -> {
            launcher.setScene(launcher.getRoomBrowserScene());
        });

        return new Scene(grid, 400, 300);
    }
    /**
     * Validates input fields and displays an error message if validation fails.
     */
    private boolean isValidInput(TextField nameOrUserField, TextField ipField, TextField portField, Label errorLabel) {
        String nameOrUser = nameOrUserField.getText().trim();
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();

        if (nameOrUser.isEmpty() || ip.isEmpty() || port.isEmpty()) {
            errorLabel.setText("All fields must be filled.");
            return false;
        }

        if (!ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
            errorLabel.setText("Invalid IP format.");
            return false;
        }

        if (!port.matches("\\d+")) {
            errorLabel.setText("Port must be a number.");
            return false;
        }

        int portNumber = Integer.parseInt(port);
        if (portNumber < 49152 || portNumber > 65535) {
            errorLabel.setText("Port must be between 1 and 65535.");
            return false;
        }

        errorLabel.setText(""); // Clear error message if all validations pass
        return true;
    }
}
