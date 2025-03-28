package com.denyandconquer.server;

import com.denyandconquer.screens.GameRoomScene;
import com.denyandconquer.screens.Launcher;
import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class GameClient extends Thread {
    Launcher launcher;
    private Player player;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public GameClient(String serverAddress, int port, Launcher launcher) {
        this.launcher = launcher;
        try {
            this.socket = new Socket(serverAddress, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
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

    private void handleTextMessage(String message) {
        System.out.println(message);
    }

    private void handleGameMessage(Message message) {
        switch (message.getType()) {
            case ENTER_ROOM:
                System.out.println("Room entered");
                Room room = (Room) message.getData();
                GameRoomScene gameRoomScene = new GameRoomScene(launcher, room);
                launcher.setGameRoomScene(gameRoomScene);
                launcher.setScene(gameRoomScene.getRoomScene());
                launcher.updatePlayerList(room.getPlayerList());
                break;
            case LEAVE_ROOM:
                System.out.println("Left the room");
                break;
            case ROOM_LIST:
                System.out.println("Refresh room list");
                List<Room> roomList = (List<Room>) message.getData();
                launcher.updateRoomList(roomList);
                break;
            case PLAYER_LIST:
                System.out.println("Refresh player list");
                List<Player> playerList = (List<Player>) message.getData();
                launcher.updatePlayerList(playerList);
                break;
            case START_GAME:
                System.out.println("Game started");
                break;
            default:
                System.out.println("Unknown message");
                break;
        }
        // handle UI ex room update
    }

    public void send(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
    public void sendCreateRoomRequest(String roomName, int maxPlayers) {
        Message msg = new Message(Message.Type.CREATE_ROOM, roomName, player, maxPlayers);
        send(msg);
    }
    public void sendJoinRoomRequest(int roomId) {
        Message msg = new Message(Message.Type.JOIN_ROOM, roomId, player);
        send(msg);
    }
    public void sendLeaveRoomRequest(){
        Message msg = new Message(Message.Type.LEAVE_ROOM, null, player);
        send(msg);
    }
    public void sendRoomListRequest(){
        Message msg = new Message(Message.Type.ROOM_LIST, null);
        send(msg);
    }
    public void sendStartGameRequest() {
        Message msg = new Message(Message.Type.START_GAME, null, player);
        send(msg);
    }
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Cleanup error for " + player.getName() + ": " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[Client] Client disconnected.");
            }
        } catch (IOException e) {
            System.out.println("[Client] Cannot disconnect.");
        }
    }
}