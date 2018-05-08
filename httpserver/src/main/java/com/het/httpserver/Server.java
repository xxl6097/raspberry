package com.het.httpserver;

import com.het.httpserver.core.HttpServerManager;
import com.het.httpserver.core.impl.EMQTTImpl;
import com.het.httpserver.core.impl.FanyiImpl;
import com.het.httpserver.core.impl.FileBrowserImpl;
import com.het.httpserver.core.impl.HttpFileImpl;
import com.het.httpserver.core.impl.LocalInfoImpl;

import java.io.IOException;

public class Server {
    public static void start(int port) throws IOException {
        HttpServerManager.getInstance().registerObserver(new EMQTTImpl());
        HttpServerManager.getInstance().registerObserver(new FanyiImpl());
        HttpServerManager.getInstance().registerObserver(new LocalInfoImpl());
        HttpServerManager.getInstance().registerObserver(new HttpFileImpl());
        HttpServerManager.getInstance().registerObserver(new FileBrowserImpl());
        HttpServerManager.getInstance().startHttpServer(port);
    }

    public static void destroyHttpServer(){
        HttpServerManager.getInstance().destroy();
    }
}
