package com.het.xml.protocol.coder.encode.crc;

/**
 * 循环冗余计算
 *
 * @author jake
 */
public interface CrcCalculateStrategy {

    int calculate(byte[] data) throws Exception;
}
