package com.java.pi;

import com.java.pi.util.Logc;

import java.io.File;

public class Test {
    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
            + "raspberry" + File.separator
            + "web" + File.separator;




    public static void main(String[] args) {
        Logc.e(FILE_PATH);
        fuck();
//        try {
//            new WebServer(8088).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        int port = 8088;
//        System.out.println(port);
//        try {
//            Server.start(port);
//        } catch (IOException var2) {
//            var2.printStackTrace();
//        }


    }

    private static void fuck(){
        Logc.e("fuck you"+Thread.class.getName());
    }

}