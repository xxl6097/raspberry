/*
 * -----------------------------------------------------------------
 * Copyright ©2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: PacketUtils.java
 * Create: 2015/9/17 10:47
 */
package com.het.udp.wifi.packet;

import com.het.protocol.CProtocolManager;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.manager.impl.PacketManager;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: UUXIA
 * Date: 2015-09-17 10:47
 * Description: UDP数据报文解码与编码
 */
public class PacketUtils {
    public static boolean usecppprotocol = false;

    /**
     * 解码报文
     *
     * @param packet
     * @throws PacketParseException
     */
    public static void in(PacketModel packet) throws PacketParseException {
        if (usecppprotocol) {
            try {
                CProtocolManager.parseData(packet);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PacketParseException(e.getMessage());
            }
        } else {
            PacketManager packetManager = new PacketManager(packet);
            IPacketIn packetIn = packetManager.createIn();
            packetIn.packetIn();
        }
    }

    public static void inCpp(PacketModel packet) throws PacketParseException {
        try {
            CProtocolManager.parseData(packet);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PacketParseException(e.getMessage());
        }
    }

    public static byte[] outCpp(PacketModel packet) throws PacketParseException {
        try {
            return CProtocolManager.packageData(packet);
        } catch (IOException e) {
            e.printStackTrace();
//                System.err.println("out:"+e.getMessage());
        }
        return null;
    }

    /**
     * 编码报文
     *
     * @param packet
     * @return
     */
    public static byte[] out(PacketModel packet) {
        if (usecppprotocol) {
            try {
                return CProtocolManager.packageData(packet);
            } catch (IOException e) {
                e.printStackTrace();
//                System.err.println("out:"+e.getMessage());
            }
        } else {
            return new PacketManager(packet).createOut().packetOut();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("hello world!");
        byte[] runData = new byte[]{(byte) 0x5A, (byte) 0x00, (byte) 0x52,
                (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0x99, (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x01,
                (byte) 0x8C, (byte) 0x18, (byte) 0xD9, (byte) 0xFF, (byte) 0xEB,
                (byte) 0x9D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x05,
                (byte) 0x00, (byte) 0x02, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x03,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03,
                (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32, (byte) 0x79};
        PacketModel packet = new PacketModel();
        packet.setData(runData);
        try {
            in(packet);
        } catch (PacketParseException e) {
            e.printStackTrace();
        }
    }
}
