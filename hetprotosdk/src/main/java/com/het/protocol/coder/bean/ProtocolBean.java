package com.het.protocol.coder.bean;

import java.io.Serializable;
import java.util.Arrays;

public class ProtocolBean implements Serializable {
    /**
     * 帧开始标志
     */
    private byte head;
    /**
     * 协议版本，详情见版本表(注 1);
     */
    private byte protoVersion;
    /**
     * 协议类型 升级协议为0x10,业务数据 0x00,绑定协议为 0x02;
     */
    private byte protoType;
    /**
     * 相关操作命令字,高字节为数据方向; Bit7~Bit7  数据源：10-服务器 01-手机 00-终端
     */
    private short command;
    /**
     * 客户端 WIFI 模组的 MAC 地址 ACCF233BA86A;
     */
    private String devMacAddr;
    /**
     * 包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】;
     */
    //设备品牌
    private int customerId;
    //设备的类型
    private int devType;
    //小分类
    private int devSubType;
    //数据协议版本
    private int dataVersion;
    /**
     * 高字节数据状态，低字 WIFI 信号强度;(注 2)
     */
    //高字节数据状态
    private int dateState;
    //WIFI信号强度
    private int wifiState;
    //数据帧序号;
    private long FrameSN;
    /**
     * 保留位 8;
     */
    private byte[] reserved;

    /**
     * Frame body 的长度，汉枫模块不超过200 字节;
     */
    private short length;
    /**
     * 帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过 200 字节;
     */
    private byte[] body;
    /**
     * 帧数据段内容 CRC16 校验值(CRC-16/X25 X16+X12+X5+1),包含所有帧数据除外不包含0xF2;
     */
    private byte[] fcs;

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public byte getProtoVersion() {
        return protoVersion;
    }

    public void setProtoVersion(byte protoVersion) {
        this.protoVersion = protoVersion;
    }

    public byte getProtoType() {
        return protoType;
    }

    public void setProtoType(byte protoType) {
        this.protoType = protoType;
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public String getDevMacAddr() {
        return devMacAddr;
    }

    public void setDevMacAddr(String devMacAddr) {
        this.devMacAddr = devMacAddr;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public int getDevSubType() {
        return devSubType;
    }

    public void setDevSubType(int devSubType) {
        this.devSubType = devSubType;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public int getDateState() {
        return dateState;
    }

    public void setDateState(int dateState) {
        this.dateState = dateState;
    }

    public int getWifiState() {
        return wifiState;
    }

    public void setWifiState(int wifiState) {
        this.wifiState = wifiState;
    }

    public long getFrameSN() {
        return FrameSN;
    }

    public void setFrameSN(long frameSN) {
        FrameSN = frameSN;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getFcs() {
        return fcs;
    }

    public void setFcs(byte[] fcs) {
        this.fcs = fcs;
    }

    @Override
    public String toString() {
        return "ProtocolBean{" +
                "head=" + head +
                ", protoVersion=" + protoVersion +
                ", protoType=" + protoType +
                ", command=" + command +
                ", devMacAddr='" + devMacAddr + '\'' +
                ", customerId=" + customerId +
                ", devType=" + devType +
                ", devSubType=" + devSubType +
                ", dataVersion=" + dataVersion +
                ", dateState=" + dateState +
                ", wifiState=" + wifiState +
                ", FrameSN=" + FrameSN +
                ", reserved=" + Arrays.toString(reserved) +
                ", length=" + length +
                ", body=" + Arrays.toString(body) +
                ", fcs=" + Arrays.toString(fcs) +
                '}';
    }
}
