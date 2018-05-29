package com.java.pi.services;

import com.java.pi.util.Ping;

import java.io.IOException;

public class Frpc {
    public static void startFrpc(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    boolean conn = Ping.isConnBaidu(3000);
                    if (conn){
                        try {
                            Runtime.getRuntime().exec("sh /home/bootstart");//注销
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
