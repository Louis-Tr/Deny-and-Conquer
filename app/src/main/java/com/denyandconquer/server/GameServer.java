package com.denyandconquer.server;

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
    private int assignedPort;
    public void startServer() {
        try {
            serverSocket = new ServerSocket(0); // either fixed or dynamic
            assignedPort = serverSocket.getLocalPort();
            System.out.println("Server started at port " + assignedPort);

            try (PrintWriter writer = new PrintWriter(new FileWriter("ports.txt"))) {
                writer.println(assignedPort);
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
