package com.het.udp.wifi.model;


import android.text.TextUtils;

import com.het.udp.wifi.utils.ByteUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 与设备交互数据模型
 */
public class UdpDeviceDataBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // 设备类型
    public short deviceType;
    // 设备子类型类型
    public byte deviceSubType;
    // 产品编号
    public byte deviceNumber;
    // 设备mac地址
    public String deviceMac;
    // 设备名称
    public String deviceName;
    // 新协议设备类型
    public byte[] newDeviceType;
    //设备IP地址
    private String ip;
    //设备通讯端口
    private int port;
    //报文起始
    private byte packetStart = (byte) 0XF2;
    //协议类型  0--老协议  1--新协议
    private byte protocolType;
    //协议版本号
    private byte protocolVersion = 0x41;//默认使用旧协议版本
    //控制命令字
    private short commandType;
    //数据状态，
    private byte dataStatus;
    //WIFI信号强度
    private byte wifiStatus;
    //数据帧序号
    private int frameSN;
    //设备编码
    private String deviceId;
    private String uesrKey;
    //客户ID
    private int customerId;
    //设备协议版本
    private Integer dataVersion;
    //开放协议
    private boolean isOpenProtocol;

    private String localServerIp;
    private short localServerPort;
    private byte[] gatewaySignKey;
    private int productId;
    /**
     * 绑定状态
     * 如果收到onBindFinish(HashMap<String, UdpDeviceDataBean> bindMap)回调，且bindMap大小为0，需要调用HTTP接口查询绑定状态，根据最终查询结果判断绑定状态；
     * status=0 绑定成功，直接跳转成功页面即可；
     * status=1 设备连接服务器超时，直接跳转绑定失败界面即可；
     * //status=2 调用HTTP接口查询绑定状态(这个是扩展，暂时没有status=2的)
     */
    private int bindStatus = 1;//默认设置为状态不成功

    private boolean onLine = true;

    //用作心跳
    private long keepaliveTime = 0L;

    //以下定义用作UDP重传机制
    private int source;
    //记录数据帧是否需要做回复
    private boolean needReply;
    //记录数据帧滚动序号
//    private short frameNo;
    //Ip最后一个字节
    private byte endIp;
    //标记此数据是不发数据
    private boolean againData = false;
    //仅仅是控制指令，非查询指令
    private boolean justCtrlData;

    //1-绑定成功  0-未绑定
    private int deviceBindStatus;

    //记录老设备
    private boolean beUseOldUserKey;

    public UdpDeviceDataBean() {
        this.keepaliveTime = System.currentTimeMillis();
    }

//    public byte[] getFrameControl() {
//        return frameControl;
//    }

