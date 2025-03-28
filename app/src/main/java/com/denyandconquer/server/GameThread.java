package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class GameThread extends Thread {
    private Player player;
    private int playerNumber;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private Map<Integer, GameThread> threadMap;
    RoomManager roomManager;
    private volatile boolean running = true;


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

            sendToClient(player);
        } catch (IOException e) {
            System.out.println("Client thread error");
        }

    }

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

    private void handleTextMessage(String message) {
        System.out.println(message);
    }

    private void handleGameMessage(Message message) {
        switch (message.getType()) {
            case CREATE_ROOM -> {requestCreateRoom(message);}
            case JOIN_ROOM -> {requestJoinRoom(message);}
            case LEAVE_ROOM -> {requestLeaveRoom();}
            case ROOM_LIST -> {requestRoomList(false);}
            case PLAYER_LIST -> {requestPlayerList(message);}
            case START_GAME -> {requestStartGame();}
            case CHECK_SQUARE -> {checkSquare();}
            case DRAW_SQUARE -> {drawSquare();}
            case FREE_SQUARE -> {releaseSquare();}
        }
    }



    private void sendToAll(Object msg) {
        for (GameThread player: threadMap.values()) {
            player.sendToClient(msg);
        }
    }

    private void sendToRoom(Room room, Object msg) {
        for (Player player: room.getPlayerList()) {
            GameThread thread = threadMap.get(player.getPlayerNumber());
            if (thread != null) {
                thread.sendToClient(msg);
            }
        }
    }

    public void sendToClient(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Server] Cannot send message");
        }

    }

    private void requestCreateRoom(Message message) {
        // Create new room for the player and send it to the host
        Room newRoom = roomManager.CreateRoom(message.getRoomName(), message.getMaxPlayers());
        newRoom.addPlayer(player);
        Message msg = new Message(Message.Type.ENTER_ROOM, newRoom);
        sendToClient(msg);

        // Notify room list update to all client
        requestRoomList(true);
    }

    private void requestJoinRoom(Message message) {
        int roomId = (int) message.getData();
        Room joinRoom = roomManager.getRoomByID(roomId);
        if (joinRoom != null && !joinRoom.isFull()){
            joinRoom.addPlayer(player);
            Message msg = new Message(Message.Type.ENTER_ROOM, joinRoom);
            sendToClient(msg);

            // Notify player list update to clients in the room
            msg = new Message(Message.Type.PLAYER_LIST, joinRoom.getPlayerList());
            sendToRoom(joinRoom, msg);
        } else if (joinRoom == null) {
            System.out.println("[Server] Room not found");
        } else{
            System.out.println("[Server] Room is full");
        }

    }

    private void requestLeaveRoom() {

    }
    private void requestRoomList(boolean isBroadcast) {
        List<Room> list = roomManager.getRoomList();
        Message msg = new Message(Message.Type.ROOM_LIST, list);

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

    public Player getPlayer() {
        return player;
    }

    public void stopThread() {
        running = false;
        cleanup();
    }

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
