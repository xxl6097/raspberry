package com.het.protocol.core.factory.manager.impl;


import com.het.protocol.bean.PacketBean;
import com.het.protocol.core.factory.IPacketIn;
import com.het.protocol.core.factory.IPacketOut;
import com.het.protocol.core.factory.manager.IPacketManager;

/**
 * Created by uuxia-mac on 15/6/21.
 * 数据包管理工厂
 */
public class PacketManager implements IPacketManager {
    private PacketBean packet;

    public PacketManager(PacketBean packet) {
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
