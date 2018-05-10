package com.java.pi.http.file;


import com.java.pi.http.core.http.NanoHTTPD;

public class HttpFileManagerFactory implements NanoHTTPD.TempFileManagerFactory {
    @Override
    public NanoHTTPD.TempFileManager create() {
        return new HttpFileManager();
    }
}
