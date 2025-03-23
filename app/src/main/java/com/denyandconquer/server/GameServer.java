package com.denyandconquer.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
public class GameServer {
    private ServerSocket serverSocket;
    private List<GameThread> playerList = Collections.synchronizedList(new ArrayList<>());
    private RoomManager roomManager;
    private InetAddress host;
    private int port;
    private volatile boolean running = true;
    public GameServer() {
        this.roomManager = new RoomManager();
        this.playerList = Collections.synchronizedList(new ArrayList<>());
    }
    public void startServer() {
        try {
            serverSocket = new ServerSocket(0); // either fixed or dynamic
            host = InetAddress.getLocalHost();
            port = serverSocket.getLocalPort();
            System.out.println("Server started at:");
            System.out.println("Address: " + host.getHostAddress());
            System.out.println("Port: " + port);
            running = true;

            try (PrintWriter writer = new PrintWriter(new FileWriter("ports.txt"))) {
                writer.println(port);
            }

            while (running) {
                try {
                    Socket socket = serverSocket.accept(); // incoming sockets
                    if (!running) break;

                    GameThread gameThread = new GameThread(socket, playerList, roomManager);
                    gameThread.start();
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

    public void stopServer() {
        if (!running) {
            return;
        }
        running = false;
        synchronized (playerList) {
            for (GameThread player : new ArrayList<>(playerList)) { // Copy to avoid modification errors
                player.stopThread();
                playerList.remove(player);
            }
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
