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
    public void startServer() {
        try {
            serverSocket = new ServerSocket(0); // either fixed or dynamic
            host = InetAddress.getLocalHost();
            port = serverSocket.getLocalPort();
            System.out.println("Server started at:");
            System.out.println("Address: " + host.getHostAddress());
            System.out.println("Port: " + port);

            try (PrintWriter writer = new PrintWriter(new FileWriter("ports.txt"))) {
                writer.println(port);
            }

            while (list.size() < MAX_PLAYERS) {
                Socket socket = serverSocket.accept(); // incoming sockets
                GameThread gameThread = new GameThread(socket, list);
                gameThread.start();
            }
            System.out.println("Game full.");

        } catch (IOException e) {
            System.out.println("Server error");
        }


    }
}
