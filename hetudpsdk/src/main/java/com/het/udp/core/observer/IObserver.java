package com.het.udp.core.observer;


import com.het.udp.wifi.model.PacketModel;

public interface IObserver {
    /**
     * 设备数据回调接口
     */
    void receive(PacketModel o);

    /**
     *
     * @param body  设备数据
     * @param dataType 数据类型：1.控制数据，2.运行数据，3.基本数据
     * @param macAddr mac地址
     * @param deviceType
     * @param deviceSubTypt
     */
//    void receive(byte[] body ,short dataType,String macAddr,int deviceType,int deviceSubTypt);
}
