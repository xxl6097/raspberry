package com.java.pi;

import com.java.log.JavaLog;
import com.java.pi.http.Server;
import com.java.pi.services.Frpc;
import com.java.pi.services.MiSocketPlusManager;
import com.java.pi.wifi.LanDiscover;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //启动HTTP服务
        startServer(args);
        //每30s检测一次MiSocketPlus状态，如果关闭了，则开启
        MiSocketPlusManager.getInstance().keepMiSocketPlusHangon(30);
        //启动MQTT
//        MQHandler.startMQ();
        //查看日志
        JavaLog.startLog(8126);

        Frpc.startFrpc();
    }



    private static void startServer(String[] args){
        System.out.println(Arrays.toString(args));
        try {
            Server.start(Integer.parseInt(args[0]));
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }


    private static void scanLan(){
        System.out.println(System.getProperty("user.dir"));
        LanDiscover lanDiscover = new LanDiscover();
        final String pcMacAddr = "C8:E7:D8:DE:5C:62";
        final String iosMacAddr = "50:7A:55:E9:9F:CC";
//        final String iosMacAddr = "DC:A9:04:C6:F5:66";
        String locIp = "192.168.1.1";//LanDiscover.getLocAddress();
        lanDiscover.discoverLoop(10 * 1000, locIp, new LanDiscover.OnDiscoverDevice() {
            @Override
            public boolean onDeviceState(boolean online, LanDiscover.Device device) {
                if (device != null && device.mac != null) {
                    String curMac = device.mac;
                    curMac = curMac.replaceAll(":", "");
                    curMac = curMac.replaceAll("-", "");
                    String tarMac = iosMacAddr;
                    tarMac = tarMac.replaceAll(":", "");
                    tarMac = tarMac.replaceAll("-", "");
                    if (curMac.toUpperCase().equals(tarMac.toUpperCase())) {
                        if (online) {
                            System.out.println("夏小力的Iphone上线啦~~~");
                        } else {
                            System.out.println("夏小力的Iphone离线啦~~~");
                        }
                    }
                }
//                System.out.println(online+"~~~"+device.toString());
                return true;
            }

            @Override
            public void onLogc(String str) {
//                System.err.println(str);
            }
        });
    }

}
