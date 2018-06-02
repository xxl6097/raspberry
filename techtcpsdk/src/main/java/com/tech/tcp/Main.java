package com.tech.tcp;

import com.tech.tcp.core.TcpHandler;
import com.tech.tcp.core.bean.PacketDataBean;
import com.tech.tcp.core.callback.OnDataRecv;
import com.tech.tcp.util.Logc;

import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) {
        startTcp();

    }

    static TcpHandler tcpClient;
    public static void startTcp(){
        tcpClient = new TcpHandler("uuxia.cn", 8888, new OnDataRecv() {
            @Override
            public void onConnected() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int index = 0;
                        while (true) {
                            String message = "hello world" + index+"\r\n";
                            tcpClient.send(new PacketDataBean(message.getBytes()));
                            Logc.i(index+"");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            index++;
                            if (index == 10){
//                                tcpClient.stop();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void messageReceived(byte[] data) {
                try {
                    System.out.println(new String(data, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        tcpClient.start();
    }


}
