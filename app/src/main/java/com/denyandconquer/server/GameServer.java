package com.denyandconquer.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
public class GameServer {
    private static final int MAX_PLAYERS = 4;
    private ServerSocket serverSocket;
    private List<GameThread> list = Collections.synchronizedList(new ArrayList<>());
    private InetAddress host;
    private int port;
    private boolean running = false;
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
                    GameThread gameThread = new GameThread(socket, list);
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
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Server stopped");
            } catch (IOException e) {
                System.out.println("Error Server closing");
            }
        }
    }
}
