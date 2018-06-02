package com.het.udp.wifi.packet.factory.vopen;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.IPacketOut;
import com.het.udp.wifi.packet.factory.manager.IPacketManager;
import com.het.udp.wifi.utils.Contants;

/**
 * Created by UUXIA on 2015/6/24.
 */

/**
 * --------------------------------------------------------------------------------------------------------
 * | 0x5A | 数据长度 | 框架版本 | 协议类型 | 设备编码 | Mac地址 | 数据帧序列号 | 保留字 | 数据类型 | 数据内容| 检验码FCS  |
 * |-------------------------------------------------------------------------------------------------------
 * | 1byte|  2byte |  1byte |   1byte |  8byte |  6byte  |   4byte   | 8byte |   2byte | nbyte|2byte
 * ------------------------------------------------------------------------------------------
 */
public class Packet_open implements IPacketManager {

    /**
     * 报文起始
     */
    public static final byte packetStart = (byte) 0x5A;
    /**
     * 数据长度
     */
    protected short dataLen = 0x00;
    /**
     * 框架版本 b7b6:主版本  b5b4b3b2b1b0:子版本
     */
    protected byte protocolVersion = 0x40;
    /**
     * 协议类型  0x00:ID认证  0x01:业务数据
     */
    protected byte protocolType = 0x01;
    /**
     * 设备编码 在开发前申请
     */
    protected byte[] deviceId = new byte[8];
    /**
     * MAC 地址或USERID 客户端WIFI 模组的MAC 地址或服务器分配的唯一USERID  例如：ACCF233BA86A
     */
    protected byte[] macAddr = new byte[6];
    /**
     * 数据帧序号
     */
    protected int frameSN = 0x00;
    /**
     * 保留位
     */
    protected byte[] reserved = new byte[8];
    /**
     * 数据类型 数据类型及操作标识  例如：0x0104  相关操作命令字
     */
    protected short commandType = 0x00;
    /**
     * 帧数据段字节数，数据内容，长度N =< 200
     */
    protected byte[] frameBody;
    /**
     * 帧数据段内容CRC16校验值(CRC-16/X25 X16+X12+X5+1)
     */
    protected byte[] fcs = new byte[]{0x00, 0x00};

    /**
     * 报文模型
     */
    protected PacketModel packetModel;

    public Packet_open(PacketModel packetModel) {
        this.packetModel = packetModel;
    }

    public static byte getPacketStart() {
        return packetStart;
    }

    public short getDataLen() {
        return dataLen;
    }

    public void setDataLen(byte dataLen) {
        this.dataLen = dataLen;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public byte getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public byte[] getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(byte[] deviceId) {
        this.deviceId = deviceId;
    }

    public byte[] getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(byte[] macAddr) {
        this.macAddr = macAddr;
    }

    public int getFrameSN() {
        return frameSN;
    }

    public void setFrameSN(int frameSN) {
        this.frameSN = frameSN;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }

    public short getCommandType() {
        return commandType;
    }

    public void setCommandType(short commandType) {
        this.commandType = commandType;
    }

    public byte[] getFrameBody() {
        return frameBody;
    }

    public void setFrameBody(byte[] frameBody) {
        this.frameBody = frameBody;
    }

    public byte[] getFcs() {
        return fcs;
    }

    public void setFcs(byte[] fcs) {
        this.fcs = fcs;
    }

    public PacketModel getPacketModel() {
        return packetModel;
    }

    public void setPacketModel(PacketModel packetModel) {
        this.packetModel = packetModel;
    }


    public int getLength(int bodyLen) {
        return Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN + bodyLen;
    }

    @Override
    public IPacketIn createIn() {
//        Logc.i(Logc.HetReportTag.INFO_WIFI,"create version_open protocol packetIn...");
        return new InPacket_open(packetModel);
    }

    @Override
    public IPacketOut createOut() {
//        Logc.i(Logc.HetReportTag.INFO_WIFI,"create version_open protocol packetOut...");
        return new OutPacket_open(packetModel);
    }
}
