package com.java.pi.http;


import com.java.pi.http.core.HttpServerManager;
import com.java.pi.http.core.impl.PiImpl;
import com.java.pi.http.core.impl.WebServerImpl;

import java.io.IOException;

public class Server {
    public static void start(int port) throws IOException {
//        HttpServerManager.getInstance().registerObserver(new EMQTTImpl());
//        HttpServerManager.getInstance().registerObserver(new LocalInfoImpl());
        HttpServerManager.getInstance().registerObserver(new PiImpl());
        HttpServerManager.getInstance().registerObserver(new WebServerImpl());
        HttpServerManager.getInstance().startHttpServer(port);
    }

    public static void destroyHttpServer(){
        HttpServerManager.getInstance().destroy();
    }
}
