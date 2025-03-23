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
        while (running && socket.isConnected()) {
            try {
                Object message = in.readObject();

                if (message instanceof String) {
                    handleTextMessage((String) message);
                } else if (message instanceof Message) {
                    handleGameMessage((Message) message);
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        }
        cleanup();
    }

    private void handleTextMessage(String message) {
        System.out.println(message);
    }

    private void handleGameMessage(Message message) {
        switch (message.getType()) {
            case CREATE_ROOM -> {requestCreateRoom(message);}
            case JOIN_ROOM -> {requestJoinRoom();}
            case LEAVE_ROOM -> {requestLeaveRoom();}
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

    public void sendToClient(Object msg) {
        try {
            out.reset();
            out.writeObject(msg);
            System.out.println(msg);
        } catch (IOException e) {
            System.out.println("[Server] Cannot send message");
        }

    }

    private void requestCreateRoom(Message message) {
        Room newRoom = roomManager.CreateRoom(message.getRoomName(), message.getMaxPlayers());
        sendToClient(newRoom);
    }

    private void requestJoinRoom() {

    }

    private void requestLeaveRoom() {

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
