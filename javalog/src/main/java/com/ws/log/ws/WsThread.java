package com.ws.log.ws;

import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WsThread extends Thread {
    private BufferedReader reader;
    private WebSocket session;

    public WsThread(InputStream in, WebSocket session) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.session = session;

    }
    @Override
    public void run() {
        super.run();
        String line;
        try {
            while((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                session.send(line + "<br>");
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
