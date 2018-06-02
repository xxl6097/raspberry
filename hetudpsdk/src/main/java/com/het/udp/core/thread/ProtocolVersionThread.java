package com.het.udp.core.thread;

import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.DataType;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.packet.factory.vopen.GenerateOpenPacket;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

/**
 * Created by UUXIA on 2015/6/23.
 */
public class ProtocolVersionThread /*extends Thread*/ {
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public synchronized void start() {
        running = true;
        startThread();
    }

//    @Override
//    public void run() {
//        super.run();
//        try {
//            for (int i = 0; i < 5; i++) {
//                if (!running)
//                    return;
//                UdpDataManager.getInstance().send(get4001Packet());
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
                    for (int i = 0; i < 5; i++) {
                        if (!running)
                            return;
//                        UdpDataManager.getInstance().send(getScanPacket());
                        UdpDataManager.getInstance().send(new GenerateOpenPacket().generateReqRunPacket());
                        Thread.sleep(200);
                        UdpDataManager.getInstance().send(get4005Packet());
                        UdpDataManager.getInstance().send(get9400Pcket());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                }
            }
        }, "ProtocolVersionThread").start();
    }

    /**
     * 查询协议版本
     *
     * @return
     */
    private PacketModel get4001Packet() {
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setCommandType(Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION_CHECK);
        udpDeviceDataBean.setDataStatus((byte) -128);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答 0000
        p.setDeviceInfo(udpDeviceDataBean);
        PacketUtils.out(p);
        return p;
    }

    /**
     * 查询协议版本
     *
     * @return
     */
    private PacketModel get4005Packet() {
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setCommandType(Contants.HET_LAN_SEND_RUN_REQ);
        udpDeviceDataBean.setDataStatus((byte) -128);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答 0000
        p.setDeviceInfo(udpDeviceDataBean);
        PacketUtils.out(p);
        return p;
    }

    /**
     * 开放平台扫描指令，其实就是请求设备控制数据的指令，只是报文体数据内容不带用户秘钥而已
     */
    private PacketModel getScanPacket() {
        if (UdpDataManager.getInstance().getDataType() == DataType.OPEN) {
            return new GenerateOpenPacket().generateReqRunPacket();
        } else {
            return get4005Packet();
        }
    }

    private PacketModel get9400Pcket() {
        int frameNo = ByteUtils.calcFrameNumber();
        GenerateOpenPacket generateOpenPacket = new GenerateOpenPacket();
        generateOpenPacket.setFrameNo(frameNo);
        PacketModel packetModel = generateOpenPacket.generate(Contants.GATEWAY_DISCOVER_RECV);
        PacketUtils.out(packetModel);
        return packetModel;
    }

    public static void main(String[] args) {
        PacketModel packetModel = new ProtocolVersionThread().getScanPacket();
//        System.out.println(ByteUtils.toHexString(packetModel.getData()));
    }
}
