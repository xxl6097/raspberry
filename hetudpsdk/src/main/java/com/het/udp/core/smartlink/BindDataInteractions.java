package com.het.udp.core.smartlink;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.het.log.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.DataType;
import com.het.udp.core.Utils.IpUtils;
import com.het.udp.core.observer.IObserver;
import com.het.udp.core.smartlink.bind.OnDeviceDiscoverListener;
import com.het.udp.core.smartlink.bind.OnDeviceRecvSsidListener;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uuxia-mac on 16/7/17.
 * 绑定相关,与设备数据交互管理类
 */
public class BindDataInteractions implements IObserver {

    private final String TAG = "binddata";

    protected Set<String> findDeviceSet = new HashSet<>();

    private OnDeviceDiscoverListener onDeviceDiscoverListener;

    private OnDeviceRecvSsidListener onDeviceRecvSsidListener;

    private String serverIp;

    private short serverPort;

    private String userKey;

    public BindDataInteractions(OnDeviceDiscoverListener onDeviceDiscoverListener) {
        this.onDeviceDiscoverListener = onDeviceDiscoverListener;
    }

    public void setOnDeviceRecvSsidListener(OnDeviceRecvSsidListener onDeviceRecvSsidListener) {
        this.onDeviceRecvSsidListener = onDeviceRecvSsidListener;
    }

    public void startUdpService(Context appContext) throws Exception {
        UdpDataManager.getInstance().init(appContext);
        //注册设备数据监听
        UdpDataManager.registerObserver(this);
    }

    private void stopUdpService(Context appContext){
        try {
            UdpDataManager.getInstance().unBind(appContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**开始配置设备连上服务器**/

    public void startConfigDeviceModule(String ip, short port, String userKey, String deviceIp, boolean isOpenProtocol, int devicePort){
        this.serverIp = ip;
        this.serverPort = port;
        this.userKey = userKey;

        try {
            PacketModel data = packageData(deviceIp, isOpenProtocol);
            UdpDeviceDataBean dm = data.getDeviceInfo();
            if (dm == null) {
                dm = new UdpDeviceDataBean();
            }
            if (devicePort <= 0) {
                dm.setPort(UdpDataManager.mPort);
            }else{
                dm.setPort(devicePort);
            }
            data.setDeviceInfo(dm);
            UdpDataManager.getInstance().send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**停止配置设备**/
    public void stopConfigDeviceModule(Context appContext){
        /**注销设备数据监听**/
        UdpDataManager.unregisterObserver(this);
        /**注销UdpService服务**/
        stopUdpService(appContext);
        Log.e(TAG, "注销UdpService服务");
    }

    @Override
    public void receive(PacketModel o) {
        if (o == null) return;
        if (Contants.OPEN.BIND._HET_OPEN_BIND_RECV_SSIDINFO == o.getCommand() || Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION == o.getCommand() || o.isOpenProtocol()) {
            parsePackets(o.getCommand(), o);
        }
    }

    private void parsePackets(int cmd, PacketModel data) {
        if (data != null) {
            UdpDeviceDataBean mi = data.getDeviceInfo();
            if (mi == null) {
                return;
            }
            if (ByteUtils.isNull(mi.getDeviceMac())) {
                return;
            }

            if (data.isOpenProtocol()) {
                if (cmd == Contants.OPEN.BIND._HET_OPEN_BIND_DISCOVER_DEVICE) {
                    //设备连上路由器应答
                    mi.setOpenProtocol(data.isOpenProtocol());
                    discover(mi);
                }else if (cmd == Contants.OPEN.BIND._HET_OPEN_BIND_RECV_SSIDINFO){
                    //Ap绑定，作为设备收到8100（ssid信息）数据的回复
                    receive8200(mi);
                }
            } else {
                if (cmd == Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION) {
                    //设备连上路由器应答
                    discover(mi);
                }
            }
        }
    }

    /**
     * 扫描后发现设备
     *
     * @param mi
     */
    private void discover(UdpDeviceDataBean mi) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "发现设备:[" + mi.getDeviceMac() + "]");
        UdpDataManager.getInstance().tips("发现设备:[" + mi.getDeviceMac() + "] 大类["+mi.getDeviceType()+ "] 小类["+mi.getDeviceSubType()+"]");
        findDeviceSet.add(mi.getDeviceMac().toUpperCase());
        if (onDeviceDiscoverListener != null) {
            onDeviceDiscoverListener.onDiscover(mi);
        }
    }

    private void receive8200(UdpDeviceDataBean mi) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "收到设备8200数据:[" + mi.getDeviceMac() + "]");
        UdpDataManager.getInstance().tips("收到设备8200数据:[" + mi.getDeviceMac() + "] 大类["+mi.getDeviceType()+ "] 小类["+mi.getDeviceSubType()+"]");
        if (onDeviceRecvSsidListener != null) {
            onDeviceRecvSsidListener.onResult(mi);
        }
    }



    /**
     * 获取绑定设备Ip最后一个字节 例如：192.168.1.123  取123作为一个字节
     */
    private byte getIpLastByte(String ip) {
        if (!ByteUtils.isNull(ip)) {
            if (IpUtils.isIpv4(ip)) {
                byte last = IpUtils.getIpLastByte(ip.trim());
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "待绑定设备IP:" + ip + " lastByte=" + last);
                return last;
            }
        }
        return 0;
    }


