package com.denyandconquer.controllers;

import com.denyandconquer.Launcher;
import com.denyandconquer.client.GameClientController;
import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.net.BoardUpdateListener;
import com.denyandconquer.net.GameRoomDTO;
import com.denyandconquer.screens.*;
import com.denyandconquer.client.GameClient;
import com.denyandconquer.common.GameRoom;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.util.List;


public class SceneController {
    private final Launcher launcher;
    private final Stage stage;
    private MenuScene menuScene;
    private CreateServerScene createServerScene;
    private JoinServerScene joinServerScene;
    private LobbyScene lobbyScene;
    private GameRoomScene gameRoomScene;
    //private GameOverScene gameOverScene;
    private GameScene gameScene;
    private GameClientController gameController;
    private GameRoom room;

    public SceneController(Launcher launcher, Stage stage) {
        this.launcher = launcher;
        this.stage = stage;
    }

    /**
     * Show the Menu Scene.
     */
    public void showMenuScene() {
        System.out.println("Showing Menu Scene");
        if (menuScene == null) {
            menuScene = new MenuScene(this);
        }
        stage.setScene(menuScene.getScene());
    }

    /**
     * Shows the Create Server Scene.
     */
    public void showCreateServerScene() {
        System.out.println("Showing Create Server Scene");
        if (createServerScene == null) {
            createServerScene = new CreateServerScene(this);
        }
        stage.setScene(createServerScene.getScene());
    }

    /**
     * Shows the Join Server Scene.
     */
    public void showJoinServerScene() {
        System.out.println("Showing Join Server Scene");
        if (joinServerScene == null) {
            joinServerScene = new JoinServerScene(this);
        }
        stage.setScene(joinServerScene.getScene());
    }

    public void showLobbyScene() {
        System.out.println("Showing Lobby Scene");
        if (lobbyScene == null) {
            lobbyScene = new LobbyScene(this);
        }
        stage.setScene(lobbyScene.getScene());
    }

    public void showGameScene() {
        System.out.println("Showing Game Scene");
        if (gameScene == null) {
            gameScene = new GameScene(this);
            launcher.getClient().addBoardUpdateListener((BoardUpdateListener) gameScene);
        }
        stage.setScene(gameScene.getScene());
    }

    public void updateLobby(List<GameRoomDTO> rooms) {
        if (lobbyScene != null) {
            lobbyScene.updateList(rooms);
        }
    }


    /**
     * Handles the Create Server button click event.
     * Set loading to true
     * Run createServer logic
     * Check if server was created successfully
     * If successful, show the Lobby Scene
     * Set loading to false
     * @param ipField The IP address field
     * @param portField The port field
     * @param nameField The name field
     *
     */
    public boolean handleCreateServer(TextField ipField, TextField portField, TextField nameField) {
        System.out.println("Attempting to Create Server");
        LoadingManager.setLoading(true, "create");
        String name = nameField.getText();
        String ip = ipField.getText();
        int port = Integer.parseInt(portField.getText());

        // Call the startServer method from launcher
        launcher.startServer(ip, port);
        if (launcher.getServer() != null) {
            System.out.println("Successfully Create Server");
            launcher.connectClient(ip, port, name);
            if (launcher.getClient() != null) {
                showLobbyScene();
                LoadingManager.setLoading(false);
                return true;
            }
        }
        System.out.println("Failed to Create Server");
        LoadingManager.setLoading(false);
        return false;
    }



    public boolean handleJoinServer(TextField ipField, TextField portField, TextField nameField) {
        System.out.println("Attempting to Join Server");
        LoadingManager.setLoading(true, "join");
        String name = nameField.getText().trim();
        String ip = ipField.getText();
        int port = Integer.parseInt(portField.getText());

        // Call the startServer method from launcher
        launcher.connectClient(ip, port, name);
        if (launcher.getClient() != null) {
            showLobbyScene();
            LoadingManager.setLoading(false);
            return true;
        }
        System.out.println("Failed to Join Server");
        LoadingManager.setLoading(false);
        return false;
    }

    public void handleServerDisconnect() {
        showMenuScene();
        menuScene.showErrorDialog();
    }

    public void handleGameOver(Player winner) {
        showGameRoom();
        gameRoomScene.showWinnerDialog(winner);
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public Stage getPrimaryStage() {
        return stage;
    }

    public void updateRoom(List<Player> players) {
        if (gameRoomScene != null) {
            gameRoomScene.updatePlayerList(players);
        } else {
            System.out.println("GameRoomScene is null");
        }
    }

    public void startGame() {
        System.out.println("Starting Game");
        if (gameScene == null) {
            gameScene = new GameScene(this);
            launcher.getClient().addBoardUpdateListener(gameScene);
        }
        stage.setScene(gameScene.getScene());
    }



    public void createRoom(GameRoomDTO room) {
        System.out.println("Creating Room");
        if (gameRoomScene == null) {
            gameRoomScene = new GameRoomScene(this, room);
        }
    }

    public void showGameRoom() {
        System.out.println("Showing Game Room Scene");
        if (gameRoomScene == null) {
            System.out.println("Showing Game Room Scene");
        }
        stage.setScene(gameRoomScene.getScene());
    }

    public GameClientController getGameController() {
        return gameController;
    }

    public void createGameController(GameClient client, Board board, List<Player> players, Player localPlayer) {
        this.gameController = new GameClientController(client, board, players, localPlayer);
    }
}