//    public void setFrameControl(byte[] frameControl) {
//        this.frameControl = frameControl;
//    }


    @Override
    public String toString() {
        return "UdpDeviceDataBean{" +
                "deviceType=" + deviceType +
                ", deviceSubType=" + deviceSubType +
                ", deviceNumber=" + deviceNumber +
                ", deviceMac='" + deviceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", newDeviceType=" + Arrays.toString(newDeviceType) +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", packetStart=" + packetStart +
                ", protocolType=" + protocolType +
                ", protocolVersion=" + protocolVersion +
                ", commandType=" + commandType +
                ", dataStatus=" + dataStatus +
                ", wifiStatus=" + wifiStatus +
                ", frameSN=" + frameSN +
                ", deviceId='" + deviceId + '\'' +
                ", uesrKey='" + uesrKey + '\'' +
                ", customerId=" + customerId +
                ", dataVersion=" + dataVersion +
                ", isOpenProtocol=" + isOpenProtocol +
                ", bindStatus=" + bindStatus +
                ", onLine=" + onLine +
                ", keepaliveTime=" + keepaliveTime +
                ", source=" + source +
                ", needReply=" + needReply +
                ", endIp=" + endIp +
                ", againData=" + againData +
                ", justCtrlData=" + justCtrlData +
                ", deviceBindStatus=" + deviceBindStatus +
                ", beUseOldUserKey=" + beUseOldUserKey +
                '}';
    }

    public byte[] getGatewaySignKey() {
        return gatewaySignKey;
    }

    public void setGatewaySignKey(byte[] gatewaySignKey) {
        this.gatewaySignKey = gatewaySignKey;
    }

    public boolean isBeUseOldUserKey() {
        return beUseOldUserKey;
    }

    public void setBeUseOldUserKey(boolean beUseOldUserKey) {
        this.beUseOldUserKey = beUseOldUserKey;
    }

    public byte[] getDeviceTypeForOpen() {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putInt(customerId);
        b.putShort(deviceType);
        b.put(deviceSubType);
        b.put(deviceNumber);
        b.flip();
        return b.array();
    }

    /**
     * 解析WIFI信号强度
     *
     * @param wifi
     * @return WIFI 信号强度0-10 对应0%-100%
     */
    public static int decodeWifi(byte wifi) {
        int wifiStrange = 0;
        wifiStrange |= wifi & 0x04;
        wifiStrange |= wifi & 0x02;
        wifiStrange |= wifi & 0x01;
        return wifiStrange;
    }

    public void setDeviceMacArray(byte[] macAdddrArray) {
        if (macAdddrArray == null || macAdddrArray.length <= 0)
            return;
        deviceMac = ByteUtils.byteToMac(macAdddrArray);
        //System.out.println("@@@@@@@@@@@@@@@@@ "+deviceMac);
    }

    /**
     * 数据状态
     *
     * @param dataStatus
     */
    public static void decodeDataStatus(byte dataStatus) {
        int bSend = dataStatus >>> 7 & 0x01;//发送数据
        int bRequest = dataStatus >>> 6 & 0x01;//请求数据
        int bResponse = dataStatus >>> 5 & 0x01;//应答数据
        int uninSend = dataStatus >>> 4 & 0x01;//0:数据需要应答 1:数据不用应答【只结合发送数据使用】
    }

    public int getDeviceBindStatus() {
        return deviceBindStatus;
    }

    public void setDeviceBindStatus(int deviceBindStatus) {
        this.deviceBindStatus = deviceBindStatus;
    }

    public String getUesrKey() {
        return uesrKey;
    }

    public void setUesrKey(String uesrKey) {
        this.uesrKey = uesrKey;
    }

    public boolean isOpenProtocol() {
        return isOpenProtocol;
    }

    public void setOpenProtocol(boolean isOpenProtocol) {
        this.isOpenProtocol = isOpenProtocol;
    }

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public byte getPacketStart() {
        return packetStart;
    }

    public void setPacketStart(byte packetStart) {
        this.packetStart = packetStart;
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

    public byte[] getNewDeviceType() {
        return newDeviceType;
    }

    public void setNewDeviceType(byte[] newDeviceType) {
        this.newDeviceType = newDeviceType;
        parseDeviceType();
    }

    public void setNewDeviceTypeForOpen(byte[] newDeviceType) {
        this.newDeviceType = newDeviceType;
        parseDeviceTypeForOpen();
    }

    public int getFrameSN() {
        return frameSN;
    }

    public void setFrameSN(int frameSN) {
        this.frameSN = frameSN;
//        System.out.println("====key=setFrameSN="+frameSN);
//        if (isOpenProtocol) {
//            frameNo = (short) (this.frameSN & 0x0000FFFF);
//        }

//        System.out.println("====key=setFrameSN="+frameSN+"  frameNo="+frameNo);
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    public long getKeepaliveTime() {
        return keepaliveTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(int bindStatus) {
        this.bindStatus = bindStatus;
    }

    public short getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(byte deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public byte getProtocolVersion() {
        if (packetStart == 0x5A) {
            protocolVersion = 64;
        }
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public short getCommandType() {
        return commandType;
    }

    public void setCommandType(short commandType) {
        this.commandType = commandType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public boolean isNeedReply() {
        return needReply;
    }

    public void setNeedReply(boolean needReply) {
        this.needReply = needReply;
    }


    public byte getEndIp() {
        return endIp;
    }

    public void setEndIp(byte endIp) {
        this.endIp = endIp;
    }

    public boolean isAgainData() {
        return againData;
    }

    public void setAgainData(boolean againData) {
        this.againData = againData;
    }

    public boolean isJustCtrlData() {
        return justCtrlData;
    }

    public void setJustCtrlData(boolean justCtrlData) {
        this.justCtrlData = justCtrlData;
    }

    public byte[] getDeviceMacToByte() {
        if (!ByteUtils.isNull(getDeviceMac())) {
            if (ByteUtils.hexStringToBytes(getDeviceMac()) != null && ByteUtils.hexStringToBytes(getDeviceMac()).length == 6)
                return ByteUtils.hexStringToBytes(getDeviceMac());
        }
        return new byte[6];
    }

    public byte[] getDeviceMacArray() {
        if (TextUtils.isEmpty(deviceMac))
            return null;
        byte[] nMac = ByteUtils.hexStringToBytes(deviceMac);
        if (nMac == null)
            return null;
        //System.out.println("@@@@@@@@@@@@@@@@@getDeviceMacArray "+ByteUtils.byteToMac(nMac));
        return nMac;
    }

    private void parseDeviceTypeForOpen() {
        if (newDeviceType != null) {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.put(newDeviceType);
            b.flip();
            customerId = b.getInt();
            deviceType = b.getShort();
            deviceSubType = b.get();
            deviceNumber = b.get();
            dataVersion = Integer.valueOf(deviceNumber);
        }
    }

    private void parseDeviceType() {
        if (newDeviceType != null) {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.put(newDeviceType);
            b.flip();
            customerId = b.getInt();
            deviceType = b.get();
            deviceSubType = b.get();
            dataVersion = Integer.valueOf(b.getShort());
        }
    }

    public String getLocalServerIp() {
        return localServerIp;
    }

    public void setLocalServerIp(String localServerIp) {
        this.localServerIp = localServerIp;
    }

    public short getLocalServerPort() {
        return localServerPort;
    }

    public void setLocalServerPort(short localServerPort) {
        this.localServerPort = localServerPort;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public static void main(String[] args) {
        int f = -65521;
        int frameNo = (short) (f & 0x0000FFFF);
//        System.out.println(frameNo);
    }
}
