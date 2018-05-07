package com.java.pi.httpserver.core;


import com.java.pi.httpserver.core.http.NanoHTTPD;
import com.java.pi.httpserver.core.observer.IHttpObserver;

import java.util.Map;

public abstract class AbstractHttpBusiness implements IHttpObserver<NanoHTTPD.IHTTPSession> {
    @Override
    public String onHttpSession(NanoHTTPD.IHTTPSession session) {
        if (session.getMethod().name().equalsIgnoreCase("POST")) {
            return onPost(session);
        }else{
            if (session == null)
                return null;
            String path = session.getUri();
            Map<String, String> param = session.getParms();
            if (path == null)
                return "path is null";
            if (param == null)
                return "param is null";
            return onGet(path, param);
        }
    }

    protected abstract String onPost(NanoHTTPD.IHTTPSession session);
    protected abstract String onGet(String path, Map<String, String> param);
}
