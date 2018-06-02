package com.het.protocol.core.factory.manager;


import com.het.protocol.core.factory.IPacketIn;
import com.het.protocol.core.factory.IPacketOut;

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
