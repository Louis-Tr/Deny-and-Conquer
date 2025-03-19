package com.denyandconquer.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
public class GameServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(0); // either fixed or dynamic
        int assignedPort = serverSocket.getLocalPort();
        System.out.println("Server started at port " + assignedPort);

        try (PrintWriter writer = new PrintWriter(new FileWriter("ports.txt"))) {
            writer.println(assignedPort);
        }

        List<GameThread> list = Collections.synchronizedList(new ArrayList<>());

        while (true) {
            Socket socket = serverSocket.accept(); // incoming sockets
            GameThread gameThread = new GameThread(socket, list);
            gameThread.start();
        }

    }
}
