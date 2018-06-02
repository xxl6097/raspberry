package com.het.udp.wifi.packet.factory.io;


import com.het.udp.wifi.packet.factory.IPacketOut;

import java.nio.ByteBuffer;

/**
 * Created by uuxia-mac on 15/6/20.
 */
public interface IOut extends IPacketOut {
    /**
     * 将包头部转化为字节流, 写入指定的ByteBuffer对象.
     *
     * @param buf 写入的ByteBuffer对象.
     */
    void putHead(ByteBuffer buf);

    /**
     * 初始化包体
     *
     * @param buf ByteBuffer
     */
    void putBody(ByteBuffer buf);

    /**
     * 初始化CRC16/X25
     *
     * @param buf ByteBuffer
     */
    void putCRC(ByteBuffer buf);

    /**
     * 封装数据包
     */
    void fill(ByteBuffer buf);
}
