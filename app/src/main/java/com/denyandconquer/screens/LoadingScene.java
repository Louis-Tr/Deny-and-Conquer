package com.denyandconquer.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * LoadingScene displays a loading indicator with a customizable message.
 */
public class LoadingScene {

    /**
     * Returns a generic loading scene.
     */
    public Scene getGenericLoadingScene() {
        return createScene("Loading, please wait...");
    }

    /**
     * Returns a loading scene for server creation.
     */
    public Scene getServerCreateLoadingScene() {
        return createScene("Creating server, please wait...");
    }

    /**
     * Returns a loading scene for joining a server.
     */
    public Scene getJoinServerLoadingScene() {
        return createScene("Joining server, please wait...");
    }

    /**
     * Internal method to create a loading scene with the given message.
     */
    private Scene createScene(String message) {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);

        Label loadingLabel = new Label(message);
        loadingLabel.setFont(new Font(20));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-alignment: center;");
        vbox.getChildren().addAll(progressIndicator, loadingLabel);

        pane.setCenter(vbox);
        return new Scene(pane, 400, 300);
    }
}
