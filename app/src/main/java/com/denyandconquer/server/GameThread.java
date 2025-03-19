package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GameThread extends Thread {
    private int playerNumber;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    List<GameThread> list;

    public GameThread(Socket socket, List<GameThread> list) {
        this.socket = socket;
        this.list = list;

        try {
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.list.add(this);
            this.playerNumber = list.size();
            sendMessage("Player " + playerNumber);
        } catch (IOException e) {
            System.out.println("Client thread error");
        }

    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    @Override
    public void run() {
        // broadcast

        try {
            broadcast("Player " + playerNumber + " has joined the game.", false);
            String line = null;

            while ((line = br.readLine()) != null) {
                // Send message including this client
                broadcast("Player " + playerNumber + ": " + line, true);

            }
        } catch (Exception e) {
            broadcast("Player " + playerNumber + " has disconnected.", false);
            this.list.remove(this);
            try {
                br.close();
                pw.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private void broadcast(String msg, boolean includeMe) {
        for (GameThread gt: list) {
            if (!includeMe && (gt == this)) {
                continue;
            }
            gt.sendMessage(msg);
        }
    }
}
