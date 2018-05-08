package com.het.httpserver.file;

import com.het.httpserver.core.http.NanoHTTPD;

public class HttpFileManagerFactory implements NanoHTTPD.TempFileManagerFactory {
    @Override
    public NanoHTTPD.TempFileManager create() {
        return new HttpFileManager();
    }
}
