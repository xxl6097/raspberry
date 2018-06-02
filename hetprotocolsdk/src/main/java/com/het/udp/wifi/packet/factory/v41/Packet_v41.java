package com.het.udp.wifi.packet.factory.v41;


/**
 * Created by uuxia-mac on 15/6/20.
 * version 41协议版本
 */

import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.factory.IPacketIn;
import com.het.udp.wifi.packet.factory.IPacketOut;
import com.het.udp.wifi.packet.factory.manager.IPacketManager;
import com.het.udp.wifi.utils.Contants;

/**
 * v41版本协议格式
 * -------------------------------------------------------------------------------------------------------------------------
 * | 0xF2 | Protocol Version | Protocol Type | Command Type | Mac Addr | Device Type | Reserved | Length | Frame Body | FCS  |
 * |-------------------------------------------------------------------------------------------------------------------------|
 * | 1byte|       1byte      |     1byte     |      2byte   |   6byte  |     2byte   |   1byte  |  2byte |   Nbyte    | 2byte|
 * -------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 0xF2: 帧开始标志;
 * Protocol Version:协议版本，详情见版本表(注 1);
 * Protocol Type:协议类型 升级协议为0x10,业务数据 0x00,绑定协议为 0x02;
 * Command Type:相关操作命令字,高字节为数据方向; Bit7~Bit7  数据源：10-服务器 01-手机 00-终端
 * Mac Addr:客户端 WIFI 模组的 MAC 地址 ACCF233BA86A;
 * Device Type:包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】;
 * Reserved:保留位;
 * WiFi状态信息
    WiFi状态信息	BIT7	BIT6	BIT5	BIT4	BIT3	BIT2	BIT1	BIT0
               绑定状态	服务器	路由器	WIFI信号强度
             1-绑定成功	1-已连接	1-已连接 预留
              0-未绑定	0-未连接	0-未连接
 * Length:Frame body 的长度，汉枫模块不超过200 字节;
 * Frame Body:帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过 200 字节;
 * FCS:帧数据段内容 CRC16 校验值(CRC-16/X25 X16+X12+X5+1),包含所有帧数据除外不包含0xF2;
 */
public class Packet_v41 implements IPacketManager {
    //标注此协议版本
    public final static int PROTOCOL_VERSION = 0x41;
    /**
     * 报文起始
     */
    public static final byte packetStart = (byte) 0XF2;
    /**
     * 软件版本，详情见版本表(注1)
     */
    protected byte protocolVersion = 0x41;
    /**
     * 协议类型，该协议值为0x10.
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
     * 设备的类型0811
     */
    protected byte[] deviceType = new byte[]{0x00, 0x00};
    /**
     * 保留位
     */
    protected byte reserved = 0x00;
    /**
     * Frame body的长度
     */
    protected short dataLen = 0x00;
    /**
     * 帧数据段字节数，合法值范围：0～1024(注2)
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


    protected Packet_v41() {
    }

    public Packet_v41(PacketModel packetModel) {
        this.packetModel = packetModel;
    }

    public byte[] getDeviceType() {
        return deviceType;
    }

    public byte getProtocolType() {
        return protocolType;
    }

    public short getCommandType() {
        return commandType;
    }


    public byte[] getFrameBody() {
        return frameBody;
    }

    public void setFrameBody(byte[] frameBody) {
        this.frameBody = frameBody;
    }

    public int getLength(int bodyLen) {
        return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER + bodyLen;
    }

    public short getDataLen() {
        return dataLen;
    }

    public void setDataLen(short dataLen) {
        this.dataLen = dataLen;
    }

    public byte[] getMacAddr() {
        return macAddr;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public IPacketIn createIn() {
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version41 protocol packetIn...");
        return new InPacket_v41(packetModel);
    }

    @Override
    public IPacketOut createOut() {
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"create version41 protocol packetOut...");
        return new OutPacket_v41(packetModel);
    }
}
