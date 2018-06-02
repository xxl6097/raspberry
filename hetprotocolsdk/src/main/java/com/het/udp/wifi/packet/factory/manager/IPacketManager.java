package com.het.udp.wifi.packet.factory.manager;


import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.IPacketOut;

/**
 * Created by uuxia-mac on 15/6/21.
 * 抽象工厂接口
 * 构建数据解析和封装
 */
public interface IPacketManager /*extends Serializable */{
    /**
     * 构建数据解析方法
     *
     * @return
     */
    IPacketIn createIn();

    /**
     * 构建数据封装方法
     *
     * @return
     */
    IPacketOut createOut();
}
