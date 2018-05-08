package com.het;

import com.baidu.translate.demo.TransApi;
import com.het.httpserver.Server;
import com.het.httpserver.http.SimpleHttpUtils;
import com.het.httpserver.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        try {
            Server.start(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        uploadFile();

//        String html = null;
//        try {
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("username","admin");
//            headers.put("password","public");
//            html = SimpleHttpUtils.get("http://uuxia.cn:8080/api/v2/nodes/emq@127.0.0.1/clients",headers);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(html);

//        say();

//        test();

        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
    }

    static void test() {
        String ret = Util.locateCityName("45.116.232.9");
        System.out.println(ret);
    }

    static void uploadFile() {
        Map<String, String> headers = new HashMap<>();
        String tmpdir = System.getProperty("user.dir") + File.separator + "file" + File.separator;
        headers.put("mqtt-clientid", "uuxia-2018");
        List<File> files = Util.getTxTFileList(tmpdir);

        try {
            for (File file : files) {
                String ret = SimpleHttpUtils.post("http://uuxia.cn:8421/v1/api/file", headers, file);
                System.out.println(file.getAbsolutePath() +" ret "+ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Exception "+e.getMessage());
        }
    }

    public Main() {

    }

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20180418000147840";
    private static final String SECURITY_KEY = "3lNTR8d9bfq4giBZcH2P";

    public static void say() {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = "高度600米";
        System.out.println(api.getTransResult(query, "auto", "en"));
    }
}
