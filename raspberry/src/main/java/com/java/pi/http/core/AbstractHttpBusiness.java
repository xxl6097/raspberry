package com.java.pi.http.core;


import com.java.pi.http.core.http.NanoHTTPD;
import com.java.pi.http.core.observer.IHttpObserver;

import java.util.Map;

public abstract class AbstractHttpBusiness implements IHttpObserver<NanoHTTPD.IHTTPSession> {
    @Override
    public NanoHTTPD.Response onHttpSession(NanoHTTPD.IHTTPSession session) {
        if (session.getMethod().name().equalsIgnoreCase("POST")) {
            String resule = onPost(session);
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/plain; charset=UTF-8",resule);
        }else{
            if (session == null)
                return null;
            String path = session.getUri();
            Map<String, String> param = session.getParms();
            if (path == null)
                return null;
            if (param == null)
                return null;
            String resule = onGet(path, param);
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/plain; charset=UTF-8",resule);

        }
    }

    protected abstract String onPost(NanoHTTPD.IHTTPSession session);
    protected abstract String onGet(String path, Map<String, String> param);
}
