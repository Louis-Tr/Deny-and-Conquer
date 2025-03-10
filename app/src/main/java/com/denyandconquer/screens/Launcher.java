package com.denyandconquer.screens;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Launcher is the main entry point for the Deny and Conquer application.
 * It displays a launcher scene with options to create or join a server.
 * When an option is selected, it navigates to the corresponding scene.
 * Each scene includes a "Back" button that returns the user to the launcher scene.
 */
public class Launcher extends Application {

    // Store the launcher scene so it can be reused in callbacks.
    private Scene launcherScene;

    /**
     * Main method that launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Sets up the primary stage with the launcher scene and configures navigation
     * to the create server or join server scenes with callbacks for the "Back" button.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Deny and Conquer Launcher");

        // Create buttons for options
        Button createServerBtn = new Button("Create Server");
        Button joinServerBtn = new Button("Join Server");

        // Set actions for each button using the InputScene class and its callback.
        createServerBtn.setOnAction(e -> {
            System.out.println("Create Server clicked!");
            Scene createServerScene = new InputScene().getCreateServerScene(() -> primaryStage.setScene(launcherScene));
            primaryStage.setScene(createServerScene);
        });

        joinServerBtn.setOnAction(e -> {
            System.out.println("Join Server clicked!");
            Scene joinServerScene = new InputScene().getJoinServerScene(() -> primaryStage.setScene(launcherScene));
            primaryStage.setScene(joinServerScene);
        });

        // Arrange the buttons in a vertical layout
        VBox layout = new VBox(20); // 20 pixels of spacing
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(createServerBtn, joinServerBtn);

        // Create the launcher scene and set it on the primary stage
        launcherScene = new Scene(layout, 400, 300);
        primaryStage.setScene(launcherScene);
        primaryStage.show();
    }
}
