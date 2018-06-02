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
package com.het.udp.wifi.utils;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ByteUtils
 * @Description: 字节通用�?
 * @Author: clark
 * @Create: 2013-11-11
 */

public final class ByteUtils {

    private static byte HEAD = (byte) 0xF2;
    // 当前BUFFER 的总数byte,（位置指引）
    private static int currentSizeNew = 0;
    // 缓冲BUFF
    private static byte[] cashBufferNew = new byte[4096];

    /**
     * 私有的构造方�?
     */
    private ByteUtils() {
    }

    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * 将精准缩短至毫秒级别，生成包的序列号
     * 只要间隔不低于70毫秒，生成的数据则不会重复且是顺序的
     *
     * @return
     */
    public static short calcFrameShort() {
        Calendar c = Calendar.getInstance();
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte minute = (byte) c.get(Calendar.MINUTE);
        byte second = (byte) c.get(Calendar.SECOND);
        int minsec = c.get(Calendar.MILLISECOND);
        byte mm = (byte) ((minsec >>> 6) & 0x0F);
        short no = (short) ((minute << 10) | (second << 4) | mm);
        //System.out.println("############ " +hour+":"+minute+":"+second+"  "+no);
        return no;
    }

    /**
     * 将精准缩短至毫秒级别，生成包的序列号
     * 只要间隔不低于250毫秒，生成的数据则不会重复且是顺序的
     *
     * @return
     */
    public static int calcFrameNumber() {
        Calendar c = Calendar.getInstance();
        byte day = (byte) c.get(Calendar.DAY_OF_MONTH);
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte minute = (byte) c.get(Calendar.MINUTE);
        byte second = (byte) c.get(Calendar.SECOND);
        int minsec = c.get(Calendar.MILLISECOND);
        byte mm = (byte) ((minsec >> 8) & 0x03);
        int no = ((day << 19) | (hour << 14) | (minute << 8) | (second << 2) | mm);
//        System.out.println("############ " +day+":"+hour+":"+minute+":"+second+"  "+no);
        return no;
    }