    private PacketModel packageData(String ip, boolean isOpenProtocol) throws Exception {
        if (TextUtils.isEmpty(serverIp)) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "serverIp is null or empty");
            throw new Exception("serverIp is null or empty");
        }
        if (serverPort <= 0) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "serverPort is null or empty");
            throw new Exception("serverPort is null or empty");
        }
        if (userKey == null || userKey.length() == 0) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "UserKey is null or size=0");
            throw new Exception("UserKey is null or size=0");
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "bind info ip:" + serverIp + ":" + serverPort + " userKey:" + ByteUtils.toHexStrings(userKey.getBytes()));

        byte[] bodybyte = null;
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setDeviceMac(null);
        if (isOpenProtocol) {
            bodybyte = ByteUtils.getBodyBytesForOpen(serverIp.trim(), serverPort, userKey.getBytes());//"203.195.139.126", "30100"
            udpDeviceDataBean.setPacketStart((byte) 0x5A);
            udpDeviceDataBean.setCommandType(Contants.OPEN.BIND._HET_OPEN_BIND_SEND_SERVERINFO);
        } else {
            bodybyte = ByteUtils.getBodyBytes(serverIp.trim(), serverPort, userKey.getBytes(), getIpLastByte(ip));//"203.195.139.126", "30100"
            udpDeviceDataBean.setPacketStart((byte) 0xF2);
            udpDeviceDataBean.setCommandType(Contants.HET_SMARTLINK_SEND_SERVER_INFO_REQ);
        }
        udpDeviceDataBean.setDataStatus((byte) -128);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答
        int frameNo = 0x00000000;
        frameNo |= (ByteUtils.calcFrameShort() & 0x0000FFFF);
        udpDeviceDataBean.setFrameSN(frameNo);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"uu ###################### 序列号 "+frameNo);
        p.setDeviceInfo(udpDeviceDataBean);
        p.setIp(ip);
        p.setBody(bodybyte);
        PacketUtils.out(p);
        return p;
    }


    private PacketModel package8100Data(String ip, byte[] body) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "package0100Data info :" + ByteUtils.toHexStrings(body));
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setDeviceMac(null);
        udpDeviceDataBean.setPacketStart((byte) 0x5A);
        udpDeviceDataBean.setCommandType(Contants.OPEN.BIND._HET_OPEN_BIND_SEND_SSIDINFO);
        p.setDeviceInfo(udpDeviceDataBean);
        p.setIp(ip);
        p.setBody(body);
        PacketUtils.out(p);
        return p;
    }

    public void send8100Data(String ip, byte[] body){
        try {
            PacketModel data = package8100Data(ip, body);
            UdpDeviceDataBean dm = data.getDeviceInfo();
            if (dm == null) {
                dm = new UdpDeviceDataBean();
            }
            dm.setPort(DataType.HET.getPort());
            data.setDeviceInfo(dm);
            UdpDataManager.getInstance().send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
