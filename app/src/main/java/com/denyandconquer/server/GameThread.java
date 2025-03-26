package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class GameThread extends Thread {
    private int playerNumber = 1;
    private Player player;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    List<GameThread> playerlist;
    RoomManager roomManager;
    private volatile boolean running = true;


    public GameThread(Socket socket, List<GameThread> list, RoomManager roomManager) {
        this.socket = socket;
        this.playerlist = list;
        this.roomManager = roomManager;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            this.playerlist.add(this);
            this.player = new Player(playerNumber);
            playerNumber++;

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
            case JOIN_ROOM -> {requestJoinRoom();}
            case LEAVE_ROOM -> {requestLeaveRoom();}
            case ROOM_LIST -> {requestRoomList(false);}
            case PLAYER_LIST -> {requestPlayerList();}
            case START_GAME -> {requestStartGame();}
            case CHECK_SQUARE -> {checkSquare();}
            case DRAW_SQUARE -> {drawSquare();}
            case FREE_SQUARE -> {releaseSquare();}
        }
    }

    public void stopThread() {
        running = false;
        cleanup();
    }

    private void cleanup() {
        synchronized (playerlist) {
            if (!playerlist.contains(this)){
                return;
            }
            playerlist.remove(this);
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

    private void sendToAll(Object msg) {
        for (GameThread player: playerlist) {
            player.sendToClient(msg);
        }
    }

    private void sendToRoom(Room room, Object msg) {
        for (GameThread player: room.getPlayerList()) {
            player.sendToClient(msg);
        }
    }

    public void sendToClient(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
            System.out.println("Message Sent");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Server] Cannot send message");
        }

    }

    private void requestCreateRoom(Message message) {
        // Create new room for the player and send it to the host
        Room newRoom = roomManager.CreateRoom(message.getRoomName(), message.getMaxPlayers());
        Message msg = new Message(Message.Type.CREATE_ROOM, newRoom, message.getRoomName(), player, message.getMaxPlayers());
        sendToClient(msg);

        // Let all the player to update the list of rooms
        requestRoomList(true);
    }

    private void requestJoinRoom() {

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
    private void requestPlayerList() {

    }

    private void requestStartGame() {

    }

    private void checkSquare() {

    }

    private void drawSquare() {

    }

    private void releaseSquare() {

    }

}