    /**
     * 将精准缩短至毫秒级别，生成包的序列号
     * 只要间隔不低于70毫秒，生成的数据则不会重复且是顺序的
     *
     * @return
     */
    public static short calcFrameNumber1() {
        Calendar c = Calendar.getInstance();
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte minute = (byte) c.get(Calendar.MINUTE);
        byte second = (byte) c.get(Calendar.SECOND);
        int minsec = c.get(Calendar.MILLISECOND);
        int a3 = (minsec >> 8) & 0x03;
        int a2 = (minsec >> 6) & 0x03;
        byte millsec = (byte) ((a3 << 2) | a2);
        short no = (short) ((hour << 12) | (minute << 8) | (second << 4) | millsec);
//        System.out.println(hour+":"+minute+":"+second+"  "+no);
        return no;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value = -1;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
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

    public static int threebytesToInt(byte[] src) {
        int value = -1;
        value = (((src[0 + 0] & 0xFF) << 16)
                | ((src[0 + 1] & 0xFF) << 8)
                | (src[0 + 2] & 0xFF));
        return value;
    }

    /**
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


//    public static byte[] short2bytes(int sht) {
//        byte[] sb = new byte[2];
//        for (int i = 0; i < sb.length; i++) {
//            sb[i] = (byte) (sht >> (i * 8) & 0xFF);
//        }
//        return sb;
//    }

    public static void putShort(short sht, byte[] sb, int index) {
        for (int i = 0; i < 2; i++) {
            sb[1 - i + index] = (byte) (sht >> (i * 8) & 0xFF);
        }
    }

    private static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (shortBuf.length - 1 - i) * 8;
            shortBuf[i] = (byte) ((s >>> offset) & 0xff);
        }
        return shortBuf;
    }

    public static byte[] get32BytesKey(String userName) {
        if (userName != null && !isNull(userName)) {
            String key = SecurityUtils.string2MD5(userName);
            byte[] userKey = key.getBytes();
            //Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "32bitKey=" + Arrays.toString(userKey));
            return userKey;
        }
        return null;
    }


    public static byte[] verifyData(byte[] data, int size) {
        int i = 0;// 记录，数据头--位置
        if (0 == currentSizeNew) { // currentSize代表数据缓冲区的当前的大小
            // 如果当前缓冲区没数据，先找到数据的头
            while ((i < size) && (data[i] != HEAD)) {//i小于总size，且不等于报头则继续找报头
                i++;
            }
            //i=0表示报文头完整
            // 如果没找到就不要这帧数据
            if (i == size) {
                return null;
            }
            // 如果找到了，就截取数据
            size -= i;
            System.arraycopy(data, i, cashBufferNew, 0, size);
        } else {
            // 如果缓冲区有数据，直接把数据追加到缓冲区cashBuffer
            System.arraycopy(data, 0, cashBufferNew, currentSizeNew, size);
        }
        currentSizeNew += size;
        do {
            // 如果当前数据包长度小于最小包长，说明数据分包了，就直接跳出循环
            if (currentSizeNew < Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER) {
                return null;
            }

            // 如果当前数据包长度大于最小包长度，就计算该数据帧的长度
            // 获取有效数据长度 00 0e
            int dataLen = getDataLength(cashBufferNew[14],
                    cashBufferNew[15]);
            int pktLen = dataLen + Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
            // ----------也许会出问题--------------------------------
            if (pktLen > 4000) {
                currentSizeNew = 0;
                return null;
            }
            // 如果包长度大于currentSize，说明数据包补完整，跳出循环
            if (pktLen > currentSizeNew) {
                return null;
            }
            // 如果数据包小于等于currentSize，就拷贝数据到
            byte[] fullpacket = new byte[pktLen];
            System.arraycopy(cashBufferNew, 0, fullpacket, 0, pktLen);
            // 拷贝完数据包后，就开始整理缓冲区---总的数据--
            if (currentSizeNew > pktLen) {// 对多个包进行，数据整理，保证数据开头为0 7E7E （保证准确性）
                // 从剩下的数据中开始找到数据头部
                for (i = pktLen; i < currentSizeNew; i++) {
                    if ((cashBufferNew[i] == HEAD)) {
                        break;// 找到数据头以后就跳出循环
                    }
                }
                // 开始移动数据到缓存区前面
                int j = 0;
                for (; i < currentSizeNew; i++) {
                    cashBufferNew[j++] = cashBufferNew[i];
                }
                // 重置当前的缓冲区大小
                currentSizeNew = j;
            } else {
                currentSizeNew = 0;// 数据全部取走
            }
            if (/*fullpacket[pktLen - 1] == TAIL*/checkCRC16(fullpacket)) {
                return fullpacket;
            } else {
                //Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY check no pass");
            }
            //Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY this is good packet");

        } while (true);
    }

    public static boolean checkCRC16(byte[] data) {
        boolean isRight = false;
        if (data != null && data.length > 0) {
            int newByteLen = data.length - 3;
            if (newByteLen > 0) {
                byte[] tmp = new byte[newByteLen];
                System.arraycopy(data, 1, tmp, 0, newByteLen);
                byte[] crcKey = ByteUtils.CRC16Calc(tmp, newByteLen);
                if (crcKey.length == 2) {
                    if (data[data.length - 2] == crcKey[0] && data[data.length - 1] == crcKey[1]) {
                        isRight = true;
                    }
                }
            }
        }
        return isRight;
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

    public static String getCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//        System.out.println(sf.format(new Date()));
        return sf.format(new Date());
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
//        Logc.i(Logc.HetReportTag.INFO_WIFI,Integer.toBinaryString(crc[0]));
//        Logc.i(Logc.HetReportTag.INFO_WIFI,Integer.toBinaryString(crc[1]));
//        Logc.i(Logc.HetReportTag.INFO_WIFI,Integer.toBinaryString(ret));
//        Logc.i(Logc.HetReportTag.INFO_WIFI,"CRC16:"+Integer.toHexString(ret));
        return crc;
    }

    /**
     * CRC16/X25校验
     *
     * @param data
     * @param length
     * @return
     */
    public static int CRC(byte[] data, int length) {
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
        return ret;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getByteBit(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
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


    public static String getCmd(byte[] data) {
        if (data != null && data.length >= 5) {
            byte[] macBytes = new byte[2];
            macBytes[0] = data[3];
            macBytes[1] = data[4];
            return byteToMac(macBytes);
        }
        return null;
    }

    public static String getCmdForOPen(byte[] data) {
        int index = 31;
        if (data != null && data.length >= index) {
            byte[] macBytes = new byte[2];
            macBytes[0] = data[index];
            macBytes[1] = data[index + 1];
            return byteToMac(macBytes);
        }
        return null;
    }

    public static String getCmd(byte[] data, int index) {
        if (data != null && data.length >= index) {
            byte[] macBytes = new byte[2];
            macBytes[0] = data[index];
            macBytes[1] = data[index + 1];
            return byteToMac(macBytes);
        }
        return null;
    }

    public static String getMacAddr(byte[] data, int index) {
        if (data != null && data.length > index) {
            byte[] macBytes = new byte[6];
            System.arraycopy(data, index, macBytes, 0, 6);
            return byteToMac(macBytes);
        }
        return null;
    }

    public static String getMacAddr(byte[] data) {
        if (data != null && data.length > 11) {
            byte[] macBytes = new byte[6];
            System.arraycopy(data, 5, macBytes, 0, 6);
            return byteToMac(macBytes);
        }
        return null;
    }

    public static int getTypeNew(ByteBuffer buf) {
        int type = -1;
        if (buf != null && buf.capacity() > 11) {
            type = buf.get(11);
        }
        return type;
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

    public static String toIp(String ip) {
        if (isNull(ip))
            return ip;
        String[] lines = ip.split("\\.");
        if (lines == null || lines.length < 3)
            return ip;
        int len = lines[3].length();
        if (len == 1) {
            ip += "  ";
        } else if (len == 2) {
            ip += " ";
        }
        return ip;
    }

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

    public static byte getProtocolVersion(byte[] data) {
        if (data != null && data[0] == (byte) 0xF2) {
            return data[1];
        }
        return -1;
    }

    public static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static byte[] short2bytes(int intvalue) {
        intvalue = (intvalue & 0xFFFF);
        byte byte1 = (byte) (intvalue & 0xFF);
        byte byte2 = (byte) (intvalue >>> 8 & 0xFF);
        byte[] port = new byte[]{byte2, byte1};
        return port;
    }

    public static byte[] getBodyBytes(String ip, String port, byte[] key, byte[] ips) throws NumberFormatException, IOException {

        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[4];
        ipByte[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
        ipByte[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
        ipByte[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
        ipByte[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(ipByte);
        if (!isNum(port))
            throw new NumberFormatException("port is not number...");
        byte[] portByte = short2bytes(Integer.parseInt(port));
        dos.write(portByte);
//        dos.writeByte(key.getBytes().length);
        dos.write(key);
        if (ips != null && ips.length > 0 && dos != null) {
            dos.write(ips);
        }
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();

        return bs;
    }

    public static byte[] getBodyBytes(String ip, short port, byte[] key, byte ipLastByte) throws NumberFormatException, IOException {
        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[4];
        ipByte[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
        ipByte[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
        ipByte[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
        ipByte[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(ipByte);
        byte[] portByte = short2bytes(port);
        dos.write(portByte);
//        dos.writeByte(key.getBytes().length);
        dos.write(key);
        dos.writeByte(ipLastByte);
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();

        return bs;
    }

    public static byte[] getBodyBytesForOpen(String ip, String port, byte[] key) throws NumberFormatException, IOException {

        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[4];
        ipByte[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
        ipByte[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
        ipByte[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
        ipByte[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(key);
        dos.write(ipByte);
        if (!isNum(port))
            throw new NumberFormatException("port is not number...");
        byte[] portByte = short2bytes(Integer.parseInt(port));
        dos.write(portByte);
//        dos.writeByte(key.getBytes().length);
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();

        return bs;
    }

    public static byte[] getBodyBytesForOpen(String ip, short port, byte[] key) throws NumberFormatException, IOException {

        String[] ipArr = ip.split("\\.");
        byte[] ipByte = new byte[4];
        ipByte[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
        ipByte[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
        ipByte[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
        ipByte[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(key);
        dos.write(ipByte);
        byte[] portByte = short2bytes(port);
        dos.write(portByte);
//        dos.writeByte(key.getBytes().length);
        byte[] bs = baos.toByteArray();
        baos.close();
        dos.close();

        return bs;
    }


    /**
     * 判断参数是否为数�?
     *
     * @param strNum 待判断的数字参数
     * @return true表示参数为数字，false表示参数非数�?
     */
    public static boolean isNum(final String strNum) {
        return strNum.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static boolean isMac(String str) {
//        String patternMac="^[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}$";
        String patternMac = "[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}";
        String arg1 = "het-";
        String arg2 = "-a-b";
        String pattern = String.format("^%s+%s+%s$", arg1, patternMac, arg2);
//        System.out.println(pattern);
        return str.matches(pattern);
    }



    private static String getMacAddr() {
        String eth0 = null;
        String wlan0 = null;
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        wlan0 = res1.toString().replace(":", "").trim();
                    }
                }

                if (nif.getName().equalsIgnoreCase("eth0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        wlan0 = res1.toString().replace(":", "").trim();
                    }
                }
                /*if (!nif.getName().equalsIgnoreCase("wlan0"))//wlan0
                    continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":", "").trim();*/
            }
        } catch (Exception ex) {
        }
        if (!TextUtils.isEmpty(wlan0)) {
            return wlan0;
        } else {
            return eth0;
        }
    }

    public static void main(String[] args) {
        short ss = 9001;
        byte[] bb = short2bytes(ss & 0xFFFF);

//        System.out.println(toHexString(bb));
    }
}
