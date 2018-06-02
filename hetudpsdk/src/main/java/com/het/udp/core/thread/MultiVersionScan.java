package com.het.udp.core.thread;

import com.het.udp.core.Utils.DeviceBindMap;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.Contants;

/**
 * Created by UUXIA on 2015/6/23.
 * 根据不同协议版本扫描局域网设备
 */
public class MultiVersionScan {
    private ScanThread ver41Thread, ver42Thread;
    private byte[] userKey = null;

    public void setUserKey(byte[] user) {
        init();
        this.userKey = user;
    }

    public void start(byte[] user, UdpDeviceDataBean udpDeviceDataBean) {
        if (udpDeviceDataBean != null && user != null) {
            this.userKey = user;
            int protocolVersion = udpDeviceDataBean.getProtocolVersion() & 0xFF;
            if (0x41 == protocolVersion) {
                if (!ver41Thread.isRunning()) {
                    ver41Thread.setPacket(getVer41data(this.userKey));
                    ver41Thread.start();
                }
                return;
            } else if (0x42 == protocolVersion) {
                int protocolType = udpDeviceDataBean.getProtocolType();
                if (!ver42Thread.isRunning() && protocolType == 0) {
                    ver42Thread.setPacket(getVer42data(this.userKey));
                    ver42Thread.start();
                }
                return;
            }
            //扫描前清空绑定关系列表
            DeviceBindMap.bindDeviceMap.clear();
        }
    }

    public void init() {
        if (ver41Thread == null) {
            ver41Thread = new ScanThread();
        }
        if (ver42Thread == null) {
            ver42Thread = new ScanThread();
        }
    }

    /**
     * v41协议查询设备控制数据包
     *
     * @param key
     * @return
     */
    private PacketModel getVer41data(byte[] key) {
        if (key != null) {
            byte[] userKey = new byte[1 + key.length];
            userKey[0] = 1;
            System.arraycopy(key, 0, userKey, 1, key.length);
            PacketModel p = new PacketModel();
            UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
            udpDeviceDataBean.setProtocolVersion((byte) 0x41);
            udpDeviceDataBean.setCommandType(Contants.HET_LAN_SEND_CONFIG_REQ);//
            p.setDeviceInfo(udpDeviceDataBean);
            p.setUserKey(userKey);
            PacketUtils.out(p);
            return p;
        }
        return null;
    }

    /**
     * v42协议查询设备控制数据包
     *
     * @param key
     * @return
     */
    private PacketModel getVer42data(byte[] key) {
        if (key != null) {
            PacketModel p = new PacketModel();
            UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
            udpDeviceDataBean.setProtocolVersion((byte) 0x42);
            udpDeviceDataBean.setCommandType(Contants.HET_LAN_SEND_CONFIG_REQ);//
            udpDeviceDataBean.setDataStatus((byte) -64);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答 0000
            p.setDeviceInfo(udpDeviceDataBean);
            p.setUserKey(key);
            PacketUtils.out(p);
            return p;
        }
        return null;
    }
}
