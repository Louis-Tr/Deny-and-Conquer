package com.denyandconquer.server;

import com.denyandconquer.screens.GameRoomScene;
import com.denyandconquer.screens.Launcher;
import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * The GameClient class handles communication between the client and the game server through GameThread.
 * It establishes a connection, sends and receives messages, and updates the UI accordingly.
 *
 */
public class GameClient extends Thread {
    private Launcher launcher;
    private Player player;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Creates a new GameClient instance and connects to the server.
     *
     * @param serverAddress The IP address of the game server.
     * @param port The port number for the server connection.
     * @param launcher The main launcher handling UI transitions.
     */
    public GameClient(String serverAddress, int port, Launcher launcher) {
        this.launcher = launcher;
        try {
            this.socket = new Socket(serverAddress, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            // Read the player object from server
            try {
                this.player = (Player) in.readObject();
                System.out.println("You are " + player.getName());
            } catch (Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Listens for incoming messages from the server and handles them.
     */
    @Override
    public void run() {
        try {
            Object message;
            while ((message = in.readObject()) != null) {
                if (message instanceof String) {
                    handleTextMessage((String) message);
                } else if (message instanceof Message) {
                    handleGameMessage((Message) message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Client] Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Handles incoming text messages from the server.
     * @param message The text message received.
     */
    private void handleTextMessage(String message) {
        System.out.println(message);
    }

    /**
     * Handles game related messages received from the server.
     * @param message The game related message received.
     */
    private void handleGameMessage(Message message) {
        switch (message.getType()) {
            case ENTER_ROOM:
                // Update player list in the current room and enter the room
                Room room = (Room) message.getData();
                GameRoomScene gameRoomScene = new GameRoomScene(launcher, room);
                launcher.setGameRoomScene(gameRoomScene);
                launcher.setScene(gameRoomScene.getRoomScene());
                launcher.updatePlayerList(room.getPlayerList());
                break;
            case LEAVE_ROOM: {
                // Update player list when leaving and leave the room
                System.out.println("[Client] Left the room");
                launcher.updatePlayerList(List.of());
                launcher.setScene(launcher.getRoomBrowserScene());
            }
            case ROOM_LIST:
                // Update the list of available game rooms
                List<Room> roomList = (List<Room>) message.getData();
                launcher.updateRoomList(roomList);
                break;
            case PLAYER_LIST:
                // Update the list of players in the current room
                List<Player> playerList = (List<Player>) message.getData();
                launcher.updatePlayerList(playerList);
                // Request room list to update the number of player in room title
                sendRoomListRequest(true);
                break;
            case START_GAME:
                System.out.println("Game started");
                break;
            default:
                System.out.println("Unknown message");
                break;
        }
    }

    /**
     * Sends message to the server.
     * @param msg The message to send.
     */
    public void send(Object msg) {
        try {
            out.reset();
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Sends a request to the server to create a new game room.
     * @param roomName The name of the new room.
     * @param maxPlayers The maximum number of players allowed in the room.
     */
    public void sendCreateRoomRequest(String roomName, int maxPlayers) {
        Message msg = new Message(Message.Type.CREATE_ROOM, roomName, player, maxPlayers);
        send(msg);
    }

    /**
     * Sends a request to the server to join an existing room.
     * @param roomId The ID of the room to join.
     */
    public void sendJoinRoomRequest(int roomId) {
        Message msg = new Message(Message.Type.JOIN_ROOM, roomId, player);
        send(msg);
    }

    /**
     * Sends a request to the server to leave the current game room.
     * @param roomId The ID of the room to leave.
     */
    public void sendLeaveRoomRequest(int roomId){
        Message msg = new Message(Message.Type.LEAVE_ROOM, roomId, player);
        send(msg);
    }

    /**
     * Sends a request to the server to get the list of available game rooms.
     * @param isBroadcast If true, server sends the response to all players.
     */
    public void sendRoomListRequest(boolean isBroadcast){
        Message msg = new Message(Message.Type.ROOM_LIST, isBroadcast);
        send(msg);
    }

    /**
     * Sends a request to the server to start the game.
     */
    public void sendStartGameRequest() {
        Message msg = new Message(Message.Type.START_GAME, null, player);
        send(msg);
    }

    /**
     * Disconnects the client from the server.
     */
    public void disconnect() {
        try {
            cleanup();
            System.out.println("[Client] Client disconnected.");
        } catch (Exception e) {
            System.out.println("[Client] Cannot disconnect.");
        }
    }

    /**
     * Cleans up resources when the client disconnects.
     */
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Cleanup error for " + player.getName() + ": " + e.getMessage());
        }
    }
}