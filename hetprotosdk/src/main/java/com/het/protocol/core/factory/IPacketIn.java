package com.het.protocol.core.factory;



import com.het.protocol.core.PacketParseException;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 15/6/21.
 * 工厂方法
 * 数据解析方法
 */
public interface IPacketIn extends Serializable {
    void packetIn() throws PacketParseException;
}
