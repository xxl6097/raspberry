package com.java.pi.httpserver.file;


import com.java.pi.httpserver.core.http.NanoHTTPD;

public class HttpFileManagerFactory implements NanoHTTPD.TempFileManagerFactory {
    @Override
    public NanoHTTPD.TempFileManager create() {
        return new HttpFileManager();
    }
}
