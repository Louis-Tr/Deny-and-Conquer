package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * The GameThread class handles communication between the server and an individual client.
 * Each player has a dedicated GameThread to process their requests.
 */
public class GameThread extends Thread {
    private Player player;
    private int playerNumber;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private Map<Integer, GameThread> threadMap;
    RoomManager roomManager;
    private volatile boolean running = true;

    /**
     * Initializes the GameThread for a player
     *
     * @param socket Client socket for communication
     * @param playerNumber Unique ID assigned to the player
     * @param map Shared map of all active game threads
     * @param roomManager Manages game rooms
     */
    public GameThread(Socket socket, int playerNumber, Map<Integer, GameThread> map, RoomManager roomManager) {
        this.socket = socket;
        this.playerNumber = playerNumber;
        this.threadMap = map;
        this.roomManager = roomManager;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            this.threadMap.put(playerNumber, this);
            this.player = new Player(this.playerNumber);

            // Send player info to the client
            sendToClient(player);
        } catch (IOException e) {
            System.out.println("Client thread error");
        }

    }

    /**
     * Listens for incoming messages from the client and handles them accordingly.
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
            System.out.println("[Server] Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Handles incoming text based messages from the client.
     * @param message The message received
     */
    private void handleTextMessage(String message) {
        System.out.println(message);
    }

    /**
     * Handles game related messages received from the client.
     * @param message The game related message received
     */
    private void handleGameMessage(Message message) {
        switch (message.getType()) {
            case CREATE_ROOM -> {requestCreateRoom(message);}
            case JOIN_ROOM -> {requestJoinRoom(message);}
            case LEAVE_ROOM -> {requestLeaveRoom(message);}
            case ROOM_LIST -> {requestRoomList(message);}
            case PLAYER_LIST -> {requestPlayerList(message);}
            case START_GAME -> {requestStartGame();}
            case CHECK_SQUARE -> {checkSquare();}
            case DRAW_SQUARE -> {drawSquare();}
            case FREE_SQUARE -> {releaseSquare();}
        }
    }

    /**
     * Sends a message to all connected clients.
     * @param msg The message to send
     */
    private void sendToAll(Object msg) {
        for (GameThread thread: threadMap.values()) {
            thread.sendToClient(msg);
        }
    }

    /**
     * Sends a message to all players in a specific game room.
     * @param room The game room
     * @param msg The message to send
     */
    private void sendToRoom(Room room, Object msg) {
        for (Player player: room.getPlayerList()) {
            GameThread thread = threadMap.get(player.getPlayerNumber());
            if (thread != null) {
                thread.sendToClient(msg);
            }
        }
    }

    /**
     * Sends a message to the client associated with this thread.
     * @param msg The message to send
     */
    public void sendToClient(Object msg) {
        try {
            out.reset();
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Server] Cannot send message");
        }

    }

    /**
     * Handles the room creation.
     * @param message The message containing room creation info
     */
    private void requestCreateRoom(Message message) {
        // Create new room for the player and send it to the host
        Room newRoom = roomManager.CreateRoom(message.getRoomName(), message.getMaxPlayers());
        newRoom.addPlayer(player);
        Message msg = new Message(Message.Type.ENTER_ROOM, newRoom);
        sendToClient(msg);

        // Notify room list update to all client
        Message newMsg = new Message(Message.Type.ROOM_LIST, true);
        requestRoomList(newMsg);
    }

    /**
     * Handles a player's request to join an existing game room.
     * @param message The message containing the room ID to join
     */
    private void requestJoinRoom(Message message) {
        // Find the room the player wants to join
        int roomId = (int) message.getData();
        Room joinRoom = roomManager.getRoomByID(roomId);

        if (joinRoom != null && !joinRoom.isFull()){
            // Add the player to the room
            joinRoom.addPlayer(player);
            Message msg = new Message(Message.Type.ENTER_ROOM, joinRoom);
            sendToClient(msg);

            // Notify players in the room about the new player
            msg = new Message(Message.Type.PLAYER_LIST, joinRoom.getPlayerList());
            sendToRoom(joinRoom, msg);
        } else if (joinRoom == null) {
            System.out.println("[Server] Room not found");
        } else{
            System.out.println("[Server] Room is full");
        }
    }

    /**
     * Handles a player's request to leave a room.
     * @param message The message containing the Room ID to leave
     */
    private void requestLeaveRoom(Message message) {
        //1. first find the room the player belongs to
        int roomId = (int) message.getData();
        Room currentRoom = roomManager.getRoomByID(roomId);

        if(currentRoom != null){
            //2. Remove the player from the room
            currentRoom.removePlayer(player);

            //3. Notify the players in the room about the deletion
            Message msg = new Message(Message.Type.PLAYER_LIST, currentRoom.getPlayerList());
            sendToRoom(currentRoom, msg);

            //4. If the room is empty, delete the room.
            if(currentRoom.getPlayerList().isEmpty()){
                roomManager.removeRoom(currentRoom.getRoomId());
            }

            //5. Notify all clients about the updated room list
            Message newMsg = new Message(Message.Type.ROOM_LIST, true);
            requestRoomList(newMsg);

            //6. Send message to client to update the UI
            Message leaveMsg = new Message(Message.Type.LEAVE_ROOM, null);
            sendToClient(leaveMsg);
        }
        else {
            System.out.println("[Server] Empty Room");
        }
    }

    /**
     * Handles a request for the list of available game rooms.
     * @param message The message containing info about broadcast
     */
    private void requestRoomList(Message message) {
        List<Room> list = roomManager.getRoomList();
        Message msg = new Message(Message.Type.ROOM_LIST, list);
        boolean isBroadcast = (boolean) message.getData();
        if (isBroadcast) {
            sendToAll(msg);
        } else{
            sendToClient(msg);
        }
    }


    private void requestPlayerList(Message msg) {

    }

    private void requestStartGame() {

    }

    private void checkSquare() {

    }

    private void drawSquare() {

    }

    private void releaseSquare() {

    }

    /**
     * Returns the player associated with this thread.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Stops the game thread and calls cleanup.
     */
    public void stopThread() {
        running = false;
        cleanup();
    }

    /**
     * Cleans up resources and removes the thread from the thread map.
     */
    private void cleanup() {
        synchronized (threadMap) {
            if (!threadMap.containsKey(playerNumber)){
                return;
            }
            threadMap.remove(playerNumber);
        }
        try {
            System.out.println("[Server] Cleaning up thread for " + player.getName());

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            in.close();
            out.close();

        } catch (IOException e) {
            System.out.println("[Server] Cleanup error for " + player.getName() + ": " + e.getMessage());
        }
    }
}
