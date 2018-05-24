package com.ws.log;

import com.ws.log.ws.WsServer;

import org.java_websocket.WebSocketImpl;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;

public class Main {
    private static String uri = "http://192.168.1.105:8080/";
    private static Session session;

    private void start() {
        WebSocketContainer container = null;
        try {
            container = ContainerProvider.getWebSocketContainer();
        } catch (Exception ex) {
            System.out.println("error" + ex);
        }

        try {
            URI r = URI.create(uri);
            session = container.connectToServer(LogWebSocketHandle.class, r);
        } catch (DeploymentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        Main client = new Main();
//        client.start();
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String input = "";
//        try {
//            do {
//                input = br.readLine();
//                if (!input.equals("exit"))
//                    client.session.getBasicRemote().sendText(input);
//            } while (!input.equals("exit"));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }



        WebSocketImpl.DEBUG = false;
        int port = 8080; // 端口
        WsServer s = new WsServer(port);
        s.start();
    }
}
