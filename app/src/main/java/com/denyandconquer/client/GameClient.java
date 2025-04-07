package com.denyandconquer.client;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;
import com.denyandconquer.common.Square;
import com.denyandconquer.controllers.SceneController;
import com.denyandconquer.net.*;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.ConnectException;
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

    // ------------------- REQUEST SEND -------------------

    /**
     * Connects to the server and sets up streams.
     */
    public boolean connectToServer(String ip, int port, String name) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send player name to server
            send(new Message(MessageType.JOIN_SERVER, name));

            // Start listener thread
            startListening();
            return true;
        } catch (ConnectException ce) {
            System.out.println("🔴 Connection refused. Is the server running?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sends a create room request to the server.
     */
    public void sendCreateRoomRequest(String roomName, int maxPlayers, boolean isPrivate, String password) {
        NavigationRequest request = new NavigationRequest(roomName, maxPlayers, isPrivate ? password : null);
        send(new Message(MessageType.CREATE_ROOM_REQUEST, request));
    }


    /**
     * Requests to join a public room.
     */
    public void sendJoinRoomRequest(String roomId, String password) {
        if (password == null) {
            NavigationRequest request = new NavigationRequest(roomId);
            send(new Message(MessageType.JOIN_ROOM, request));
        } else {
            NavigationRequest request = new NavigationRequest(roomId, password);
            send(new Message(MessageType.JOIN_ROOM, request));
        }
    }

    /**
     * Sends a leave room request to the server.
     */
    public void sendLeaveRoomRequest() {
        send(new Message(MessageType.LEAVE_ROOM, null));
    }


    /**
     * Sends a start game request to the server.
     */
    public void sendStartGameRequest(String roomId) {
        send(new Message(MessageType.START_GAME, roomId));
    }

    /**
     * Sends mouse input as MouseData to the server.
     */
    public void sendMouseAction(MouseEvent e) {
        MouseAction action = getActionFromEvent(e);
        if (action == null) return;

        double absoluteX = e.getX(); // on overlay canvas
        double absoluteY = e.getY();

        int col = (int) (absoluteX / Square.WIDTH);
        int row = (int) (absoluteY / Square.HEIGHT);

        double x = absoluteX % Square.WIDTH;
        double y = absoluteY % Square.HEIGHT;

        MouseData data = new MouseData(row, col, x, y, action);
        send(new Message(MessageType.MOUSE_ACTION, data));
    }



    /**
     * Internal send method (synchronized).
     */
    private synchronized void send(Message message) {
        try {
            if (out != null) {
                out.reset();
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the MouseAction type from the MouseEvent.
     */
    private MouseAction getActionFromEvent(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) return MouseAction.PRESS;
        if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) return MouseAction.DRAG;
        if (e.getEventType() == MouseEvent.MOUSE_RELEASED) return MouseAction.RELEASE;
        return null;
    }


    // ------------------- RESPONSE RECEIVE -------------------

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
                System.out.println("❌ Disconnected from server.");
                Platform.runLater(() -> sceneController.handleServerDisconnect());
            }
        }).start();
    }

    /**
     * Handles messages received from the server.
     */
    private void handleServerMessage(Message message) {
        System.out.println("📥 [RECV] Message received: " + message.getType());

        switch (message.getType()) {
            case PLAYER_NAME_ACCEPTED -> {
                this.localPlayer = (Player) message.getData();
                System.out.println("✅ Name accepted: " + localPlayer.getName());
            }

            case PLAYER_NAME_REJECTED -> {
                System.out.println("❌ Name rejected by server.");
            }

            case ROOM_LIST_UPDATE -> {
                List<GameRoomDTO> rooms = (List<GameRoomDTO>) message.getData();
                System.out.println("📄 Room list received: " + rooms.size() + " rooms");
                Platform.runLater(() -> sceneController.updateLobby(rooms));
            }

            case ROOM_JOIN_SUCCESS, ROOM_CREATED -> {
                GameRoomDTO room = (GameRoomDTO) message.getData();
                System.out.println("🚪 Entered room: " + room.getRoomName());
                Platform.runLater(() -> {
                    sceneController.createRoom(room);
                    sceneController.showGameRoom();
                });
            }

            case PLAYER_ROOM_LIST_UPDATE -> {
                List<Player> players = (List<Player>) message.getData();
                System.out.println("👥 Player list in room: " + players.size());

                // ✅ Delegate responsibility to sceneController
                Platform.runLater(() -> sceneController.updateRoom(players));
            }


            case START_GAME -> {
                List<Player> players = (List<Player>) message.getData();
                System.out.println("🎮 Game starting with " + players.size() + " players.");
                sceneController.createGameController(this, new Board(), players, localPlayer);
                Platform.runLater(() -> {
                    this.gameController = sceneController.getGameController();
                    sceneController.startGame();
                });
            }

            case MOUSE_ACTION -> {
                MouseData data = (MouseData) message.getData();
                System.out.println("🖱️ Mouse action: " + data.getAction() + " at (" + data.getX() + ", " + data.getY() + ") by " + data.getPlayer().getName());

                boolean change = gameController.handleMouseAction(data.getPlayer(), data);
                if (change) {
                    Square updated = gameController.getBoard().getSquare(data.getX(), data.getY());
                    System.out.println("✅ [UI] Square updated at: (" + data.getX() + ", " + data.getY() + ")");
                    for (BoardUpdateListener listener : listeners) {
                        listener.onSquareUpdated(updated);
                    }
                }
            }

            default -> {
                System.out.println("⚠️ Unhandled message type: " + message.getType());
            }
        }
    }





}
