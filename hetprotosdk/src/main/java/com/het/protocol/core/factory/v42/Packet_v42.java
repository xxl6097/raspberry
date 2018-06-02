package com.het.protocol.core.factory.v42;



import com.het.protocol.bean.PacketBean;
import com.het.protocol.core.factory.IPacketIn;
import com.het.protocol.core.factory.IPacketOut;
import com.het.protocol.core.factory.manager.IPacketManager;
import com.het.protocol.util.Contants;

import java.util.Arrays;

/**
 * Created by UUXIA on 2015/6/19.
 * version 42协议版本
 */

/**
 * v42版本协议格式
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * | 0xF2 | Protocol Version | Protocol Type | Command Type | Mac Addr | Device Type | Frame Control & WIFI status | Frame SN | Reserved | Length | Frame Body | Frame Body FCS | FCS  |
 * |-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
 * | 1byte|       1byte      |     1byte     |      2byte   |   6byte  |     8byte   |               2byte         |   4byte  |   8byte  |  2byte |   Nbyte    |       2byte    | 2byte|
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 0xF2: 帧开始标志;
 * Protocol Version:协议版本，详情见版本表(注 1);
 * Protocol Type:协议类型 升级协议为0x10,业务数据 0x00,绑定协议为 0x02;
 * Command Type:相关操作命令字,高字节为数据方向; Bit7~Bit7  数据源：10-服务器 01-手机 00-终端
 * Mac Addr:客户端 WIFI 模组的 MAC 地址 ACCF233BA86A;
 * Device Type:包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】;
 * Frame Control & WIFI status:高字节数据状态，低字 WIFI 信号强度;(注 2)
 * Frame SN:数据帧序号;
 * Reserved:保留位;
 * Length:Frame body 的长度，汉枫模块不超过200 字节;
 * Frame Body:帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过 200 字节;
 * Frame Body FCS:Frame Body 部分未加密前的 FCS,不加密的数据填 0x0000;
 * FCS:帧数据段内容 CRC16 校验值(CRC-16/X25 X16+X12+X5+1),包含所有帧数据除外不包含0xF2;
 * <p/>
 * <p/>
 * 注1：协议版本号这里包括主协议版本和子协议版本。详情如下表 4-2。
 * 表 4-2 协议版本说明
 * --------------------------------------------------------------
 * |      类型        |      数据值       |         类型描述        |
 * |--------------------------------------------------------------|
 * |                 |       00         |       老版本协议         |
 * |                 |--------------------------------------------|
 * | 主版本号(b7b6)   |       11         |       新版本协议          |
 * |                 |--------------------------------------------|
 * |                 |     10~11        |          预留            |
 * |--------------------------------------------------------------|
 * |    子协议版本号   |     000~111      |      根据具体子协议而定    |
 * | （b5b4b3.b2b1b0) |                  |                        |
 * --------------------------------------------------------------
 * <p/>
 * 注2：数据状态（如下表 4-3）和WiFi状态(如下表 4-4)；
 * <p/>
 * 表 4-3 数据状态
 * ------------------------------------------------------------------------------------------------------------------------------------------------
 * |         数据及数据状态          |      Bit7    |    Bit6    |    Bit5      |                 Bit4                               |   Bit3~Bit0    |
 * |------------------------------------------------------------------------------------------------------------------------------------------------|
 * |   Bit4~Bit7标示当前数据帧的性质 |   1:发送数据 |  1:请求数据 |   1:应答数据 |   0：数据需要应答；1：数据不用应答【只结合发送数据使用】   |    保留        |
 * ------------------------------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 表 4-4 WIFI状态
 * ------------------------------------------------------------------------------------------------------------------------
 * |         WIFI状态      |      Bit7     |      Bit6     |       Bit5     |        Bit4      |          Bit3~Bit0          |
 * ------------------------------------------------------------------------------------------------------------------------
 * |   Bit0~Bit3信号强度   |     保留      |      保留     |       保留     |       保留       |  WIFI信号强度0~10对应0%~100%  |
 * ------------------------------------------------------------------------------------------------------------------------
 */
public class Packet_v42 implements IPacketManager {
    //标注此协议版本
    public final static int PROTOCOL_VERSION = 0x42;

