package com.java.pi.http.core.http;

import com.java.pi.util.Logc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public abstract class BaseServer extends Thread {
    private int port;
    private boolean mIsRunning;
    private ServerSocket mServerSocket;

    public BaseServer(int port) {
        this.port = port;
        mIsRunning = true;
    }

    @Override
    public void run() {
        createSocketServer();
    }

    protected void createSocketServer() {
        try {
            mServerSocket = new ServerSocket(port);
            Logc.i("####mqtt ServerSocket.port:" + port);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                onHandle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Logc.e("####mqtt Web server error.", e);
        } catch (Exception ignore) {
        }
    }

    protected void release(){
        mIsRunning = false;
    }

    protected abstract void onHandle(Socket socket)throws IOException;
}
