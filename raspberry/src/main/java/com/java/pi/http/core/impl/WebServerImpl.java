package com.java.pi.http.core.impl;

import com.java.pi.http.core.http.NanoHTTPD;
import com.java.pi.http.core.observer.IHttpObserver;
import com.java.pi.util.FileUtil;
import com.java.pi.util.Logc;
import com.java.pi.util.RaspberryConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebServerImpl implements IHttpObserver<NanoHTTPD.IHTTPSession> {
    @Override
    public NanoHTTPD.Response onHttpSession(NanoHTTPD.IHTTPSession data) {
        String uri = data.getUri();
        Logc.e("====WebServerImpl==== "+uri);
        return processFile(uri);
    }

    private NanoHTTPD.Response processFile(String uri) {
        String file_name = uri.substring(1);
        // 默认的页面名称设定为index.html
        if (file_name.equalsIgnoreCase("")) {
            file_name = "index.html";
        }else{
            try {
                file_name = file_name.replaceAll("/", "\\\\");
            }catch (Exception e){}
        }
        String path = RaspberryConst.HTTP.FILE_PATH + file_name;
        byte[] data = null;
        try {
            data = FileUtil.loadContent(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data == null){
            return null;
        }
        return new NanoHTTPD.Response(new String(data));
    }

    private String processPostData(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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
}
