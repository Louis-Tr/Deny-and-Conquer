package com.denyandconquer.screens;

import com.denyandconquer.controllers.SceneController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Scene for setting up a new server.
 */
public class CreateServerScene {
    private final Scene scene;
    private final SceneController controller;

    public CreateServerScene(SceneController sceneController) {
        this.controller = sceneController;

        GridPane grid = createForm();

        TextField nameField = new TextField();
        TextField ipField = new TextField("127.0.0.1");
        TextField portField = new TextField("55555");
        Label errorLabel = createErrorLabel();

        grid.addRow(0, new Label("Server Name:"), nameField);
        grid.addRow(1, new Label("Server IP:"), ipField);
        grid.addRow(2, new Label("Port:"), portField);
        grid.add(errorLabel, 1, 4);

        Button createBtn = new Button("Create Server");
        Button backBtn = new Button("Back");

        grid.add(createBtn, 1, 3);
        grid.add(backBtn, 0, 3);

        createBtn.setOnAction(e -> {
            if (isValid(ipField, portField, errorLabel)) {
                controller.handleCreateServer(ipField, portField, nameField);
            }
        });

        backBtn.setOnAction(e -> controller.showMenuScene());

        this.scene = new Scene(grid, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red;");
        return label;
    }

    private boolean isValid(TextField ipField, TextField portField, Label errorLabel) {
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();

        if (!ip.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
            errorLabel.setText("Invalid IP format.");
            return false;
        }

        if (!port.matches("\\d+")) {
            errorLabel.setText("Port must be numeric.");
            return false;
        }

        int portNum = Integer.parseInt(port);
        if (portNum < 49152 || portNum > 65535) {
            errorLabel.setText("Port must be between 49152 and 65535.");
            return false;
        }

        errorLabel.setText("");
        return true;
    }
}
