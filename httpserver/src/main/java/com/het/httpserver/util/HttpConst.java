package com.het.httpserver.util;

import java.io.File;

public class HttpConst {
    public final static String HOST = "localhost";
    //httpserver端口
    public final static int LOCAL_HTTP_SERVER_PORT = 8888;

    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator + "file"+ File.separator;
}
