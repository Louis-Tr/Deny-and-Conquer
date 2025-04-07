package com.denyandconquer;

import com.denyandconquer.client.GameClientController;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.client.GameClient;
import com.denyandconquer.servers.GameServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Launcher extends Application {
    private Stage primaryStage;
    private SceneController sceneController;
    private GameServer server;
    private GameClient client;
    private GameClientController gameClientController;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.sceneController = new SceneController(this, primaryStage);

        primaryStage.setTitle("Deny and Conquer");
        primaryStage.show();
        sceneController.showMenuScene();

        primaryStage.setOnCloseRequest(event -> {
            if (server != null) server.stop();
//            if (client != null) client.stop();

            Platform.exit();
            System.exit(0);
        });
    }

    @Override
    public void stop() throws Exception {
        if (server != null) server.stop();
        super.stop();
    }

    // === Server - Client Setup ===

    /**
     * Starts the local game server on the given port.
     */
    public void startServer(String ip, int port) {
//        System.out.println("Starting server on " + ip + ":" + port);
        this.server = new GameServer(port);
        Thread serverThread = new Thread(() -> {server.start();});
        serverThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!server.isStarted()) {
            this.server = null;
        }
    }

    /**
     * Connects a new client to the game server.
     */
    public void connectClient(String ip, int port, String playerName) {
        this.client = new GameClient(this.sceneController);
        boolean success = client.connectToServer(ip, port, playerName);

        if (!success) {
           this.client = null;
        }
    }

    public GameServer getServer() {
        return server;
    }

    public GameClient getClient() {
        return client;
    }

    public SceneController getSceneController() {
        return sceneController;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public GameClientController getGameClientController() {
        return gameClientController;
    }

    public void setGameClientController(GameClientController gameClientController) {
        this.gameClientController = gameClientController;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    public void setClient(GameClient client) {
        this.client = client;
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }
}
