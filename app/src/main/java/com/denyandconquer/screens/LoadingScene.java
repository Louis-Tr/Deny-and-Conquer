package com.denyandconquer.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LoadingScene {

    /**
     * Returns a loading scene based on the specified operation with a loading circle.
     *
     * @param operation Either "Join Server" or "Create Server"
     * @return A Scene displaying a loading indicator and a message.
     */
    public Scene getLoadingScene(String operation) {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        // Create a circular progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);

        // Determine the loading message based on the operation
        String message;
        if ("Join Server".equalsIgnoreCase(operation)) {
            message = "Joining server, please wait...";
        } else if ("Create Server".equalsIgnoreCase(operation)) {
            message = "Creating server, please wait...";
        } else {
            message = "Loading, please wait...";
        }

        Label loadingLabel = new Label(message);
        loadingLabel.setFont(new Font(20));

        // Arrange the indicator and label vertically
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-alignment: center;");
        vbox.getChildren().addAll(progressIndicator, loadingLabel);

        pane.setCenter(vbox);
        return new Scene(pane, 400, 300);
    }
}
