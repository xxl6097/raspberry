package com.java.pi.http.core.http;

import com.java.pi.util.FileUtil;
import com.java.pi.util.Logc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class WebServers extends BaseServer {
    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
            + "raspberry" + File.separator
            + "web" + File.separator;

    public WebServers(int port) {
        super(port);
    }


    @Override
    protected void onHandle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;
            int type = 0;
            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                Logc.i("#@@@@@@@@@@@@### " + line);
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                } else if (line.startsWith("POST /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    String tag = line.substring(start, end);
                    if (tag.endsWith("request")) {
                        type = 3;
                    }
                    route = processPostData(reader);
                }


                if (reader.ready()) {
                    line = reader.readLine();
                } else {
                    break;
                }
            }
            Logc.i("#@##Client ## " + route);
            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());
            final byte[] bytes;

            if (route == null) {
                type = -1;
            } else if (route.startsWith("html")) {
                type = 2;
            } else if (route.startsWith("js")) {
                type = 1;
            } else if (route.startsWith("css")) {
                type = 4;
            } else if (route.startsWith("img")) {
                type = 5;
            }

            if (type == 1) {
                bytes = FileUtil.loadContent(FILE_PATH+route);
            } else if (type == 2) {
                bytes = FileUtil.loadContent(FILE_PATH+route);
            } else if (type == 4) {
                bytes = FileUtil.loadContent(FILE_PATH+route);
            } else if (type == 5) {
                bytes = FileUtil.loadContent(FILE_PATH+route);
            } else if (type == 3) {
                bytes = processData(FILE_PATH+route);
            } else if (type == -1) {
                bytes = null;
            } else {
                bytes = FileUtil.loadContent(FILE_PATH+"index.html");
            }

            if (null == bytes) {
                writeServerError(output);
                return;
            }
            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + FileUtil.detectMimeType(FILE_PATH+route));
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String processPostData(BufferedReader reader) throws IOException {
        //post提交方式的数据长度
        int contentLength = 0;
        //下面行出问题了， 如果不使用前面new的reader，用下面方式新开的reader读不到数据。
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
            if ("".equals(line)) {
                break;
            } else if (line.indexOf("Content-Length") != -1) {
                contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
            }
        }
        String data = null;
        //继续读取普通post（没有附件）提交的数据
        Logc.i("begin read posted data......");
        if (contentLength != 0) {
            char[] buf = new char[contentLength];
            reader.read(buf, 0, contentLength);
            data = new String(buf);
            Logc.i("The data user posted: " + data);
        }
        return data;
    }

    private byte[] processData(String data) {
        return null;
    }

    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }

}
