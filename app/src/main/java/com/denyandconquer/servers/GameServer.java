package com.denyandconquer.servers;

import com.denyandconquer.common.Player;
import com.denyandconquer.controllers.GameController;
import com.denyandconquer.common.Square;
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
    private GameController gameController;

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
                case PLAYER_NAME_SET_REQUEST -> {
                    String name = (String) message.getData();
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
                    System.out.println("Create Room Request Receive");
                    CreateRoomRequest req = (CreateRoomRequest) message.getData();
                    GameRoom room = lobbyManager.createRoom(req.getRoomName(), req.getMaxPlayers(), req.isPrivate(), req.getPassword());
                    if (room != null) {
                        currentRoom = room;
                        GameRoomDTO dtos = new GameRoomDTO(room);
                        send(new Message(MessageType.ROOM_CREATED, dtos));
                        broadcastRoomList();
                        System.out.println("Create Room Request Success");
                    } else {
                        send(new Message(MessageType.ROOM_CREATE_FAILED, null));
                    }
                }

                case JOIN_ROOM -> {
                    JoinRoomRequest req = (JoinRoomRequest) message.getData();
                    GameRoom room = lobbyManager.getRoom(req.getRoomId());
                    if (room != null && room.addPlayer(req.getPlayer())) {
                        currentRoom = room;
                        GameRoomDTO dtos = new GameRoomDTO(room);
                        send(new Message(MessageType.ROOM_JOIN_SUCCESS, dtos));
                        broadcastRoomList();
                        sendRoomPlayerList(room);
                    } else {
                        send(new Message(MessageType.ROOM_JOIN_FAILED, null));
                    }
                }

                case JOIN_ROOM_WITH_PASSWORD -> {
                    JoinRoomWithPasswordRequest req = (JoinRoomWithPasswordRequest) message.getData();
                    GameRoom room = lobbyManager.getRoom(req.getRoomId());
                    if (room != null && room.checkPassword(req.getPassword()) && room.addPlayer(req.getPlayer())) {
                        currentRoom = room;
                        send(new Message(MessageType.ROOM_JOIN_SUCCESS, new GameRoomDTO(room)));
                        broadcastRoomList();
                        sendRoomPlayerList(room);
                    } else {
                        send(new Message(MessageType.ROOM_JOIN_FAILED, null));
                    }
                }

                case LEAVE_ROOM -> {
                    if (currentRoom != null) {
                        currentRoom.removePlayer(player);
                        currentRoom = null;
                        broadcastRoomList();
                    }
                }

                case START_GAME -> {
                    String roomId = (String) message.getData();
                    GameRoom room = lobbyManager.getRoom(roomId);
                    if (room != null && room.equals(currentRoom)) {
                        room.startGame();
                        broadcastToRoom(room, new Message(MessageType.START_GAME, room.getPlayerList()));
                    }
                }

                case CLICK_ON_TILE -> {
                    if (currentRoom != null) {
                        TileClickData data = (TileClickData) message.getData();
                        currentRoom.getGameController().handlePress(player, data);

                        Square updatedSquare = currentRoom.getGameController().getBoard().getSquare(data.getBoardX(), data.getBoardY());
                        broadcastToRoom(currentRoom, new Message(MessageType.TILE_UPDATE, updatedSquare));
                    }
                }

                case DRAW_ON_TILE -> {
                    if (currentRoom != null) {
                        DrawData draw = (DrawData) message.getData();
                        GameController controller = currentRoom.getGameController();
                        controller.handleDraw(player, draw);

                        Square updatedSquare = controller.getBoard().getSquare(draw.getBoardX(), draw.getBoardY());
                        broadcastToRoom(currentRoom, new Message(MessageType.TILE_UPDATE, updatedSquare));
                    }
                }

                case RELEASE_TILE -> {
                    if (currentRoom != null) {
                        DrawData draw = (DrawData) message.getData();
                        GameController controller = currentRoom.getGameController();
                        controller.handleRelease(player, draw);

                        Square updatedSquare = controller.getBoard().getSquare(draw.getBoardX(), draw.getBoardY());
                        broadcastToRoom(currentRoom, new Message(MessageType.TILE_UPDATE, updatedSquare));
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
