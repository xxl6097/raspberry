/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: GeneratePacket.java
 * Create: 2015/11/2 10:34
 */
package com.het.udp.wifi.packet.factory.vopen;

import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.Contants;

/**
 * Created by Android Studio.
 * Author: UUXIA
 * Date: 2015-11-02 10:34
 * Description: 构建开放平台协议报文
 */
public class GenerateOpenPacket {

    /**
     * 框架版本 0x40
     */
    private byte version = 0x40;
    /**
     * 协议类型 业务数据0x01
     */
    private byte type = 0x01;

    /**
     * 设备编码号
     */
    private int frameNo = 0x20000000;
    /**
     * 设备Mac地址
     */
    private String mac;// = "accf235614bc";

    private byte[] body;


    /**
     * 构建开放平台数据报文
     *
     * @param cmd 命令字
     */
    public PacketModel generate(short cmd) {
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setCommandType(cmd);
        udpDeviceDataBean.setPacketStart(Packet_open.packetStart);
        udpDeviceDataBean.setProtocolType(type);
        udpDeviceDataBean.setProtocolVersion(version);
        udpDeviceDataBean.setFrameSN(frameNo);
        udpDeviceDataBean.setDeviceMac(mac);
        p.setDeviceInfo(udpDeviceDataBean);
        p.setBody(body);
        PacketUtils.out(p);
        return p;
    }

    public PacketModel generate(short cmd, PacketModel p) {
        if (p == null) {
            p = new PacketModel();
        }
        UdpDeviceDataBean udpDeviceDataBean = p.getDeviceInfo();
        if (udpDeviceDataBean == null) {
            udpDeviceDataBean = new UdpDeviceDataBean();
        }
        if (udpDeviceDataBean.getDeviceMac() != null){
            mac = udpDeviceDataBean.getDeviceMac();
        }
        udpDeviceDataBean.setCommandType(cmd);
        udpDeviceDataBean.setPacketStart(Packet_open.packetStart);
        udpDeviceDataBean.setProtocolType(type);
        udpDeviceDataBean.setProtocolVersion(version);
        udpDeviceDataBean.setFrameSN(frameNo);
        udpDeviceDataBean.setDeviceMac(mac);
        p.setDeviceInfo(udpDeviceDataBean);
        p.setBody(body);
        PacketUtils.out(p);
        return p;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * 构建查询控制数据报文
     *
     * @return
     */
    public PacketModel generateReqConfigPacket() {
        return generate(Contants.OPEN.CONFIG._HET_OPEN_CONFIG_REQ);
    }

    public PacketModel generateSendConfigPacket(PacketModel p) {
        return generate(Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND, p);
    }


    /**
     * 构建查询运行数据报文
     *
     * @return
     */
    public PacketModel generateReqRunPacket() {
        return generate(Contants.OPEN.RUN._HET_OPEN_RUN_REQ);
    }

    public void setFrameNo(int frameNo) {
        this.frameNo |= (frameNo & 0x0FFFFFFF);
    }


    public void setFrameNoEx(int frameNo) {
        this.frameNo = frameNo;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public static void main(String[] args) {
//        GenerateOpenPacket generateOpenPacket = new GenerateOpenPacket();
//
//        generateOpenPacket.setFrameNo((int) 0x1234);//-15528
//        System.out.println(generateOpenPacket.getFrameNo() + " hex=" + Integer.toHexString(generateOpenPacket.getFrameNo()));
//
//
//        generateOpenPacket.setFrameNoEx(0);
//        PacketModel packetModel = generateOpenPacket.generate(Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND);
//        System.out.println(ByteUtils.toHexString(packetModel.getData()));
//
//
//        int a = 536875572;
//        System.out.println((a & 0xFFFFFFFF) >> 28);
        int frameNo = 0x20000000;
        int a = (int) System.currentTimeMillis();
//        System.out.println(a);
        frameNo |= (a & 0x0FFFFFFF);
//        System.out.println(frameNo);
    }
}
