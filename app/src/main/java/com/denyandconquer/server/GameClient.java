package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;

public class GameClient {
    public static void main(String[] args) throws Exception {

//        if (args.length != 1) {
//            System.out.println("Usage: java GameClient 'nickname'");
//            return;
//        }
//        String name = args[0];

        int portNumber;
        try(BufferedReader reader = new BufferedReader(new FileReader("ports.txt"))) {
            portNumber = Integer.parseInt(reader.readLine());
        }
        System.out.println("Connecting to " + portNumber);

        Socket socket = new Socket("localhost", portNumber);

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // 닉네임 전송
        pw.println("mickey17");
        pw.flush();

        // 백그라운드로 서버가 보내준 메시지를 읽어들여서 화면에 출력한다.
        InputThread inputThread = new InputThread(br);
        inputThread.start();

        // 클라이언트는 읽어들인 메시지를 서버에게 전송한다.
        try {
            String line = null;
            while((line = input.readLine()) != null) {

                // 종료
                if ("/quit".equals(line)){
                    pw.println("/quit");
                    pw.flush();
                    break;
                }
                // 전송
                pw.println(line);
                pw.flush();
            }
        } catch (Exception ex) {
            System.out.println("...");
        }

        try {
            br.close();
        }catch (Exception ex) {
            System.out.println("111");
        }

        try {
            pw.close();
        }catch (Exception ex) {
            System.out.println("222");
        }

        // 연결 종료
        try {
            socket.close();
            System.out.println("socket close!!");
        }catch (Exception ex) {
            System.out.println("333");
        }
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