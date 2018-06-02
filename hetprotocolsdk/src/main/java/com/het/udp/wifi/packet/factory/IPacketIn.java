package com.het.udp.wifi.packet.factory;


import com.het.udp.wifi.packet.PacketParseException;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 15/6/21.
 * 工厂方法
 * 数据解析方法
 */
public interface IPacketIn extends Serializable {
    void packetIn() throws PacketParseException;
}
