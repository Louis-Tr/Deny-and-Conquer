package com.denyandconquer.servers;

import com.denyandconquer.common.GameRoom;
import com.denyandconquer.common.Player;
import com.denyandconquer.net.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private final int port;
    private final LobbyManager lobbyManager = new LobbyManager();
    private final Map<Socket, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("GameServer started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket);
            clientHandlers.put(clientSocket, handler);
            new Thread(handler).start();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("🔴 Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Player player;
        private GameRoom currentRoom;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("🟢 Client connected: " + socket.getRemoteSocketAddress());

                while (true) {
                    Message message = (Message) in.readObject();
                    System.out.println("📩 Received: " + message.getType());
                    handleMessage(message);
                }

            } catch (Exception e) {
                System.out.println("🔴 Client handler crashed: " + e.getMessage());
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }

        private void handleMessage(Message message) throws IOException {
            switch (message.getType()) {
                case JOIN_SERVER -> {
                    String name = (String) message.getData();
                    System.out.println("🔐 JOIN_SERVER: " + name);
                    if (lobbyManager.registerPlayerName(name)) {
                        player = new Player(name);
                        send(new Message(MessageType.PLAYER_NAME_ACCEPTED, player));
                        broadCastServerPlayerList();
                        sendRoomList(this);
                    } else {
                        send(new Message(MessageType.PLAYER_NAME_REJECTED, null));
                    }
                }

                case CREATE_ROOM_REQUEST -> {
                    NavigationRequest req = (NavigationRequest) message.getData();
                    System.out.println("📦 CREATE_ROOM_REQUEST: " + req.getRoomName());

                    if (lobbyManager.getRoomByName(req.getRoomName()) != null) {
                        System.out.println("⚠️ Room name already exists: " + req.getRoomName());
                        send(new Message(MessageType.ROOM_CREATE_FAILED, "Room name already exists"));
                        return;
                    }

                    boolean isPrivate = req.hasPassword();
                    GameRoom room = lobbyManager.createRoom(req.getRoomName(), req.getMaxPlayers(), isPrivate, req.getPassword());
                    room.addPlayer(player);
                    if (room != null) {
                        currentRoom = room;
                        send(new Message(MessageType.ROOM_CREATED, new GameRoomDTO(room)));
                        broadcastRoomList();
                        System.out.println("✅ Room created: " + room.getRoomName());
                    } else {
                        send(new Message(MessageType.ROOM_CREATE_FAILED, "Unknown error"));
                    }
                }

                case JOIN_ROOM -> {
                    NavigationRequest req = (NavigationRequest) message.getData();
                    System.out.println("➡️ JOIN_ROOM: " + req.getRoomID());
                    GameRoom room = lobbyManager.getRoom(req.getRoomID());

                    if (room != null) {
                        boolean correctPassword = !room.isPrivate() || room.checkPassword(req.getPassword());
                        if (correctPassword && room.addPlayer(player)) {
                            currentRoom = room;
                            send(new Message(MessageType.ROOM_JOIN_SUCCESS, new GameRoomDTO(room)));
                            broadcastRoomList();
                            sendRoomPlayerList(room);
                            System.out.println("✅ Joined room: " + room.getRoomName());
                        } else {
                            send(new Message(MessageType.ROOM_JOIN_FAILED, null));
                            System.out.println("❌ Failed to join room: " + room.getRoomName());
                        }
                    } else {
                        send(new Message(MessageType.ROOM_JOIN_FAILED, null));
                        System.out.println("❌ Room not found: " + req.getRoomID());
                    }
                }

                case LEAVE_ROOM -> {
                    if (currentRoom != null) {
                        System.out.println("🚪 LEAVE_ROOM: " + currentRoom.getRoomName());
                        currentRoom.removePlayer(player);
                        currentRoom = null;
                        broadcastRoomList();
                    }
                }

                case START_GAME -> {
                    String roomId = (String) message.getData();
                    System.out.println("▶️ START_GAME: " + roomId);
                    GameRoom room = lobbyManager.getRoom(roomId);
                    if (room != null && room.equals(currentRoom)) {
                        room.startGame();
                        broadcastToRoom(room, new Message(MessageType.START_GAME, room.getPlayerList()));
                    }
                }

                case MOUSE_ACTION -> {
                    MouseData data = (MouseData) message.getData();
                    boolean changed = currentRoom.getGameController().handleMouseAction(player, data);
                    System.out.println("🖱️ MOUSE_ACTION by " + player.getName() + ": " + data.getAction());
                    if (changed) {
                        data.setPlayer(player);
                        broadcastToRoom(currentRoom, new Message(MessageType.MOUSE_ACTION, data));
                    }
                }

                case DISCONNECT -> disconnect();
            }
        }

        private void broadcastToAll(Message message) {
            for (ClientHandler handler : clientHandlers.values()) {
                try {
                    handler.send(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void send(Message message) throws IOException {
            out.writeObject(message);
            out.flush();
            System.out.println("📤 Sent: " + message.getType());
        }

        private void sendRoomPlayerList(GameRoom room) {
            List<Player> players = room.getPlayerList();
            Message message = new Message(MessageType.PLAYER_ROOM_LIST_UPDATE, players);

            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.currentRoom != null && handler.currentRoom.equals(room)) {
                    try {
                        handler.send(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void broadCastServerPlayerList() {
            List<Player> players = new ArrayList<>();
            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.player != null) {
                    players.add(handler.player);
                }
            }

            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.player != null) {
                    try {
                        handler.send(new Message(MessageType.PLAYER_SERVER_LIST_UPDATE, players));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void sendRoomList(ClientHandler handler) {
            try {
                List<GameRoomDTO> dtoList = lobbyManager.getAllRooms()
                        .stream()
                        .map(GameRoomDTO::new)
                        .toList();
                handler.send(new Message(MessageType.ROOM_LIST_UPDATE, dtoList));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcastRoomList() {
            List<GameRoomDTO> dtoList = lobbyManager.getAllRooms()
                    .stream()
                    .map(GameRoomDTO::new)
                    .toList();

            Message roomListMessage = new Message(MessageType.ROOM_LIST_UPDATE, dtoList);

            for (ClientHandler handler : clientHandlers.values()) {
                try {
                    handler.send(roomListMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void broadcastToRoom(GameRoom room, Message message) {
            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.currentRoom != null && handler.currentRoom.equals(room)) {
                    try {
                        handler.send(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void disconnect() {
            try {
                if (player != null) {
                    lobbyManager.unregisterPlayerName(player.getName());
                    broadCastServerPlayerList();
                }

                if (currentRoom != null) {
                    currentRoom.removePlayer(player);
                    broadcastRoomList();
                }

                clientHandlers.remove(socket);
                socket.close();
                System.out.println("🔴 Client disconnected: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
