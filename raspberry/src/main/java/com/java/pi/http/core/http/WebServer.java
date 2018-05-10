package com.java.pi.http.core.http;

import com.java.pi.util.FileUtil;
import com.java.pi.util.Logc;

import java.io.File;
import java.io.IOException;

public class WebServer extends NanoHTTPD {
    public WebServer(int port) {
        super(port);
    }

    public WebServer(String hostname, int port) {
        super(hostname, port);
    }

    public WebServer() {
        // 端口是8088，也就是说要通过http://127.0.0.1:8088来访当问
        super(8088);
    }


    @Override
    public void start() throws IOException {
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
            + "raspberry" + File.separator
            + "web" + File.separator;

    @Override
    public Response serve(IHTTPSession session) {
        return processFile(session.getUri());
    }

    /*public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {

        // 将读取到的文件内容返回给浏览器
        return processFile(uri);
    }*/

    private Response processFile(String uri) {
        String file_name = uri.substring(1);
        Logc.e(file_name);
        // 默认的页面名称设定为index.html
        if (file_name.equalsIgnoreCase("")) {
            file_name = "index.html";
        }else{
            try {
                file_name = file_name.replaceAll("/", "\\\\");
            }catch (Exception e){}
        }
        String path = FILE_PATH + file_name;
        byte[] data = null;
        try {
            data = FileUtil.loadContent(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = new String(data==null?"error":new String(data));
        return new NanoHTTPD.Response(result);
    }
}