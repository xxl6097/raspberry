package com.het.protocol.core.factory.manager;


import com.het.protocol.bean.PacketBean;

/**
 * Created by uuxia-mac on 15/6/21.
 * 协议版本管理
 */
public interface IPacketVersionManager {
    IPacketManager createVersion(PacketBean p);
}
