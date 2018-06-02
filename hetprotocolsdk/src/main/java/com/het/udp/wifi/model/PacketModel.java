package com.het.udp.wifi.model;


import com.het.udp.wifi.callback.OnSendListener;
import com.het.udp.wifi.utils.ByteUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by 夏小力 on 2014-27-18.
 */
public class PacketModel implements Serializable {
    private final static int DEFALT_PORT = 18899;
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //报文数据
    private byte[] data;
    //报文体
    private byte[] body;
    //设备控制秘钥
    private byte[] userKey;
    //设备控制标志
    private byte[] updateFlag;
    //设备信息
    private UdpDeviceDataBean deviceInfo;
    //设备Json格式数据
    private String json;
    //开放协议
    private boolean isOpenProtocol;
    //汉枫smartlink绑定数据
    private boolean isSmartlink;
    private OnSendListener onSendListener;

    public PacketModel() {
//        data = new byte[0];
    }

    public static void main(String[] args) {
        PacketModel packetModel = new PacketModel();
        byte[] body = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] data = new byte[]{11, 22, 33, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, 55};
        UdpDeviceDataBean dm = new UdpDeviceDataBean();
        dm.setDeviceMac("uuxia");
        dm.setCommandType((short) 7);
        packetModel.setBody(body);
        packetModel.setData(data);
        packetModel.setDeviceInfo(dm);
//        System.out.println("------------before---" + packetModel.toString());

        try {
            PacketModel copyOne = (PacketModel) packetModel.deepCopy();
//            System.out.println("------------copyyy---" + copyOne.toString());
            byte[] body1 = copyOne.getBody();
            body1[0] = 100;
            UdpDeviceDataBean dd = copyOne.getDeviceInfo();
            dd.setDeviceMac("xiaxia");
            dd.setCommandType((short) 9);
//            System.out.println("------------chcopy---" + copyOne.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OnSendListener getOnSendListener() {
        return onSendListener;
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public boolean isSmartlink() {
        return isSmartlink;
    }

    public void setIsSmartlink(boolean isSmartlink) {
        this.isSmartlink = isSmartlink;
    }

    public boolean isOpenProtocol() {
        return isOpenProtocol;
    }

    public void setOpenProtocol(boolean isOpenProtocol) {
        this.isOpenProtocol = isOpenProtocol;
    }

    /**
     * 获取Mac地址
     *
     * @return
     */
    public String getMacAddr() {
        if (deviceInfo != null) {
            return deviceInfo.getDeviceMac();
        }
        return null;
    }

    public int getPort() {
        if (deviceInfo != null) {
            return deviceInfo.getPort();
        }
        return DEFALT_PORT;
    }

    public void setPort(int port) {
        if (deviceInfo == null) {
            deviceInfo = new UdpDeviceDataBean();
        }
        deviceInfo.setPort(port);
    }

    public String getIp() {
        if (deviceInfo != null) {
            return deviceInfo.getIp();
        }
        return null;
    }

    public void setIp(String ip) {
        if (deviceInfo == null) {
            deviceInfo = new UdpDeviceDataBean();
        }
        deviceInfo.setIp(ip);
    }

    public short getCommand() {
        if (deviceInfo != null) {
            return deviceInfo.getCommandType();
        }
        return 0;
    }

    public void setCommand(short frameControl) {
        if (deviceInfo == null) {
            deviceInfo = new UdpDeviceDataBean();
        }
        deviceInfo.setCommandType(frameControl);
    }

    public short getProtocolVersion() {
        if (deviceInfo != null) {
            return deviceInfo.getProtocolVersion();
        }
        return 0;
    }

    public void setProtocolVersion(byte protocolVersion) {
        if (deviceInfo == null) {
            deviceInfo = new UdpDeviceDataBean();
        }
        deviceInfo.setProtocolVersion(protocolVersion);
    }

    public byte getPacketStart() {
        if (deviceInfo != null) {
            return deviceInfo.getPacketStart();
        }
        return 0;
    }

    public void setPacketStart(byte packetStart) {
        if (deviceInfo == null) {
            deviceInfo = new UdpDeviceDataBean();
        }
        this.deviceInfo.setPacketStart(packetStart);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public UdpDeviceDataBean getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(UdpDeviceDataBean deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getUserKey() {
        return userKey;
    }

    public void setUserKey(byte[] userKey) {
        this.userKey = userKey;
        if (this.userKey != null) {
            if (body == null) {
                body = userKey;
            } else {
                byte[] tmp = new byte[body.length + userKey.length];
                System.arraycopy(userKey, 0, tmp, 0, userKey.length);
                System.arraycopy(body, 0, tmp, userKey.length, body.length);
            }
        }
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "PacketModel{" +
                "data=" + ByteUtils.toHexString(data) +
                ", body=" + ByteUtils.toHexString(body) +
                ", userKey=" + ByteUtils.toHexString(userKey) +
                ", updateFlag=" + ByteUtils.toHexString(updateFlag) +
                ", deviceInfo=" + deviceInfo +
                ", json='" + json + '\'' +
                '}';
    }

    public void resetCommendType() {
        if (data != null) {
            short cmd = 0;
            if (isOpenProtocol) {
                cmd = (short) ByteUtils.getCommandForOpen(data);
            } else {
                cmd = (short) ByteUtils.getCommandNew(data);
            }

            setCommand(cmd);
        }
    }


    public byte[] getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(byte[] updateFlag) {
        this.updateFlag = updateFlag;
    }

    /**
     * 深度拷贝对象
     *
     * @return
     * @throws Exception
     */
    public Object deepCopy() throws Exception {
        // 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(this);

        // 将流序列化成对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        ObjectInputStream ois = new ObjectInputStream(bis);

        return ois.readObject();
    }
}
