package com.het.udp.core.smartlink;


import android.content.Context;
import android.text.TextUtils;

import com.het.log.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.DeviceBindMap;
import com.het.udp.core.Utils.IpUtils;
import com.het.udp.core.smartlink.bind.IBindListener;
import com.het.udp.core.smartlink.bind.IScanListener;
import com.het.udp.core.smartlink.callback.OnDiffComplayEvents;
import com.het.udp.core.smartlink.ti.TiManager;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SmartLinkManipualtor extends SmartLinkBindBase {
    public static boolean bindTi = false;
    private static SmartLinkManipualtor instance = null;
    //记录设备IP
    private Set<Byte> lastIpByte = new HashSet<Byte>();
    private byte protocolVersion = 65;
    private String serverIp, serverPort;
    private byte[] key;

    /**
     * 存储连接设备信息*
     */
    private HashMap<String, UdpDeviceDataBean> bindDeviceMap = new HashMap<String, UdpDeviceDataBean>();
    /**
     * 向设备发送服务器IP、port、key
     */
    private boolean bSendSerInfo = true;
    private Thread sendSerThread = null;
    /**
     * 发指令退出路由
     */
    private boolean bExit = true;
    private int nCount = 0;

    public SmartLinkManipualtor() {
        super();
//		UdpDataManager.registerObserver(this);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "实例化SmartLinkManipualtor");
    }

    private SmartLinkManipualtor(Context c, OnDiffComplayEvents events) {
        this();
        mContext = c;
        /////////TI绑定////////////
        this.diffComplayEvents = events;
        if (diffComplayEvents != null) {
            if (diffComplayEvents.getType() == OnDiffComplayEvents.ACCESS_ROUTER_STYLE_TI) {
                tiManager = new TiManager(c);
            }
        } else {
            if (bindTi) {
                tiManager = new TiManager(c);
            }
        }
        /////////TI绑定////////////
        if (c != null) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "实例化（Context）" + c.toString());
        }

        //        this.broadCastIP = IpUtils.getBroadcastAddress(c);
        //        Logc.i(Logc.HetReportTag.DEVICE_BIND_ERROR, "广播地址:"+broadCastIP);
    }

    /**
     * 获取单独实例
     *
     * @param c
     * @return
     */
    public static SmartLinkManipualtor getInstence(Context c) {
        if (instance == null) {
            instance = new SmartLinkManipualtor(c, null);
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "实例化 getInstence（Context）");
        return instance;
    }

    public static SmartLinkManipualtor getInstence(Context c, OnDiffComplayEvents events) {
        if (instance == null) {
            instance = new SmartLinkManipualtor(c, events);
        } else {
            if (instance.diffComplayEvents != null && instance.diffComplayEvents.equals(events)) {
                return instance;
            } else {
                instance = new SmartLinkManipualtor(c, events);
            }
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "实例化 getInstence（）");
        return instance;
    }


    /**
     * 开始绑定
     */
    public void bind() throws Exception {
        if (selectSets.size() > 0) {
            /////////TI绑定////////////
            if (tiManager != null) {
                tiManager.stopSmartConfig();
            }
            /////////TI绑定////////////
            //            byte[] sendByts = potServerInfoBytes();
            potServerInfoBytes();
            bIsBinding = true;
            bScanning = false;
            //            sendSerInfo2Device(sendByts);
            sendSerInfo2Device();
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "开始绑定设备..." + selectSets.keySet().toString());
        } else {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "未添加绑定设备...");
            throw new Exception("请选择设备...");
        }
    }

    /**
     * 开始扫描
     */
    public void scan() {
        //扫描状态变量
        bScanning = true;
        //true:接收0x0010
        bIsBinding = false;
        //true:广播ssid密码
        isConnecting = true;
        //存储发现的设备，用作过滤0x0001
        findDeviceSet.clear();
        //保存发送给设备的0x4010数据
        multiVersionServerData.clear();
        //保存设备IP
        lastIpByte.clear();
        //存储待绑定设备Set集合
        selectSets.clear();
        //可以理解为绑定成功设备集合,用作过滤0x0010
        deviceSet.clear();
        //保存成功绑定的设备
        bindDeviceMap.clear();
        //用table保存发现的设备
        findDeviceTable.clear();
        //保存发送0x4010数据
        multiVersionServerData.clear();

        //注册数据回调
        UdpDataManager.registerObserver(this);

        startConfig();
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "开始扫描...");
    }

    public void bind(String sIp, String sPort, IBindListener onBindListener) throws Exception {
        this.serverIp = sIp;
        this.serverPort = sPort;
        bind(onBindListener);
    }

    public void bind(IBindListener onBindListener) throws Exception {
        if (onBindListener != null) {
            this.onBindListener = onBindListener;
            bind();
        } else {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "未设置绑定回调函数...");
            throw new Exception("未设置绑定回调函数...");
        }
    }

    public void stopBind() {
//		this.onScanListener = null;
//		this.onBindListener = null;
        //true:接收0x0010
        bIsBinding = false;
        //true:广播ssid密码
        isConnecting = false;
        bSendSerInfo = false;
        bScanning = true;

        //注销数据回调函数
        UdpDataManager.getInstance().unregisterObserver(this);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "停止绑定...");
    }

    public void scan(String ssidPwd, IScanListener onScanListener) {
        setSsid_pwd(ssidPwd);
        scan(onScanListener);
    }

    public void scan(IScanListener onScanListener) {
        this.onScanListener = onScanListener;
        scan();
    }

    /**
     * 注销资源
     */
    public void release() {
        if (instance == null)
            return;
        stopSendSerInfo2Device();
        isConnecting = false;
        bScanning = true;
        selectSets.clear();
        findDeviceSet.clear();
        deviceSet.clear();
        bindDeviceMap.clear();
        findDeviceTable.clear();
        lastIpByte.clear();
        multiVersionServerData.clear();
        UdpDataManager.getInstance().unregisterObserver(this);
        /////////TI绑定////////////
        if (tiManager != null) {
            tiManager.stopSmartConfig();
        }
        /////////TI绑定////////////
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "注销绑定资源...");
    }

    /**
     * 添加选择的设备
     *
     * @param dm
     */
    public void addSelcet(UdpDeviceDataBean dm) {
        if (dm != null && !ByteUtils.isNull(dm.getDeviceMac())) {
            selectSets.put(dm.getDeviceMac().toUpperCase(), dm);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "将(" + dm.getDeviceMac() + ")添加至(待绑定)列表");
        }
    }

    public void addSelcet(String macAddr) throws Exception {
        if (!ByteUtils.isNull(macAddr)) {
            UdpDeviceDataBean dm = findDeviceTable.get(macAddr.toUpperCase());
            selectSets.put(macAddr.toUpperCase(), dm);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "将(" + dm.getDeviceMac() + ")添加至(待绑定)列表");
        }
    }

    /**
     * 移除选的设备
     *
     * @param dm
     */
    public void deleteSelcet(UdpDeviceDataBean dm) {
        if (dm != null && !ByteUtils.isNull(dm.getDeviceMac())) {
            selectSets.remove(dm.getDeviceMac().toUpperCase());
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "移除(待绑定)设备=" + dm.toString());
        }
    }

    public void deleteSelcet(String macAddr) throws Exception {
        if (!ByteUtils.isNull(macAddr)) {
            UdpDeviceDataBean dm = findDeviceTable.get(macAddr.toUpperCase());

            selectSets.remove(dm.getDeviceMac().toUpperCase());
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "移除(待绑定)设备=" + dm.toString());
        }
    }

    public Set<String> getSelectSet() {
        return selectSets.keySet();
    }

    public HashMap<String, UdpDeviceDataBean> getSelectlist() {
        return selectSets;
    }

    public void setOnScanListener(IScanListener onScanListener) {
        this.onScanListener = onScanListener;
    }

    public void setOnBindListener(IBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    public void setSsid_pwd(String ssid_pwd) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "设置路由器密码:" + ssid_pwd);
        this.ssid_pwd = ssid_pwd;
        /////////TI绑定////////////
        if (tiManager != null) {
            tiManager.setPassKey(ssid_pwd);
        }
        /////////TI绑定////////////
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * 在smartlink模式下发送服务器信息给设备
     *
     * @throws Exception
     */
    private void sendSerInfo2Device() throws Exception {
        if (multiVersionServerData != null && multiVersionServerData.size() > 0) {
            startSendSerInfoToDevice();
        } else {
            stopSendSerInfo2Device();
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "发送服务器信息数据包为空");
            throw new Exception("发送服务器信息数据包为空");
        }
    }

    /**
     * 停止向设备发送服务器IP、port、key
     */
    private void stopSendSerInfo2Device() {
        bSendSerInfo = false;
        //        timeCount = 0;
        sendSerThread = null;
    }

    private void startSendSerInfoToDevice(/*final byte[] sendBytes*/) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "开辟线程给设备发送认证服务器IP和端口 " + serverIp + ":" + serverPort);
        sendSerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isConnecting = false;
                float timeCount = 0;
                try {
                    while (bSendSerInfo) {
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "第 " + (int) timeCount + " 次发送认证服务器IP和端口:" + serverIp + ":" + serverPort);
                        Iterator<PacketModel> it = multiVersionServerData.iterator();
                        while (it.hasNext()) {
                            PacketModel data = it.next();
                            if (data == null)
                                continue;
//                            PacketModel p = new PacketModel();
//                            p.setData(data);
                            UdpDeviceDataBean dm = data.getDeviceInfo();
                            if (dm == null) {
                                dm = new UdpDeviceDataBean();
                            }
                            dm.setPort(trasPort);
                            data.setDeviceInfo(dm);
                            UdpDataManager.getInstance().send(data);
                            Thread.sleep(10);
                        }

                        Thread.sleep(1000);
                        timeCount++;
                        if (onBindListener != null) {
                            //                            float per = (timeCount* 1.0f) / BINDTIMEOUT * 100f ;
                            onBindListener.onBindProgress((int) timeCount);
                        }

                        if (timeCount >= BINDTIMEOUT) {
                            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "发送认证服务器IP和端口.超时...");
                            if (onBindListener != null) {
                                beOffRouter();
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "发送认证服务器IP和端口.异常..." + e.getMessage());
                    e.printStackTrace();
                    if (onBindListener != null) {
                        beOffRouter();
                    }
                }
            }
        });

        if (multiVersionServerData != null && multiVersionServerData.size() > 0) {
            bSendSerInfo = true;
            sendSerThread.setName("sendServerInfo");
            sendSerThread.start();
        }
    }

    private void beOffRouter() {
        bExit = true;
        Thread beOffThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (bExit) {
                        send4020();
                        Thread.sleep(1000);
                        nCount++;
                        if (nCount >= 3) {
                            bExit = false;
                            nCount = 0;
                            break;
                        }
                    }
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "发出退出路由指令给设备...");
                    if (onBindListener != null/* && bindDeviceMap.size() > 0*/) {
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "绑定结束...");
                        onBindListener.onBindFinish(bindDeviceMap);
                    }
                    if (onBindListener != null) {
                        onBindListener.onBindProgress(100);
                    }

                } catch (Exception e) {
                    Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "发出退出路由指令.异常..." + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        beOffThread.setName("beOffRouter");
        beOffThread.start();
    }


    private PacketModel potServerInfoBytes(UdpDeviceDataBean dm) throws Exception {
        if (TextUtils.isEmpty(serverIp)) {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "serverIp is null or empty");
            throw new Exception("serverIp is null or empty");
        }
        if (TextUtils.isEmpty(serverPort)) {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "serverPort is null or empty");
            throw new Exception("serverPort is null or empty");
        }
        if (key == null || key.length == 0) {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "UserKey is null or size=0");
            throw new Exception("UserKey is null or size=0");
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "bind info ip:" + serverIp + ":" +serverPort + " userKey:" + ByteUtils.toHexStrings(key));
        if (!ByteUtils.isNull(serverIp) && !ByteUtils.isNull(serverPort) && key != null && key.length > 0) {

            byte[] bodybyte = null;
            if (dm.isOpenProtocol()) {
                bodybyte = ByteUtils.getBodyBytesForOpen(serverIp.trim(), serverPort, key);//"203.195.139.126", "30100"
            } else {
                bodybyte = ByteUtils.getBodyBytes(serverIp.trim(), serverPort, key, getIps());//"203.195.139.126", "30100"
            }

            PacketModel p = new PacketModel();
            UdpDeviceDataBean udpDeviceDataBean = dm;
            if (udpDeviceDataBean == null) {
                udpDeviceDataBean = new UdpDeviceDataBean();
            }
            udpDeviceDataBean.setDeviceMac(null);
            if (dm.isOpenProtocol()) {
                udpDeviceDataBean.setPacketStart((byte) 0x5A);
                udpDeviceDataBean.setCommandType(Contants.OPEN.BIND._HET_OPEN_BIND_SEND_SERVERINFO);
            } else {
                udpDeviceDataBean.setPacketStart((byte) 0xF2);
                udpDeviceDataBean.setCommandType(Contants.HET_SMARTLINK_SEND_SERVER_INFO_REQ);
            }
            udpDeviceDataBean.setDataStatus((byte) -128);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答
            p.setDeviceInfo(udpDeviceDataBean);
            p.setBody(bodybyte);
            PacketUtils.out(p);
//            return PacketUtils.out(p);
            return p;
        }
        return null;
    }

    /**
     * 获取多版本协议
     */
    private void potServerInfoBytes() throws Exception {
        if (selectSets != null && selectSets.size() > 0) {
            Iterator<String> it = selectSets.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                UdpDeviceDataBean value = selectSets.get(key.toUpperCase());
                UdpDeviceDataBean find = findDeviceTable.get(key.toUpperCase());
                value.setOpenProtocol(find == null ? false : find.isOpenProtocol());
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "设备IP:" + value.toString());
                PacketModel data = potServerInfoBytes(value);
                if (data != null) {
                    multiVersionServerData.add(data);
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "封装4010:" + data.toString());
                }
            }
        }

    }

    /**
     * 获取绑定设备Ip最后一个字节 例如：192.168.1.123  取123作为一个字节
     */
    private void getIPByteArry() {
        if (selectSets != null && selectSets.size() > 0) {
            Iterator<String> it = selectSets.keySet().iterator();
            while (it.hasNext()) {
                UdpDeviceDataBean dm = selectSets.get(it.next());
                if (dm != null /*&& dm.isbCheck()*/ && !ByteUtils.isNull(dm.getIp())) {
                    String ip = dm.getIp().trim();
                    if (IpUtils.isIpv4(ip)) {
                        protocolVersion = dm.getProtocolVersion();
                        byte last = IpUtils.getIpLastByte(ip.trim());
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "待绑定设备IP:" + ip + " lastByte=" + last);
                        if (last == 0 || last == -1)
                            continue;
                        lastIpByte.add(last);
                    }
                }
            }
        }
    }

    private byte[] getIps() throws Exception {
        getIPByteArry();
        byte[] ips = null;
        if (lastIpByte != null && lastIpByte.size() > 0) {
            ips = new byte[lastIpByte.size()];
            Iterator<Byte> it = lastIpByte.iterator();
            int i = 0;
            while (it.hasNext()) {
                ips[i] = it.next();
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "待绑定设备IP地址:" + ips[i]);
            }
        }
        if (ips == null) {
            Logc.e(Logc.HetLogRecordTag.DEVICE_BIND_ERROR, "未获取到绑定设备IP，或者没有添加绑定设备");
            throw new Exception("未获取到绑定设备IP，或者没有添加绑定设备");
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "待绑定设备列表:" + selectSets.toString());
        return ips;
    }

    @Override
    public void receive(PacketModel o) {
        if (o == null) return;
        if (Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION == o.getCommand() || Contants.HET_SMARTLINK_SEND_SERVER_INFO_RES == o.getCommand() || o.isOpenProtocol()) {
            parsePackets(o.getCommand(), o);
        }
    }


    private void parsePackets(int cmd, PacketModel data) {
        if (data != null) {
            UdpDeviceDataBean mi = data.getDeviceInfo();
            if (mi == null) {
                return;
            }
            //绑定类型协议数据包
            //            if (mi.getProtocolType() != 2) {
            //                return;
            //            }
            if (ByteUtils.isNull(mi.getDeviceMac())) {
                return;
            }

            if (data.isOpenProtocol()) {
                if (cmd == Contants.OPEN.BIND._HET_OPEN_BIND_DISCOVER_DEVICE) {
                    //TODO 设备连上路由器应答
                    mi.setOpenProtocol(data.isOpenProtocol());
                    discover(mi);
                }
            } else {
                if (cmd == Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION) {
                    //TODO 设备连上路由器应答
                    discover(mi);
                } else if (cmd == Contants.HET_SMARTLINK_SEND_SERVER_INFO_RES) {
                    //TODO SmartLink绑定应答
                    replyForServerConfig(mi, data);
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
//        if (!findDeviceSet.contains(mi.getDeviceMac().toUpperCase()) && bScanning) {
        findDeviceSet.add(mi.getDeviceMac().toUpperCase());
        findDeviceTable.put(mi.getDeviceMac().toUpperCase(), mi);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "将[" + mi.getDeviceMac() + "]添加至(发现)设备列表");
        if (onScanListener != null) {
            onScanListener.onFind(mi);
        }
//        }
    }

    private void replyForServerConfig(UdpDeviceDataBean mi, PacketModel data) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "收到设备回复0x0010:[" + mi.getDeviceMac() + "] 状态机变量=" + bIsBinding);
        if (bIsBinding) {
            String macAddr = mi.getDeviceMac().toUpperCase();
            if (selectSets.containsKey(macAddr)) {
                if (findDeviceSet.contains(macAddr)) {   //判断该设备是否存在于扫描到设备集合里面
                    if (!deviceSet.contains(macAddr)) {
                        deviceSet.add(macAddr);
                        if (data.getBody() != null && data.getBody().length > 0) {
                            byte flag = data.getBody()[0];//frameBody总共8字节，暂用第一个字节  为0表示绑定成功，为1表示绑定超时
                            mi.setBindStatus(flag);
                        }
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "成功绑定一台设备:[" + mi.getDeviceMac() + "]");
                        bindDeviceMap.put(macAddr, mi);
                        DeviceBindMap.bindDeviceMap.putAll(bindDeviceMap);
                        DeviceBindMap.runJudgeBindStatus.putAll(bindDeviceMap);
                        if (selectSets.size() == bindDeviceMap.size()) {//如果选择的待绑定设备数量与已经绑定完成数量相等，则立即返回绑定完成。
//                            stopSendSerInfo2Device();
//                            beOffRouter();
                        } else {
                            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "(待绑定)设备列表size=" + selectSets.keySet().toString() + " (成功绑定)设备列表size=" + bindDeviceMap.keySet().toString());
                        }
                    }
                } else {
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "收到设备回复0x0010:" + mi.getDeviceMac() + " 该设备不在(发现)设备列表:" + findDeviceSet.toString());
                }
            } else {
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "收到设备回复0x0010:" + mi.getDeviceMac() + " 该设备不在(待绑定)设备列表:" + selectSets.keySet().toString());
            }
        }
    }
}
