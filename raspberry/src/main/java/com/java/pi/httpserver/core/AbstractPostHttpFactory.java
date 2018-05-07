package com.java.pi.httpserver.core;


import com.java.pi.httpserver.core.http.NanoHTTPD;

import java.util.Map;

public abstract class AbstractPostHttpFactory extends AbstractHttpBusiness {

    @Override
    protected String onPost(NanoHTTPD.IHTTPSession session) {
        return onMessageReceive(session);
    }

    @Override
    protected String onGet(String path, Map<String, String> param) {
        return null;
    }

    protected abstract String onMessageReceive(NanoHTTPD.IHTTPSession session);
}
