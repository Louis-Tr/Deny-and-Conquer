package com.denyandconquer.screens;

import com.denyandconquer.controllers.CreateRoomController;
import com.denyandconquer.server.GameClient;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import com.denyandconquer.controllers.CreateServerController;
import com.denyandconquer.controllers.JoinServerController;

/**
 * The InputScene class provides scenes for user inputs to create or join a server.
 * It encapsulates the UI elements and ties them to their respective controllers.
 * Additionally, it adds a "Back" button to allow navigation to the previous scene.
 */
public class InputScene {
    Label titleLabel;

    /**
     * Creates and returns a Scene for creating a new server.
     * <p>
     * This scene includes input fields for the server name, server IP, and port,
     * a button that triggers the create server logic in the CreateServerController,
     * and a "Back" button to return to the previous scene.
     * </p>
     *
     * @param onBack a Runnable callback that is executed when the "Back" button is clicked.
     *               This should handle the navigation to the previous scene.
     * @return a Scene for creating a server with the specified input fields and back navigation.
     */
    public Scene getCreateServerScene(Runnable onBack) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Server Name Input
        Label nameLabel = new Label("Server Name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Server IP Input
        Label ipLabel = new Label("Server IP:");
        TextField ipField = new TextField();
        grid.add(ipLabel, 0, 1);
        grid.add(ipField, 1, 1);

        // Port Input
        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);

        // Create Server Button
        Button createButton = new Button("Create Server");
        grid.add(createButton, 1, 3);

        // Back Button
        Button backButton = new Button("Back");
        grid.add(backButton, 0, 3);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // Create a controller instance, passing in the controls
        CreateServerController controller = new CreateServerController(nameField, ipField, portField);
        createButton.setOnAction(controller::handleCreateServer);

        return new Scene(grid, 400, 300);
    }

    /**
     * Creates and returns a Scene for joining an existing server.
     * <p>
     * This scene includes input fields for the username, server IP, and port,
     * a button that triggers the join server logic in the JoinServerController,
     * and a "Back" button to return to the previous scene.
     * </p>
     *
     * @param onBack a Runnable callback that is executed when the "Back" button is clicked.
     *               This should handle the navigation to the previous scene.
     * @return a Scene for joining a server with the specified input fields and back navigation.
     */
    public Scene getJoinServerScene(Runnable onBack) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Username Input
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        // Server IP Input
        Label ipLabel = new Label("Server IP:");
        TextField ipField = new TextField();
        grid.add(ipLabel, 0, 1);
        grid.add(ipField, 1, 1);

        // Port Input
        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);

        // Join Server Button
        Button joinButton = new Button("Join Server");
        grid.add(joinButton, 1, 3);

        // Back Button
        Button backButton = new Button("Back");
        grid.add(backButton, 0, 3);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // Create the controller and set the event handler
        JoinServerController controller = new JoinServerController(usernameField, ipField, portField);
        joinButton.setOnAction(controller::handleJoinServer);

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
}
