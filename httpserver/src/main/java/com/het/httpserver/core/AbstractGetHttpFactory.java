package com.het.httpserver.core;

import com.het.httpserver.core.http.NanoHTTPD;

import java.util.Map;

public abstract class AbstractGetHttpFactory extends AbstractHttpBusiness {
    @Override
    protected String onGet(String path, Map<String, String> param) {
        return onMessageReceive(path,param);
    }

    @Override
    protected String onPost(NanoHTTPD.IHTTPSession session) {
        return null;
    }

    protected abstract String onMessageReceive(String path, Map<String, String> param);
}
