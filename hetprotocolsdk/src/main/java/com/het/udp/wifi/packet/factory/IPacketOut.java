package com.het.udp.wifi.packet.factory;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 15/6/21.
 * 工厂方法
 * 封装数据方法
 */
public interface IPacketOut extends Serializable {
    byte[] packetOut();
}
