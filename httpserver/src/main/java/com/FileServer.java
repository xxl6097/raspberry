package com;

import com.het.httpserver.http.SimpleHttpUtils;
import com.het.httpserver.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileServer
{
    public static void main(String[] args) {
        //uploadFile();
        try {
            Runtime.getRuntime().exec("reboot");//注销
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static void uploadFile() {
        Map<String, String> headers = new HashMap<>();
        String tmpdir = System.getProperty("user.dir") + File.separator + "files" + File.separator;
        headers.put("mqtt-clientid", "uuxia-2018");
        List<File> files = Util.getTxTFileList(tmpdir);

        try {
//            String host = "http://uuxia.cn:8421/v1/api/file";
            String host = "http://192.168.1.100:8421/v1/api/file";
            for (File file : files) {
                String ret = SimpleHttpUtils.uploadFile(host, headers, file,"wahha");
                System.out.println("Name:" + file.getName() + " ret "+ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Exception "+e.getMessage());
        }
    }
}
