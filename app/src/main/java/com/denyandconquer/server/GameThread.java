package com.denyandconquer.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GameThread extends Thread {
    private String name;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    List<GameThread> list;

    public GameThread(Socket socket, List<GameThread> list) throws Exception {
        this.socket = socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = br;
        this.pw = pw;
        this.name = br.readLine();
        this.list = list;
        this.list.add(this);

    }

    public void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
    }

    @Override
    public void run() {
        // broadcast

        // 접속하면 나를 제외한 모든 사용자에게 "00님이 연결되었습니다."
        try {
            broadcast(name + "님이 연결되었습니다.", false);
            String line = null;

//            while ((line = br.readLine()) != null) {
//                            // 나를 포함한 ChatThread에게 메시지를 보낸다.
//                            broadcast(name + " : " + line, true);
//
//            }
        } catch (Exception e) {
            broadcast(name + "님이 연결이 끊어졌습니다.", false);
            this.list.remove(this);
        }
    }

    private void broadcast(String msg, boolean includeMe) {
        List<GameThread> chatThreads = new ArrayList<>();
        for(int i=0; i < this.list.size(); i++){
            chatThreads.add(list.get(i));
        }

        try {
            for (int i = 0; i < chatThreads.size(); i++){
                GameThread ct = chatThreads.get(i);
                if (!includeMe) { //나를 포함하지 말아라.
                    if (ct == this){
                        continue;   // 다시 위로
                    }
                }
                ct.sendMessage(msg);
            }
        }catch (Exception ex){
            System.out.println("///");
        }
    }
}
