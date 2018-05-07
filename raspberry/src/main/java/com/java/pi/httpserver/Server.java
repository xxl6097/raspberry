package com.java.pi.httpserver;


import com.java.pi.httpserver.core.HttpServerManager;
import com.java.pi.httpserver.core.impl.EMQTTImpl;
import com.java.pi.httpserver.core.impl.LocalInfoImpl;
import com.java.pi.httpserver.core.impl.PiImpl;

import java.io.IOException;

public class Server {
    public static void start(int port) throws IOException {
//        HttpServerManager.getInstance().registerObserver(new EMQTTImpl());
//        HttpServerManager.getInstance().registerObserver(new LocalInfoImpl());
        HttpServerManager.getInstance().registerObserver(new PiImpl());
        HttpServerManager.getInstance().startHttpServer(port);
    }

    public static void destroyHttpServer(){
        HttpServerManager.getInstance().destroy();
    }
}
