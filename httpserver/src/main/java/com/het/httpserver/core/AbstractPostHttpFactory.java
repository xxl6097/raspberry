package com.het.httpserver.core;

import com.het.httpserver.core.http.NanoHTTPD;

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