    /**
     * 报文起始
     */
    protected static final byte packetStart = (byte) 0XF2;
    /**
     * 软件版本，详情见版本表(注1)
     */
    protected byte protocolVersion = 0x42;
    /**
     * 协议类型，升级协议为0x10,业务数据0x00,绑定协议为0x02
     */
    protected byte protocolType = 0x02;
    /**
     * 相关操作命令字,高字节为数据方向
     */
    protected short commandType = 0x00;
    /**
     * 上行账号字节组（设备MAC地址)
     */
    protected byte[] macAddr = new byte[6];
    /**
     * 包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】
     */
    protected byte[] deviceType = new byte[8];
    /**
     * 高字节数据状态，低字节WIFI信号强度
     */
//    protected byte[] frameControl = new byte[2];
    protected byte dataStatus;
    //WIFI信号强度
    protected byte wifiStatus;
    /**
     * 数据帧序号
     */
    protected int frameSN = 0x00;
    /**
     * 保留位
     */
    protected byte[] reserved = new byte[8];
    /**
     * Frame body的长度
     */
    protected short dataLen = 0x00;
    /**
     * 帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过200 字节
     */
    protected byte[] frameBody;
    /**
     * Frame Body 部分未加密前的FCS
     */
    protected byte[] frameBodyCrc = new byte[]{0x00, 0x00};
    /**
     * 帧数据段内容CRC16校验值(CRC-16/X25 X16+X12+X5+1)
     */
    protected byte[] fcs = new byte[]{0x00, 0x00};


    /**
     * 报文模型
     */
    protected PacketBean packetBean;

    public Packet_v42(PacketBean packetBean) {
        this.packetBean = packetBean;
    }

    protected Packet_v42() {
    }

    public static byte getPacketStart() {
        return packetStart;
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

    public short getCommandType() {
        return commandType;
    }

    public void setCommandType(short commandType) {
        this.commandType = commandType;
    }

    public byte[] getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(byte[] macAddr) {
        this.macAddr = macAddr;
    }

    public byte[] getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte[] deviceType) {
        this.deviceType = deviceType;
    }

//    public byte[] getFrameControl() {
//        return frameControl;
//    }

//    public void setFrameControl(byte[] frameControl) {
//        this.frameControl = frameControl;
//    }

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

    public short getDataLen() {
        return dataLen;
    }

    public void setDataLen(short dataLen) {
        this.dataLen = dataLen;
    }

    public byte[] getFrameBody() {
        return frameBody;
    }

    public void setFrameBody(byte[] frameBody) {
        this.frameBody = frameBody;
    }

    public byte[] getFrameBodyCrc() {
        return frameBodyCrc;
    }

    public void setFrameBodyCrc(byte[] frameBodyCrc) {
        this.frameBodyCrc = frameBodyCrc;
    }

    public byte[] getFcs() {
        return fcs;
    }

    public void setFcs(byte[] fcs) {
        this.fcs = fcs;
    }

    public PacketBean getPacketBean() {
        return packetBean;
    }

    public void setPacketBean(PacketBean packetBean) {
        this.packetBean = packetBean;
    }

    public byte getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(byte dataStatus) {
        this.dataStatus = dataStatus;
    }

    public byte getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(byte wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    @Override
    public String toString() {
        return "Packet_v42{" +
                "protocolVersion=" + protocolVersion +
                ", protocolType=" + protocolType +
                ", commandType=" + commandType +
                ", macAddr=" + Arrays.toString(macAddr) +
                ", deviceType=" + Arrays.toString(deviceType) +
                ", dataStatus=" + dataStatus +
                ", wifiStatus=" + wifiStatus +
                ", frameSN=" + frameSN +
                ", reserved=" + Arrays.toString(reserved) +
                ", dataLen=" + dataLen +
                ", frameBody=" + Arrays.toString(frameBody) +
                ", frameBodyCrc=" + Arrays.toString(frameBodyCrc) +
                ", fcs=" + Arrays.toString(fcs) +
                ", packetBean=" + packetBean +
                '}';
    }

    public int getLength(int bodyLen) {
        return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42 + bodyLen;
    }

    @Override
    public IPacketIn createIn() {
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version42 protocol packetIn...");
        return new InPacket_v42(packetBean);
    }

    @Override
    public IPacketOut createOut() {
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version42 protocol packetOut...");
        return new OutPacket_v42(packetBean);
    }
}
