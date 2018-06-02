/*
 * -----------------------------------------------------------------
 * Copyright (C) 2012-2013, by Het, ShenZhen, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: ByteUtils.java
 * Author: clark
 * Version: 1.0
 * Create: 2013-11-11
 *
 * Changes (from 2013-11-11)
 * -----------------------------------------------------------------
 * 2013-11-11 : 创建 ByteUtils.java (clark);
 * -----------------------------------------------------------------
 */
package com.het.protocol.util;


/**
 * @ClassName: ProtoUtils
 * @Description: 字节通用�?
 * @Author: clark
 * @Create: 2013-11-11
 */

public final class ProtoUtils {
    /**
     * 2字节转int
     *
     * @param byte1
     * @param byte2
     * @return
     */
    public static int getDataLength(byte byte1, byte byte2) {
        int len16 = 0;
        String hex = Integer.toHexString(byte1 & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        len16 = Integer.valueOf(hex, 16);
        int len17 = 0;
        String hex1 = Integer.toHexString(byte2 & 0xFF);
        if (hex1.length() == 1) {
            hex1 = '0' + hex1;
        }
        len17 = Integer.valueOf(hex1, 16);
        int pktLen = len16 * 256 + len17;
        return pktLen;
    }
    public static int getCommandNew(byte[] data) {
//        if (buf != null && buf.capacity() > 10) {
//            return buf.getShort(9);
//        }
        if (data != null && data.length > 5) {
            int dataLen = getDataLength(data[3],
                    data[4]);
            return dataLen;
        }
        return -1;
    }
    public static int getCommandNew(byte[] data, int index) {
        if (data != null && data.length > index) {
            int dataLen = getDataLength(data[index],
                    data[index + 1]);
            return dataLen;
        }
        return -1;
    }

    public static int getCommandForOpen(byte[] data) {
        int index = 31;
        if (data != null && data.length > index) {
            int dataLen = getDataLength(data[index],
                    data[index + 1]);
            return dataLen;
        }
        return -1;
    }
    public static String toHexStrings(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if (b != null)
            for (int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                buffer.append(s + " ");
            }
        return buffer.toString();
    }

    public static String toHexString(byte[] b) {
//        StringBuffer buffer = new StringBuffer();
//        if (b != null)
//            for (int i = 0; i < b.length; ++i) {
//                String s = Integer.toHexString(b[i] & 0xFF);
//                if (s.length() == 1) {
//                    s = "0" + s;
//                }
//                buffer.append(s + " ");
//            }
//        return buffer.toString();
        if (b != null && b.length > 0) {
            if (b[0] == 0x5A) {
                return toHexStringForOpen(b);
            } else {
                return toHexStringForHet(b);
            }
        }
        return toHexStringForHet(b);
    }

    public static String toHexStringForHet(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if (b != null)
            for (int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                if (b[1] == 0x42) {
                    if (i < 2 || (i > 2 && i < 4) || (i > 4 && i < 10) || (i > 10 && i < 18) || (i
                            > 18 && i < 20) || (i > 20 && i < 24) || (i > 24 && i < 32)
                            || (i > 32 && i < 34) || (i > 34 && i < (b.length - 5)) || (i > (b
                            .length - 5) && i < (b.length - 1))) {
                        buffer.append(s);
                    } else {
                        buffer.append(s + " ");
                    }
                } else {
                    if (i < 2 || (i > 2 && i < 4) || (i > 4 && i < 10) || (i > 10 && i < 13) || (i
                            > 13 && i < 15) || (i > 15 && i < 48) || (i > 48 && i < (b.length - 3)) || (i > (b
                            .length - 3) && i < (b.length - 1))) {
                        buffer.append(s);
                    } else {
                        buffer.append(s + " ");
                    }
                }

            }
        return buffer.toString();
    }

    public static String toHexStringForOpen(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if (b != null)
            for (int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                if ((i > 0 && i < 2) || ((i > 2 && i < 4) || (i > 4 && i < 12)) || (i > 12 && i < 18) || (i > 18 && i < 22) || (i
                        > 22 && i < 30) || (i > 30 && i < 32) || (i > 32 && i < (b.length - 3))
                        || (i > (b.length - 3) && i < (b.length - 1))) {
                    buffer.append(s);
                } else {
                    buffer.append(s + " ");
                }
            }
        return buffer.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }


    /**
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * 判断字符串是否为空或空字�?
     *
     * @param strSource 源字符串
     * @return true表示为空，false表示不为�?
     */
    public static boolean isNull(final String strSource) {
        return strSource == null || "".equals(strSource.trim());
    }


    /**
     * Mac地址转换
     *
     * @param resBytes
     * @return
     */
    public static String byteToMac(byte[] resBytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < resBytes.length; i++) {
            String hex = Integer.toHexString(resBytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            buffer.append(hex.toUpperCase());
        }
        return buffer.toString();
    }

    /**
     * CRC16/X25校验
     *
     * @param data
     * @param length
     * @return
     */
    public static byte[] CRC16Calc(byte[] data, int length) {
        int j = 0;
        int crc16 = 0x0000FFFF;
        for (int i = 0; i < length; i++) {
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
        int ret = ~crc16 & 0x0000FFFF;
        byte[] crc = new byte[2];
        crc[1] = (byte) (ret & 0x000000FF);
        crc[0] = (byte) ((ret >> 8) & 0x000000FF);
        return crc;
    }

    public static byte getProtocolVersion(byte[] data) {
        if (data != null && data[0] == (byte) 0xF2 && data.length > 2) {
            return data[1];
        }
        return -1;
    }


}
