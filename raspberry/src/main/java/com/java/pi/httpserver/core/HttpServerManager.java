package com.java.pi.httpserver.core;


import com.java.pi.httpserver.core.http.HttpServer;
import com.java.pi.httpserver.core.http.NanoHTTPD;
import com.java.pi.httpserver.core.impl.UnknowImpl;
import com.java.pi.httpserver.core.observer.IHttpObserver;
import com.java.pi.httpserver.file.HttpFileManagerFactory;
import com.java.pi.util.Logc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HttpServerManager {

    private static Set<IHttpObserver> obs = new HashSet<>();
    private static HttpServerManager INSTANCE = null;
    private HttpServer httpServer;

    public static HttpServerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpServerManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new HttpServerManager();
                }
            }
        }
        return INSTANCE;
    }

    public void startHttpServer() throws IOException {
        if (httpServer == null) {
            httpServer = new HttpServer();
            httpServer.start();
        }
    }

    public void startHttpServer(int port) throws IOException {
        if (httpServer == null) {
            httpServer = new HttpServer(port);
            httpServer.start();
            Logc.d("=================================HttpServerManager.startHttpServer.port"+port);
        }
    }

    public void startHttpFileServer(int port) throws IOException {
        startHttpServer(port);
        httpServer.setTempFileManagerFactory(new HttpFileManagerFactory());
    }

    public void startHttpServer(String hostname, int port) throws IOException {
        if (httpServer == null) {
            httpServer = new HttpServer(hostname, port);
            httpServer.start();
        }
    }

    public void stopHttpServer() {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
    }

    public synchronized void registerObserver(IHttpObserver o) {
        if (o != null) {
            if (!obs.contains(o)) {
                obs.add(o);
            }
        }
    }

    public synchronized void unregisterObserver(IHttpObserver o) {
        if (obs.contains(o)) {
            obs.remove(o);
        }
    }

    public synchronized void destroy() {
        obs.clear();
        stopHttpServer();
    }

    public synchronized String onResponse(NanoHTTPD.IHTTPSession session) {
        String response = null;
        if (session == null)
            return null;
        Iterator<IHttpObserver> it = obs.iterator();
        while (it.hasNext()) {
            IHttpObserver mgr = it.next();
            response = mgr.onHttpSession(session);
            if (response != null)
                break;
        }
        if (response == null) {
            response = new UnknowImpl().onHttpSession(session);
        }
        return response;
    }


}
