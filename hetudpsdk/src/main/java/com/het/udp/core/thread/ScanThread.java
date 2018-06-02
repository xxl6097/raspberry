package com.het.udp.core.thread;

import com.het.udp.core.UdpDataManager;
import com.het.udp.wifi.model.PacketModel;

/**
 * Created by UUXIA on 2015/6/23.
 */
public class ScanThread /*extends Thread */ {
    private boolean running = false;
    private PacketModel packet = null;
    private int repet = 5;

    public void setRepet(int repet) {
        this.repet = repet;
    }

    public void setPacket(PacketModel packet) {
        this.packet = packet;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
        startThread();
    }

//    @Override
//    public void run() {
//        super.run();
//        try {
//            Logc.i(Logc.HetReportTag.INFO_WIFI,"开始进行5s一次局域网扫描设备...");
//            for (int i = 0; i < repet; i++) {
//                if (!running)
//                    return;
//                if (packet == null)
//                    continue;
//                UdpDataManager.getInstance().send(packet);
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            running = false;
//        }
//    }

    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Logc.i(Logc.HetReportTag.INFO_WIFI,"开始进行5s一次局域网扫描设备...");
                    for (int i = 0; i < repet; i++) {
                        if (!running)
                            return;
                        if (packet == null)
                            continue;
                        UdpDataManager.getInstance().send(packet);
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                }
            }
        }, "ScanThread").start();
    }
}
