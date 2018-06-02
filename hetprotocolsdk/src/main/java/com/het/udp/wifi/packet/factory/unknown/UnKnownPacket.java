package com.het.udp.wifi.packet.factory.unknown;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.PacketParseException;
import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.IPacketOut;
import com.het.udp.wifi.packet.factory.manager.IPacketManager;

/**
 * Created by uuxia-mac on 15/6/21.
 */
public class UnKnownPacket implements IPacketManager, IPacketIn, IPacketOut {
    private PacketModel packet;

    public UnKnownPacket(PacketModel packet) {
        this.packet = packet;
        if (this.packet == null) {
//            if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"packet is null");
            return;
        }
        if (this.packet.getDeviceInfo() == null) {
//            if (LOG.PACKET_VERSION_OFF) Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"packet's device info is null");
            return;
        }
//        if (LOG.PACKET_VERSION_OFF)
//            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"unKnown protocol version is:" + packet.getDeviceInfo().getProtocolVersion());
    }

    @Override
    public IPacketIn createIn() {
        return this;
    }

    @Override
    public IPacketOut createOut() {
        return this;
    }

    @Override
    public void packetIn() throws PacketParseException {
//        if (LOG.PACKET_VERSION_OFF) Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"data parse error...");
    }

    @Override
    public byte[] packetOut() {
//        if (LOG.PACKET_VERSION_OFF) Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"data package error...");
        return null;
    }
}
