package com.tech.tcp.core.tcp;

import com.tech.tcp.core.callback.OnDataRecv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private OnDataRecv onDataRecv;

    public void startServer(int port, OnDataRecv onDataRecv) {
        this.onDataRecv = onDataRecv;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            while ((socket = serverSocket.accept()) != null) {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                getMessageFromClient();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void getMessageFromClient() {
        //获取消息的长度
        int length;
        try {
            while (true) {
                length = dataInputStream.read();
                //获取消息
                byte[] body = new byte[length];
                dataInputStream.read(body);
                String message = new String(body, "utf-8");
                System.out.println(message);
                if (onDataRecv != null) {
                    onDataRecv.messageReceived(body);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) {
        try {
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TcpServer server = new TcpServer();
        server.startServer(8888, new OnDataRecv() {
            @Override
            public void onConnected() {

            }

            @Override
            public void messageReceived(byte[] iData) {

            }
        });
    }
}
