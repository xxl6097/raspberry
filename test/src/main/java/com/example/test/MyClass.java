package com.example.test;

import java.io.IOException;
import java.util.Random;

public class MyClass {
    public static void main(String[] args) {
//        try {
//            Runtime.getRuntime().exec("systemctl restart home-assistant@root");//注销
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String monitorMac = "AAAAAA";
        monitorMac = monitorMac + new Random().nextInt(1000000);
        System.out.println(monitorMac);
        //startFrpc();
    }


    public static void startFrpc(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    boolean conn = Ping.isConnBaidu(3000);
                    if (conn){
                        try {
                            Runtime.getRuntime().exec("/home/frp/frp_0.19.1_linux_arm/frpcerver start");//注销
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
