package com.ws.log;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    public static void SayInFile(){
        try {
            //文件生成路径
            PrintStream ps=new PrintStream("D:\\role.txt");
            System.setOut(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
//        WebSocketImpl.DEBUG = false;
//        int port = 8125; // 端口
//        WsServer s = new WsServer(port);
//        s.start();



        System.out.println(System.getProperty("java.io.tmpdir"));

//        SayInFile();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("System.out.println:test");
//                }
//            }
//        }).start();
    }
}
