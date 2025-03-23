package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class GameThread extends Thread {
    private int playerNumber;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    List<GameThread> playerlist;
    RoomManager roomManager;

    public GameThread(Socket socket, List<GameThread> list, RoomManager roomManager) {
        this.socket = socket;
        this.playerlist = list;
        this.roomManager = roomManager;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            this.playerlist.add(this);
            this.playerNumber = playerlist.size();
            Player newPlayer = new Player(playerNumber);

            sendToClient(newPlayer);
        } catch (IOException e) {
            System.out.println("Client thread error");
        }

    }

    @Override
    public void run() {
//        String[] request = null;
        while (socket.isConnected()) {
            Object received = null;
            try {
                received = in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[Server] error");
            }

            System.out.println("[Server] received: " + received);
            if (received instanceof Message message){
                switch (message.getType()) {
                    case CREATE_ROOM -> {requestCreateRoom(message);}
                    case JOIN_ROOM -> {requestJoinRoom();}
                    case LEAVE_ROOM -> {requestLeaveRoom();}
                    case START_GAME -> {requestStartGame();}
                    case CHECK_SQUARE -> {checkSquare();}
                    case DRAW_SQUARE -> {drawSquare();}
                    case FREE_SQUARE -> {releaseSquare();}
                    case null -> {}
                }
            }
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
