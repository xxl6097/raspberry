package com.het.udp.wifi.packet.factory.manager.impl;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.factory.manager.IPacketManager;
import com.het.udp.wifi.packet.factory.manager.IPacketVersionManager;
import com.het.udp.wifi.packet.factory.unknown.UnKnownPacket;
import com.het.udp.wifi.packet.factory.v41.Packet_v41;
import com.het.udp.wifi.packet.factory.v42.Packet_v42;
import com.het.udp.wifi.packet.factory.vopen.Packet_open;

/**
 * Created by uuxia-mac on 15/6/21.
 * 工厂类
 * 构建不同版本的通讯协议
 */
public class PacketVersionManager implements IPacketVersionManager {
    @Override
    public IPacketManager createVersion(PacketModel p) {
        if (p != null && p.getDeviceInfo() != null) {
            byte packetStart = p.getPacketStart();
            if (packetStart == Packet_v41.packetStart) {
                byte version = p.getDeviceInfo().getProtocolVersion();
                if ((version & 0xFF) == Packet_v42.PROTOCOL_VERSION) {
//                    if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version42 protocol...");
                    return new Packet_v42(p);
                } else if ((version & 0xFF) == Packet_v41.PROTOCOL_VERSION) {
//                    if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version41 protocol...");
                    return new Packet_v41(p);
                }
            } else if (packetStart == Packet_open.packetStart) {
                return new Packet_open(p);
            }
        }
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create UnKnown version protocol...");
        return new UnKnownPacket(p);
    }
}
