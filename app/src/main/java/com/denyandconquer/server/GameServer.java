package com.denyandconquer.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.IOException;

/**
 * The GameServer class connects with incoming client,
 * and creating new game threads for each connected client.
 */
public class GameServer {
    private String serverIP;
    private int port;
    private ServerSocket serverSocket;
    private Map<Integer, GameThread> threadMap;
    private int playerNumber = 1;
    private RoomManager roomManager;
    private volatile boolean running = true;

    /**
     * Initializes the GameServer with provided IP and port,
     * RoomManager and threadMap for managing rooms and game threads.
     * @param serverIP
     * @param port
     */
    public GameServer(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        this.roomManager = new RoomManager();
        this.threadMap = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Starts the server and listens for incoming client connections.
     * For each client, it starts a new GameThread to handle the client communication.
     */
    public void startServer() {
        try {
            // Init ServerSocket
            serverSocket = new ServerSocket(port);
            running = true;

            while (running) {
                try {
                    // Accept a new client connection
                    Socket socket = serverSocket.accept();
                    if (!running) break;

                    // Create a new game thread to handle the client
                    GameThread gameThread = new GameThread(socket, playerNumber, threadMap, roomManager);
                    gameThread.start();
                    playerNumber++;
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server error");
        } finally {
            stopServer();
        }
    }

    /**
     * Stops the server by stopping all game threads,
     * and closing the server socket.
     */
    public void stopServer() {
        if (!running) {
            return;
        }
        running = false;

        // Clear all game threads
        synchronized (threadMap) {
            List<GameThread> players = new ArrayList<>(threadMap.values());
            for (GameThread player : players) {
                player.stopThread();
            }
            threadMap.clear();
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped");
    }
}
