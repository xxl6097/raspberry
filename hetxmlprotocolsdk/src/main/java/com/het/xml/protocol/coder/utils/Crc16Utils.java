package com.het.xml.protocol.coder.utils;


/**
 * @author Alone
 */
public class Crc16Utils {
    /**
     * CRC16/X25校验
     *
     * @param data
     * @param length
     * @return
     */
    public static int computeChecksum(byte[] data, int length) {
        int j = 0;
        int crc16 = 0x0000FFFF;
        for (int i = 1; i < length; i++) {
            crc16 ^= data[i] & 0x000000FF;
            for (j = 0; j < 8; j++) {
                int flags = crc16 & 0x00000001;
                if (flags != 0) {
                    crc16 = (crc16 >> 1) ^ 0x8408;
                } else {
                    crc16 >>= 0x01;
                }
            }
        }
        return ~crc16 & 0x0000FFFF;
    }

    /**
     * CRC16/X25校验
     *
     * @param data
     * @return
     */
    public static int computeBodyCrc(byte[] data) {
        int j = 0;
        int crc16 = 0x0000FFFF;
        for (int i = 0; i < data.length; i++) {
            crc16 ^= data[i] & 0x000000FF;
            for (j = 0; j < 8; j++) {
                int flags = crc16 & 0x00000001;
                if (flags != 0) {
                    crc16 = (crc16 >> 1) ^ 0x8408;
                } else {
                    crc16 >>= 0x01;
                }
            }
        }
        return ~crc16 & 0x0000FFFF;
    }

    public static void main(String[] args) {
        byte[] bytes = StringUtil.hexStringToByteArray("f241100003accf233ba86a080b00000501007e000017d9f241100003accf233ba86a080b00000501007e0000");
//        System.out.println(Crc16Utils.computeChecksum(bytes, bytes.length));
    }
}
