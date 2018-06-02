package com.het.udp.wifi.packet.factory.manager;


import com.het.udp.wifi.model.PacketModel;

/**
 * Created by uuxia-mac on 15/6/21.
 * 协议版本管理
 */
public interface IPacketVersionManager {
    IPacketManager createVersion(PacketModel p);
}
