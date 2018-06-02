package com.het.protocol.core.factory.io;



import com.het.protocol.core.PacketParseException;
import com.het.protocol.core.factory.IPacketIn;

import java.nio.ByteBuffer;

/**
 * Created by uuxia-mac on 15/6/20.
 */
public interface IIn extends IPacketIn {
    /**
     * 校验头部
     *
     * @return true表示头部有效
     */
    boolean validateHeader(ByteBuffer buf) throws Exception;

    /**
     * 计算包体数据
     *
     * @param buf
     * @param length
     * @return
     * @throws PacketParseException
     */
    byte[] calcBody(ByteBuffer buf, int length) throws PacketParseException;

    /**
     * 从buf的当前位置解析包头
     *
     * @param buf ByteBuffer
     */
    void parseHeader(ByteBuffer buf) throws PacketParseException;

    /**
     * 从buf的当前未知解析包尾
     *
     * @param buf ByteBuffer
     */
    void parseTail(ByteBuffer buf) throws PacketParseException;

    /**
     * 解析数据
     *
     * @return
     */
    Object toPacketModel();


}
