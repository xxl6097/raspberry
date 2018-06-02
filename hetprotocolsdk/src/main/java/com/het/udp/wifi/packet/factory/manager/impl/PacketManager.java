package com.het.udp.wifi.packet.factory.manager.impl;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.IPacketOut;
import com.het.udp.wifi.packet.factory.manager.IPacketManager;

/**
 * Created by uuxia-mac on 15/6/21.
 * 数据包管理工厂
 */
public class PacketManager implements IPacketManager {
    private PacketModel packet;

    public PacketManager(PacketModel packet) {
        this.packet = packet;
    }

    @Override
    public IPacketIn createIn() {
        return new PacketVersionManager().createVersion(packet).createIn();
    }

    @Override
    public IPacketOut createOut() {
        return new PacketVersionManager().createVersion(packet).createOut();
    }

}
