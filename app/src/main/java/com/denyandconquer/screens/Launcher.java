package com.denyandconquer.screens;

import com.denyandconquer.server.GameClient;
import com.denyandconquer.server.GameServer;
import com.denyandconquer.server.Room;
import com.denyandconquer.global_state.LoadingManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Launcher is the main entry point for the Deny and Conquer application.
 * It displays a launcher scene with options to create or join a server.
 * When an option is selected, it navigates to the corresponding scene.
 * Each scene includes a "Back" button that returns the user to the launcher scene.
 */
public class Launcher extends Application {

    // Store the launcher scene and room browser scene
    // so it can be reused in callbacks.
    private Stage primaryStage;
    private Scene launcherScene;
    private RoomBrowserScene roomBrowserScene;
    private GameServer server;
    private GameClient gameClient;
    private Scene loadingSceneJoinServer = new LoadingScene().getLoadingScene("Join Server");
    private Scene loadingSceneCreateServer = new LoadingScene().getLoadingScene("Create Server");


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
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Deny and Conquer Launcher");

        // Create buttons for options
        Button createServerBtn = new Button("Create Server");
        Button playBtn = new Button("Play");

        // Listen for loading completion
        LoadingManager.loadingProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) { // When loading completes
                primaryStage.setScene(GameScene.getGameScene());
            }
        });


        // Set actions for each button using the InputScene class and its callback.
        createServerBtn.setOnAction(e -> {

            // Create and start the server in a new thread
            if (server == null) {
                server = new GameServer();
                new Thread(server::startServer).start();
            } else {
                System.out.println("Server is already running.");
            }

//            Scene createServerScene = new InputScene().getCreateServerScene(() -> primaryStage.setScene(launcherScene));
//            primaryStage.setScene(createServerScene);
        });

        playBtn.setOnAction(e -> {
            System.out.println("Play button clicked!");

            // Find the port number and start new GameClient
            int portNumber = findPortNumber();
            if (portNumber != -1) {
                // Start new GameClient
                GameClient client = new GameClient("localhost", portNumber, this);
                client.start();
                this.gameClient = client;

                this.roomBrowserScene = new RoomBrowserScene(this);
                client.roomList();
                primaryStage.setScene(roomBrowserScene.getRoomBrowserScene());
            }

//            Scene joinServerScene = new InputScene().getJoinServerScene(() -> primaryStage.setScene(launcherScene));
//            primaryStage.setScene(joinServerScene);
        });

        // Close server when closing window
        primaryStage.setOnCloseRequest(e -> {
            stopApplication();
            System.out.println("Create Server clicked!");
            Scene createServerScene = new InputScene().getCreateServerScene(
                    () -> primaryStage.setScene(launcherScene),
                    () -> primaryStage.setScene(loadingSceneCreateServer));
            primaryStage.setScene(createServerScene);
        });

        joinServerBtn.setOnAction(e -> {
            System.out.println("Join Server clicked!");
            Scene joinServerScene = new InputScene().getJoinServerScene(
                    () -> primaryStage.setScene(launcherScene),
                    () -> primaryStage.setScene(loadingSceneJoinServer));
            primaryStage.setScene(joinServerScene);
        });

        // Arrange the buttons in a vertical layout
        VBox layout = new VBox(20); // 20 pixels of spacing
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(createServerBtn, playBtn);
        // Create the launcher scene and set it on the primary stage
        launcherScene = new Scene(layout, 400, 300);
        primaryStage.setScene(launcherScene);
        primaryStage.show();
    }

    public void setScene(Scene newScene) {
        Platform.runLater(() -> primaryStage.setScene(newScene));
    }
    public Scene getLaucherScene() {
        return launcherScene;
    }

    public Scene getRoomBrowserScene() {
        return roomBrowserScene.getRoomBrowserScene();
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    private void stopApplication() {
        if (gameClient != null) {
            gameClient.disconnect();
        }
        if (server != null) {
            server.stopServer();
        }
        System.exit(0);
    }

    private int findPortNumber() {
        int portNumber = -1;

        // Read the port number from file
        try (BufferedReader reader = new BufferedReader(new FileReader("ports.txt"))) {
            portNumber = Integer.parseInt(reader.readLine());
        } catch (IOException ex) {
            System.out.println("Reading port number error");
        }

        return portNumber;
    }

    public void updateRoomList(List<Room> list) {
        Platform.runLater(() -> {
            roomBrowserScene.updateList(list);
        });
    }
}
