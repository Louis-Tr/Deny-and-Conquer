package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;

public class GameClient {
    private String playerInfo;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private BufferedReader input;
    public void startClient(String serverAddress, int portNumber) {

        System.out.println("Connecting to " + serverAddress + " on port " + portNumber);

        try {
            socket = new Socket(serverAddress, portNumber);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            input = new BufferedReader(new InputStreamReader(System.in));

            playerInfo = br.readLine();
            System.out.println("You are " + playerInfo);

            // Thread to receive messages from the server
            InputThread inputThread = new InputThread(br);
            inputThread.start();

            // Send message to server
            String line = null;
            while ((line = input.readLine()) != null) {

                // End connection
                if ("quit".equals(line)) {
                    pw.println("quit");
                    break;
                }
                // Send message
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Connection error");
        } finally {

            try {
                input.close();
                br.close();
                pw.close();
                socket.close();
            } catch (Exception ex) {
                System.out.println("Closing resources error");
            }
        }
        System.out.println(playerInfo + " has disconnected.");
    }
}

class InputThread extends Thread {
    BufferedReader br;
    public InputThread(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        try {
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.out.println("...");
        }
    }
}