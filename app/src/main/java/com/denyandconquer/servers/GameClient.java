package com.denyandconquer.servers;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.controllers.GameClientController;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.*;

import javafx.application.Platform;
import javafx.geometry.Point2D;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles client-side connection and communication with the game server.
 */
public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Player localPlayer;
    private GameClientController gameController;
    private SceneController sceneController;
    private final List<BoardUpdateListener> listeners = new ArrayList<>();

    public void addBoardUpdateListener(BoardUpdateListener listener) {
        listeners.add(listener);
    }

    public GameClient(SceneController sceneC) {
        this.sceneController = sceneC;
    }

    /**
     * Connects to the server and sets up streams.
     */
    public void connectToServer(String ip, int port, String name) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send player name to server
            send(new Message(MessageType.PLAYER_NAME_SET_REQUEST, name));

            // Start listener thread
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a tile lock request to the server.
     */
    public void sendLockRequest(int x, int y, Point2D localPosition) {
        TileClickData data = new TileClickData(localPlayer, x, y, localPosition);
        send(new Message(MessageType.CLICK_ON_TILE, data));
    }

    /**
     * Sends a draw action on a tile to the server.
     */
    public void sendDrawAction(int x, int y, Point2D localPostion) {
        DrawData draw = new DrawData(localPlayer, x, y, localPostion);
        send(new Message(MessageType.DRAW_ON_TILE, draw));
    }

    /**
     * Sends a release request for a tile.
     */
    public void sendReleaseRequest(int x, int y) {
        DrawData release = new DrawData(localPlayer, x, y, null); // localX/Y unused here
        send(new Message(MessageType.RELEASE_TILE, release));
    }

    /**
     * Requests to join a room (public room).
     */
    public void sendJoinRoomRequest(String roomId, Object unused) {
        JoinRoomRequest request = new JoinRoomRequest(localPlayer, roomId);
        send(new Message(MessageType.JOIN_ROOM, request));
    }

    /**
     * Requests to join a private room with password.
     */
    public void sendJoinRoomWithPasswordRequest(String roomId, String password) {
        JoinRoomWithPasswordRequest request = new JoinRoomWithPasswordRequest(localPlayer, roomId, password);
        send(new Message(MessageType.JOIN_ROOM_WITH_PASSWORD, request));
    }

    /**
     * Sends a create room request to the server.
     */
    public void sendCreateRoomRequest(String roomName, int maxPlayers, boolean isPrivate, String password) {
        CreateRoomRequest request = new CreateRoomRequest(roomName, maxPlayers, isPrivate, password, localPlayer);
        send(new Message(MessageType.CREATE_ROOM_REQUEST, request));
    }

    /**
     * Internal send method (synchronized).
     */
    private synchronized void send(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a listener thread to receive and process server messages.
     */
    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Message message = (Message) in.readObject();
                    handleServerMessage(message);
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server.");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handles messages received from the server.
     */
    private void handleServerMessage(Message message) {
        switch (message.getType()) {
            case PLAYER_NAME_ACCEPTED -> {
                this.localPlayer = (Player) message.getData();
                System.out.println("✅ Name accepted: " + localPlayer.getName());
            }

            case PLAYER_NAME_REJECTED -> {
                System.out.println("❌ Name rejected.");
            }

            case TILE_UPDATE -> {
                Square updatedSquare = (Square) message.getData();
                System.out.println("Tile updated: " + updatedSquare);

                Platform.runLater(() -> {
                    Square localSquare = gameController.getBoard().getSquare(updatedSquare.getPosition());
                    if (localSquare != null) {
                        localSquare.copyStateFrom(updatedSquare);
                        for (BoardUpdateListener l : listeners) {
                            l.onSquareUpdated(localSquare);
                        }
                    }
                });
            }

            case ROOM_LIST_UPDATE -> {
                List<GameRoomDTO> rooms = (List<GameRoomDTO>) message.getData();
                Platform.runLater(() -> sceneController.updateLobby(rooms));
            }

            case ROOM_JOIN_SUCCESS -> {
                GameRoomDTO room = (GameRoomDTO) message.getData();
                Platform.runLater(() -> {
                    sceneController.createRoom(room);
                    sceneController.showGameRoom();
                });
            }

            case ROOM_CREATED -> {
                GameRoomDTO room = (GameRoomDTO) message.getData();
                Platform.runLater(() -> {
                    sceneController.createRoom(room);
                    sceneController.showGameRoom();
                });
            }

            case START_GAME -> {
                List<Player> players = (List<Player>) message.getData();
                sceneController.createGameController(this, new Board(), players, localPlayer);
                Platform.runLater(() -> {
                    this.gameController = sceneController.getGameController();
                    sceneController.startGame();
                });
            }

            default -> {
                System.out.println("Unhandled message: " + message.getType());
            }
        }
    }



    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void sendStartGameRequest(String roomId) {
        send(new Message(MessageType.START_GAME, roomId));
    }

    public void sendLeaveRoomRequest(String roomId) {
    }
}
