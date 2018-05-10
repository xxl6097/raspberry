package com.java.pi;

import com.java.pi.http.Server;

import java.io.File;
import java.io.IOException;

public class Test {
    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
            + "raspberry" + File.separator
            + "web" + File.separator;

    public static void main(String[] args) {
        System.out.println(FILE_PATH);
//        try {
//            new WebServer(8088).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int port = 8088;
        System.out.println(port);
        try {
            Server.start(port);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

}