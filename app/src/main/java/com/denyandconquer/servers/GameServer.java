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
    private boolean isStarted = false;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isStarted = true;
            System.out.println("GameServer started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.put(clientSocket, handler);
                new Thread(handler).start();
            }
        } catch (SocketException se) {
            stop();
        }
        catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            isStarted = false;
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

    public boolean isStarted() {
        return isStarted;
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
                    try {
                        Object obj = in.readObject();
                        System.out.println("[Server]Raw object received: " + obj.getClass());
                        Message message = (Message) obj;
                        System.out.println("📩 Received: " + message.getType());
                        handleMessage(message);
                    } catch (ClassCastException cce) {
                        System.out.println("❌ ClassCastException: " + cce.getMessage());
                        cce.printStackTrace();
                    }
                }
            } catch (EOFException eofe) {
                System.out.println("🔴 Client disconnected (EOF) from: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                System.out.println("🔴 Client handler crashed: " + e.getMessage());
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
                        sendRoomPlayerList(currentRoom);
                        currentRoom = null;
                        broadcastRoomList();
                    }
                }

                case START_GAME -> {
                    String roomId = (String) message.getData();
                    System.out.println("▶️ START_GAME: " + roomId);
                    GameRoom room = lobbyManager.getRoom(roomId);
                    List <Player> players = room.getPlayerList();
                    for (Player player : players) {
                        player.resetScore();
                    }
                    if (room != null && room.equals(currentRoom)) {
                        room.startGame();
                        broadcastToRoom(room, new Message(MessageType.START_GAME, players));
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
                    if (data.getAction() == MouseAction.RELEASE) {
                        Player winner = currentRoom.getGameController().getWinner();
                        if (winner != null) {
                            broadcastToRoom(currentRoom, new Message(MessageType.GAME_OVER, winner));
                        }
                    }
                }

                case DISCONNECT -> disconnect();
            }
        }

        private void broadcastToAll(Message message) {
            for (ClientHandler handler : clientHandlers.values()) {
                handler.send(message);
            }
        }

        private void send(Message message) {
            try {
                if (message.getType() == MessageType.PLAYER_ROOM_LIST_UPDATE ||
                        message.getType() == MessageType.START_GAME) {
                    out.reset();
                }
                out.writeObject(message);
                out.flush();
                System.out.println("📤 Sent: " + message.getType());
            } catch (IOException e) {
                System.out.println("❌ [Server] Cannot send message: " + e.getMessage());
                e.printStackTrace();
                disconnect();
            }
        }

        private void sendRoomPlayerList(GameRoom room) {
            List<Player> players = room.getPlayerList();
            Message message = new Message(MessageType.PLAYER_ROOM_LIST_UPDATE, players);

            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.currentRoom != null && handler.currentRoom.equals(room)) {
                    handler.send(message);
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
                    handler.send(new Message(MessageType.PLAYER_SERVER_LIST_UPDATE, players));
                }
            }
        }

        private void sendRoomList(ClientHandler handler) {
            List<GameRoomDTO> dtoList = lobbyManager.getAllRooms()
                    .stream()
                    .map(GameRoomDTO::new)
                    .toList();
            handler.send(new Message(MessageType.ROOM_LIST_UPDATE, dtoList));
        }

        private void broadcastRoomList() {
            List<GameRoomDTO> dtoList = lobbyManager.getAllRooms()
                    .stream()
                    .map(GameRoomDTO::new)
                    .toList();

            Message roomListMessage = new Message(MessageType.ROOM_LIST_UPDATE, dtoList);

            for (ClientHandler handler : clientHandlers.values()) {
                handler.send(roomListMessage);
            }
        }


        private void broadcastToRoom(GameRoom room, Message message) {
            for (ClientHandler handler : clientHandlers.values()) {
                if (handler.currentRoom != null && handler.currentRoom.equals(room)) {
                    handler.send(message);
                }
            }
        }

        private void disconnect() {
            try {
                synchronized (clientHandlers) {
                    clientHandlers.remove(socket);
                }

                if (player != null) {
                    lobbyManager.unregisterPlayerName(player.getName());
                    broadCastServerPlayerList();
                }

                if (currentRoom != null) {
                    currentRoom.removePlayer(player);
                    broadcastRoomList();
                }

                if (socket != null && !socket.isClosed()) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();

                System.out.println("🔴 Client disconnected: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
