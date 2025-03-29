package com.denyandconquer.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.IOException;

public class GameServer {
    private String serverIP;
    private int port;
    private ServerSocket serverSocket;
    private Map<Integer, GameThread> threadMap;
    private int playerNumber = 1;
    private RoomManager roomManager;
    private InetAddress host;
    private volatile boolean running = true;
    public GameServer(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        this.roomManager = new RoomManager();
        this.threadMap = Collections.synchronizedMap(new HashMap<>());
    }
    public void startServer() {
        try {
//            serverSocket = new ServerSocket(0); // either fixed or dynamic
//            host = InetAddress.getLocalHost();
//            port = serverSocket.getLocalPort();
//            System.out.println("Server started at:");
//            System.out.println("Address: " + host.getHostAddress());
//            System.out.println("Port: " + port);
            serverSocket = new ServerSocket(port);
            running = true;

//            try (PrintWriter writer = new PrintWriter(new FileWriter("ports.txt"))) {
//                writer.println(port);
//            }

            while (running) {
                try {
                    Socket socket = serverSocket.accept(); // incoming sockets
                    if (!running) break;

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

    public void stopServer() {
        if (!running) {
            return;
        }
        running = false;
        synchronized (threadMap) {
            List<GameThread> players = new ArrayList<>(threadMap.values());
            for (GameThread player : players) {
                player.stopThread();
            }
            threadMap.clear(); // clear all game threads
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
