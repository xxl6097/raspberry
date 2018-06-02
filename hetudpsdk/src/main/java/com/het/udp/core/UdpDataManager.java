package com.het.udp.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.het.log.Logc;
import com.het.udp.core.Utils.DataType;
import com.het.udp.core.Utils.DeviceBindMap;
import com.het.udp.core.Utils.IpUtils;
import com.het.udp.core.Utils.SystemUtils;
import com.het.udp.core.broadcast.NetWorkBroadcast;
import com.het.udp.core.keepalive.OnDeviceOnlineListener;
import com.het.udp.core.keepalive.impl.KeepAliveManager;
import com.het.udp.core.observer.IObserver;
import com.het.udp.core.observer.Observable;
import com.het.udp.core.thread.MultiVersionScan;
import com.het.udp.core.thread.ProtocolVersionThread;
import com.het.udp.wifi.callback.ILogMessage;
import com.het.udp.wifi.callback.OnSendListener;
import com.het.udp.wifi.core.ReplyManager;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketParseException;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.packet.factory.vopen.GenerateOpenPacket;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;
import com.het.udp.wifi.utils.Prefers;
import com.het.xml.protocol.ProtocolManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by uuxia-mac on 2015/3/4.
 */
public final class UdpDataManager extends Observable {
    public static int length = 73;
    public static boolean DEBUG = false;
    private static UdpDataManager INSTANCE = null;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private String serviceDemonName = "android.intent.action.UdpService";
    private boolean mIsBound;
    Messenger mService = null;
    int mCount = 0;
    /**
     * 用户控制密钥
     */
    private byte[] mUserKey = null;
    /**
     * 线程锁
     */
    private byte[] lock = new byte[0];
    /**
     * 查询协议版本线程 0x4005
     */
    private ProtocolVersionThread protocolVersionThread = new ProtocolVersionThread();
    /**
     * 多协议版本的数据查询
     */
    private MultiVersionScan multiVersionScan = new MultiVersionScan();
    /**
     * 标记查询
     */
    private boolean markScan = false;

    /**
     * 查询线程
     */
    private Thread scanThread = null;
    /**
     * 请求回复回调
     */
    private HashMap<Integer, IObserver> responseMapping = new HashMap<Integer, IObserver>();
    /**
     * 网络监听
     */
    private NetWorkBroadcast netWorkBroadcast;
    /**
     * 中转设备数据开关
     */
    private boolean bForwardBroadcastDataOff = false;
    /**
     * 上下文句柄
     */
    private Context mContext;

    /**
     * 打印绑定日志
     **/
    private boolean debug = false;

    private boolean recvudp = true;

    private ILogMessage logMessage;

    /**
     * 通讯端口
     */
    public static int mPort = DataType.HET.getPort();

    public static int mPort1 = DataType.OPEN.getPort();

    private DataType dataType = DataType.HET;

    /*为兼容老设备，做此处理*/
    private byte[] oldUserKey = null;
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "onServiceConnected.Connected to remote service");
            try {
                Message msg = Message.obtain(null,
                        UdpService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                msg.arg1 = mPort;
                msg.arg2 = DataType.OPEN.getPort();
                mService.send(msg);
                if (mService != null) {
                    scan();
                } else {
                }
            } catch (RemoteException e) {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "onServiceConnected.." + e.getMessage());
            }
            tips("UdpService服务启动成功");
        }

        public void onServiceDisconnected(ComponentName className) {
//            mService = null;
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "Disconnected from remote service mService:" + mService + " context:" + mContext);
            if (mContext != null) {
                try {
                    init(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private UdpDataManager() {
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setLogMessage(ILogMessage callback) {
        this.logMessage = callback;
    }

    /**
     * 获取服务管理实例
     *
     * @return
     */
    public static UdpDataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (UdpDataManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new UdpDataManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 目前port只能为18899（内部协议通讯端口） 28899（外部协议通讯端口） 49999（汉枫模块绑定通讯端口），不可任意填写
     *
     * @param port
     * @return
     */
    public UdpDataManager type(int port) {
        this.mPort = port;
        this.dataType = DataType.getDataType(port);
        return getInstance();
    }

    public UdpDataManager type(DataType dataType) {
        this.mPort = dataType.getPort();
        this.dataType = dataType;
        return getInstance();
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isOpenProtocol(UdpDeviceDataBean udpDeviceDataBean) {
        if (dataType.equals(DataType.OPEN) || (udpDeviceDataBean != null && udpDeviceDataBean.getPacketStart() == 0x5A))
            return true;
        else
            return false;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    /**
     * 设置是否将收到的数据进行UDP广播转发
     *
     * @param bForwardBroadcastData
     */
    public void setForwardBroadcastDataOff(boolean bForwardBroadcastData) {
        this.bForwardBroadcastDataOff = bForwardBroadcastData;
    }

    /**
     * 转发设备数据
     *
     * @param packetModel
     */
    private void trunBroadCastData(PacketModel packetModel) {
        if (bForwardBroadcastDataOff && packetModel != null) {
            packetModel.setIp(null);
            packetModel.setPort(this.mPort);
            try {
                send(packetModel);
            } catch (Exception e) {
                e.printStackTrace();
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "trunBroadCaseData is error=" + e.getMessage());
            }
        }
    }

    /**
     * 这是用户控制秘钥
     *
     * @param key
     */
    public void setmUserKey(byte[] key) {
        this.mUserKey = key;
    }

    //In order to compatible with criticism
    public void setUserKey(byte[] key) {
        this.mUserKey = key;
    }

    /**
     * 初始化服务
     *
     * @param context
     */
    public void init(Context context) throws Exception {
        if (context == null) {
            throw new Exception("application's Context is null");
        }
        recvudp = true;
        mContext = context.getApplicationContext();
        savePort(mPort);
        registerWifiListener(context);
        Intent intent = new Intent();
        int androidVersion = SystemUtils.getSDKVersionNumber();
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "cucent android os version:" + androidVersion + " model=" + android.os.Build.MODEL + " port:" + mPort + " thread:" + Thread.currentThread().getName());
        //MI PAD
        if (androidVersion >= 21 || android.os.Build.MODEL.equalsIgnoreCase("MI PAD")
                || android.os.Build.MODEL.equalsIgnoreCase("LG-D728")) {
            //这一句至关重要，对于android5.0以上，所以minSdkVersion最好小于21；
            intent.setPackage(context.getPackageName());
        }
        if (mPort == DataType.HET.getPort()) {
            serviceDemonName = "android.intent.action.UdpService";
        } else {
            serviceDemonName = "android.intent.action.UdpService_v2";
        }
//        serviceDemonName = "android.intent.action.UdpService";
        intent.setAction(serviceDemonName);
        Prefers.init(mContext).save(UdpService.MSG_ARG_SERVICENAME, serviceDemonName);

        mIsBound = context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (!mIsBound) {
            tips("UdpService初始化失败");
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "初始化service失败..mService=" + mService + " mConnection=" + mConnection);
            throw new Exception("create service error!..mService=" + mService + " mConnection=" + mConnection);
        } else {
            KeepAliveManager.getInstnce().init();
            ReplyManager.getInstance().startKeepReply(null);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "成功初始化ServiceManager App.packageName=" + context.getPackageName() + " mService=" + mService + " mConnection=" + mConnection);
        }
    }

    /**
     * 注册设备状态监听器
     *
     * @param onDeviceOnlineListener
     */
    public void registerDeviceOnlineListener(OnDeviceOnlineListener onDeviceOnlineListener) {
        if (onDeviceOnlineListener != null)
            KeepAliveManager.getInstnce().resgisterDeviceOnlineListener(onDeviceOnlineListener);
    }

    /**
     * 注销设备状态监听器
     *
     * @param onDeviceOnlineListener
     */
    public void unregisterDeviceOnlineListener(OnDeviceOnlineListener onDeviceOnlineListener) {
        if (onDeviceOnlineListener != null) {
            KeepAliveManager.getInstnce().unresgisterDeviceOnlineListener(onDeviceOnlineListener);
        }
    }

    private void registerWifiListener(Context context) {
//        netWorkBroadcast = new NetWorkBroadcast();
//        IntentFilter intentfilter = new IntentFilter();
//        intentfilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        intentfilter.addAction("android.net.conn.WIFI_STATE_CHANGED");
//        intentfilter.addAction("android.net.conn.STATE_CHANGE");
//        intentfilter.addAction("android.net.wifi.STATE_CHANGE");
//        intentfilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        context.registerReceiver(netWorkBroadcast, intentfilter);
    }

    private void unregisterWifiListener(Context context) {
//        if (netWorkBroadcast != null && context != null) {
//            context.unregisterReceiver(netWorkBroadcast);
//        }
    }

    /**
     * 重载初始化服务
     *
     * @param context
     * @param callback
     */
    public void init(Context context, IObserver callback) throws Exception {
        init(context);
        if (callback != null) {
            registerObserver(callback);
        }
    }

    public void init(Context context, byte[] userKey) throws Exception {
        setmUserKey(userKey);
        init(context);
    }

    public void init(Context context, IObserver callback, byte[] userKey) throws Exception {
        setmUserKey(userKey);
        init(context, callback);
    }

    /**
     * 解除Service绑定
     *
     * @param context
     * @throws RemoteException
     */
    public void unBind(Context context) throws Exception {
        if (context == null)
            throw new Exception("application's Context is null");
        if (mIsBound) {
            if (mService != null) {
                Message msg = Message.obtain(null,
                        UdpService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            // Detach our existing connection.
            context.unbindService(mConnection);
            mIsBound = false;
            unregisterWifiListener(context);
            KeepAliveManager.getInstnce().close();
            clear();
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "解除Service绑定");
        }
        DeviceBindMap.runJudgeBindStatus.clear();
        DeviceBindMap.bindDeviceMap.clear();
        DeviceBindMap.normalMacSets.clear();
        DeviceBindMap.specialMacSets.clear();
        ProtocolManager.getInstance().close();
    }

    private byte getProtocolVersion(byte[] data) {
        if (data != null && data[0] == (byte) 0xF2) {
            return data[1];
        }
        return -1;
    }

    /**
     * UDP数据回调，分发数据；
     *
     * @param msg
     */
    private void dispathPackets(Message msg) {
        if (msg == null || msg.obj == null)
            return;
        Bundle bundle = (Bundle) msg.obj;
        if (bundle == null)
            return;
        try {
            byte[] data = bundle.getByteArray(UdpService.MSG_ARG_DATA);
            if (data == null || data.length == 0)
                return;
            byte packetStart = data[0];
            //数据来源IP地址
            String ip = bundle.getString(UdpService.MSG_ARG_IP);
            int port = bundle.getInt(UdpService.MSG_ARG_PORT, DataType.HET.getPort());
            PacketModel packets = new PacketModel();
            //来源数据命令字
            int cmd = ByteUtils.getCommandNew(data);
            if (packetStart == 0x5A) {
                //针对开放平台协议获取命令字，其数组索引号为31；
                cmd = ByteUtils.getCommandForOpen(data);
                //标记协议为开放平台协议
                packets.setOpenProtocol(true);
            }
            packets.setData(data);
            trunBroadCastData(packets);
            packets.setIp(ip);
            packets.setPort(port);
            packets.setPacketStart(packetStart);
            packets.setCommand((short) cmd);
            packets.setProtocolVersion(getProtocolVersion(packets.getData()));
            //解析数据
            PacketUtils.in(packets);

            if (recvudp) {
                recvudp = false;
                tips("UdpService已经启动");
            }
            //数据来源：
            // 设备发送序列号（0）
            // 服务器发送序列号（1）
            // App发送序列号（2）
            /*if (packets.isOpenProtocol()){
                UdpDeviceDataBean dm = packets.getDeviceInfo();
                if (dm != null) {
                    int source = dm.getFrameSN();
                    int src;
                    if ((src = (source & 0xFFFFFFFF) >> 28) == 2){
                        Logc.i(Logc.HetReportTag.INFO_WIFI,"App发出数据："+packets.getIp()+" data="+ByteUtils.toHexString(packets
                                .getData()));
                        return;
                    }

                }
            }*/

            //添加到reply队列
            recvReply(packets);
            if (packets.getCommand() == Contants.OPEN.RUNERROR._HET_OPEN_RUN_ERR_RECV)
                recvReply010E(packets);

            if (packets.getDeviceInfo() != null && !ByteUtils.isNull(packets.getDeviceInfo().getDeviceMac())) {
                DeviceBindMap.runJudgeBindStatus.put(packets.getDeviceInfo().getDeviceMac().toUpperCase(), packets.getDeviceInfo());
                if (packets.getDeviceInfo().getDeviceBindStatus() == 0) {
                    DeviceBindMap.unbindDeviceMap.put(packets.getDeviceInfo().getDeviceMac().toUpperCase(), packets.getDeviceInfo());
                }
                //Due to old protocol send error data causes have to doing this dispose in here.
//				startDiscover(cmd,packets);
                releaseLock();
                //0010 0017 0007 的数据一定是绑定的设备
                if (cmd == Contants.HET_LAN_QUERY_CONFIG_AND_RUNNING ||
                        cmd == Contants.HET_SMARTLINK_SEND_SERVER_INFO_RES ||
                        cmd == Contants.HET_LAN_SEND_CONFIG_RSP ||
                        //开放平台，请求设备控制数据，设备回复控制数据命令字0x0304
                        cmd == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RES ||
                        cmd == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RECV ||
                        cmd == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND) {
                    DeviceBindMap.bindDeviceMap.put(packets.getDeviceInfo().getDeviceMac().toUpperCase(), packets.getDeviceInfo());
                }
            }

            if (cmd > 0) {
                byte[] body = packets.getBody();
                //针对请求做的回复
                if (responseMapping.size() > 0 && body != null) {
                    int key = ByteUtils.bytesToInt(body, 0);
                    IObserver callback = responseMapping.get(key);
                    if (callback != null) {
                        callback.receive(packets);
                        responseMapping.remove(key);
                    } else {
                        notifyObservers(packets);
                    }
                } else {
                    notifyObservers(packets);
                }
            } else {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "非法数据");
            }
        } catch (PacketParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, e.getMessage());
        }
    }

    public void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PacketModel ppp = new PacketModel();
                UdpDeviceDataBean dm = new UdpDeviceDataBean();
//                (deviceModel.isNeedReply() && !deviceModel.isAgainData() && deviceModel.isJustCtrlData()
                dm.setNeedReply(true);
                dm.setAgainData(false);
                dm.setJustCtrlData(true);
                ppp.setDeviceInfo(dm);
                dm.setIp(IpUtils.getBroadcastAddress(mContext));
                dm.setFrameSN(12303);
                ppp.setBody(new byte[10]);
                ppp.setData(new byte[]{(byte) 0xf2, (byte) 0x41, (byte) 0x00, (byte) 0x40, (byte) 0x07, (byte) 0xac, (byte) 0xcf, (byte) 0x23, (byte) 0xa2, (byte) 0x69, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x2e, (byte) 0x02, (byte) 0x43, (byte) 0x35, (byte) 0x43, (byte) 0x36, (byte) 0x37, (byte) 0x45, (byte) 0x36, (byte) 0x30, (byte) 0x44, (byte) 0x39, (byte) 0x35, (byte) 0x32, (byte) 0x31, (byte) 0x45, (byte) 0x34, (byte) 0x41, (byte) 0x43, (byte) 0x46, (byte) 0x32, (byte) 0x41, (byte) 0x44, (byte) 0x41, (byte) 0x43, (byte) 0x43, (byte) 0x35, (byte) 0x33, (byte) 0x39, (byte) 0x32, (byte) 0x32, (byte) 0x33, (byte) 0x39, (byte) 0x44, (byte) 0x41, (byte) 0x0f, (byte) 0x30, (byte) 0x0f, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x12, (byte) 0x02, (byte) 0x01, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x32, (byte) 0xf1});
                try {
                    send(ppp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送UDP数据包
     *
     * @throws RemoteException
     */
    public void send(PacketModel data) throws Exception {
        if (data == null)
            return;
        sendReply(data);
        if (data.getData() != null && data.getData().length > 0) {
            Message msg = Message.obtain();
            msg.what = UdpService.MSG_SEND_UDP;
            Bundle bundle = new Bundle();
            bundle.putString(UdpService.MSG_ARG_IP, data.getIp());
            //如果是49999 则是广播ssid密码
//            int port = data.getPort() == DataType.HF.getPort() ? data.getPort() : mPort;
            int port = (data.getPort() == 0 ? DataType.HET.getPort() : data.getPort());
            bundle.putInt(UdpService.MSG_ARG_PORT, port);
            bundle.putByteArray(UdpService.MSG_ARG_DATA, data.getData());
            msg.obj = bundle;
            if (mService != null) {
                try {
                    mService.send(msg);
                } catch (DeadObjectException e) {
                    e.printStackTrace();
                    if (e != null) {
                        Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "DeadObjectException 1.3.2:" + e.getMessage());
                    }
                }
            } else {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "udpService being initialized,so wait " + mContext);
            }
        }
    }

    public void sendToService(int type,String extral) {
        if (extral == null)
            return;
        Message msg = Message.obtain();
        msg.what = UdpService.MSG_EXTRAL;
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("extral", extral);
        msg.obj = bundle;
        if (mService != null) {
            try {
                mService.send(msg);
            } catch (DeadObjectException e) {
                e.printStackTrace();
                if (e != null) {
                    Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "DeadObjectException 1.3.2:" + e.getMessage());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "udpService being initialized,so wait " + mContext);
        }
    }

    //查询类数据、绑定类数据、控制类数据
    private void sendReplyForOpen(PacketModel packet) {
        if (packet == null)
            return;
        //数据必须是下发控制数据 0x0104
//        if (packet.getCommand() != Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND) {
//            PacketUtils.out(packet);
//            return;
//        }
        //数据必须是下发控制数据 0x0104 0404
        if (!mustReplyCmd(packet.getCommand())) {
            PacketUtils.out(packet);
            return;
        }
        if (packet.getBody() == null)
            return;
        UdpDeviceDataBean udpDeviceDataBean = packet.getDeviceInfo();
        if (udpDeviceDataBean == null)
            return;
//        udpDeviceDataBean.setFrameNo((short) System.currentTimeMillis());
        if (TextUtils.isEmpty(packet.getIp()))
            return;
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "ppppppx-sendReply->need=" + udpDeviceDataBean.isNeedReply() + " agin=" + udpDeviceDataBean.isAgainData() + " just=" + udpDeviceDataBean.isJustCtrlData() + " data=" + packet.toString());
        //0x4007表示控制数据，需要做回复
        //需要回复&&不是补发数据&&仅仅是config控制数据，非查询类指令数据（因为body != null）
        //条件简述：必须是App主动发出的控制帧，且不是补发帧
        if (udpDeviceDataBean.isNeedReply() && !udpDeviceDataBean.isAgainData() && udpDeviceDataBean.isJustCtrlData()) {

            GenerateOpenPacket generateOpenPacket = new GenerateOpenPacket();
            generateOpenPacket.setFrameNo(ByteUtils.calcFrameNumber());
            generateOpenPacket.setBody(packet.getBody());
            generateOpenPacket.generateSendConfigPacket(packet);
//            PacketUtils.out(packet);
            ReplyManager.getInstance().addPacketReply(packet);
        }
    }

    private boolean mustReplyCmd(short cmd) {
        if (cmd == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND/* || cmd == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_REQ*/)
            return true;
        else
            return false;
    }

    private void sendReplyForHet(PacketModel packet) {
        if (packet == null)
            return;
        //TODO Open 如果接入开放平台，此处加入开放平台协议控制命令字
        //TODO 暂时不考虑补发机制
        if (packet.getCommand() == Contants.HET_LAN_SEND_RUN_REQ) {//查询运行数据
            PacketUtils.out(packet);
        }
        if (packet.getBody() == null)
            return;
        UdpDeviceDataBean udpDeviceDataBean = packet.getDeviceInfo();
        if (udpDeviceDataBean == null)
            return;
        if (packet.getBody().length < 4)
            return;
        if (TextUtils.isEmpty(packet.getIp()))
            return;
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "ppppppx-sendReply->need=" + udpDeviceDataBean.isNeedReply() + " agin=" + udpDeviceDataBean.isAgainData() + " just=" + udpDeviceDataBean.isJustCtrlData() + " data=" + packet.toString());
        //0x4007表示控制数据，需要做回复
        //需要回复&&不是补发数据&&仅仅是config控制数据，非查询类指令数据（因为body != null）
        if (udpDeviceDataBean.isNeedReply() && !udpDeviceDataBean.isAgainData() && udpDeviceDataBean.isJustCtrlData()) {
            //TODO Open 如果接入开放平台，此处加入开放平台协议控制命令字
            if (packet.getCommand() == Contants.HET_LAN_SEND_CONFIG_REQ) {
                ReplyManager.makePacketReply(packet, IpUtils.getLocalIP(mContext));
                PacketUtils.out(packet);
            }
            ReplyManager.getInstance().addPacketReply(packet);
        } else {
            //TODO Open 如果接入开放平台，此处加入开放平台协议控制命令字
            if (packet.getCommand() == Contants.HET_LAN_SEND_CONFIG_REQ) {
                PacketUtils.out(packet);
            }
        }
    }

    private void sendReply(PacketModel packet) {
        if (packet == null || packet.isSmartlink())
            return;
        if (isOpenProtocol(packet.getDeviceInfo())) {
            sendReplyForOpen(packet);
        } else {
            sendReplyForHet(packet);
        }
    }

    private void recvReply(PacketModel arg) {
        if (arg == null)
            return;
        if (arg.isOpenProtocol()) {
            recvReplyForOpen(arg);
        } else {
            recvReplyForHet(arg);
        }
    }

    private void recvReply010E(PacketModel packet) {
        if (packet == null)
            return;
        UdpDeviceDataBean udpDeviceDataBean = packet.getDeviceInfo();
        UdpDeviceDataBean deviceInfo = new UdpDeviceDataBean();
        deviceInfo.setDeviceMac(udpDeviceDataBean.getDeviceMac());
        deviceInfo.setCommandType(Contants.OPEN.RUNERROR._HET_OPEN_RUN_ERR_RECV_REPLY);
        deviceInfo.setNewDeviceTypeForOpen(udpDeviceDataBean.getNewDeviceType());
        deviceInfo.setPacketStart(udpDeviceDataBean.getPacketStart());
        PacketModel p = new PacketModel();
        p.setDeviceInfo(deviceInfo);
        p.setCommand(Contants.OPEN.RUNERROR._HET_OPEN_RUN_ERR_RECV_REPLY);
        p.setBody(packet.getBody());
        p.setIp(packet.getIp());
        p.setPacketStart((byte) 0x5A);
        p.setOpenProtocol(true);
        PacketUtils.out(packet);
        byte[] data = PacketUtils.out(p);
        p.setData(data);
        try {
            send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recvReplyForOpen(PacketModel arg) {
        PacketModel replyPacket = null;
        if (arg == null)
            return;
        //接收到的数据必须是0x0104和0x0204
        //0x0104:上传控制参数（终端App）
        //0x0204:下发控制参数回复（终端App）
        if (!(arg.getCommand() == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND || arg.getCommand() ==
                Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RECV))
            return;
        try {
            replyPacket = (PacketModel) arg.deepCopy();
            //App主动控制设备，设备对改帧回复，则从补发队列移除
            if (arg.getCommand() == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RECV) {
                ReplyManager.getInstance().removeReplyPacket(replyPacket);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (replyPacket.getBody() == null)
            return;
        UdpDeviceDataBean udpDeviceDataBean = replyPacket.getDeviceInfo();
        udpDeviceDataBean.setNeedReply(true);
        udpDeviceDataBean.setCommandType(Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RECV);
        if (udpDeviceDataBean.isNeedReply()) {
            try {
                PacketModel answer = (PacketModel) replyPacket.deepCopy();
                answer.setBody(null);
                answer.setUserKey(null);
//                byte[] data = PacketUtils.out(answer);
                send(answer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void recvReplyForHet(PacketModel arg) {
        PacketModel replyPacket = null;
        if (arg == null)
            return;
        //TODO Open 如果接入开放平台，此处加入开放平台协议控制命令字
        if (arg.getCommand() != Contants.HET_LAN_SEND_CONFIG_RSP)
            return;
        try {
            replyPacket = (PacketModel) arg.deepCopy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (replyPacket.getBody() == null)
            return;
        if (replyPacket.getBody().length < 4)
            return;
        //TODO 测试用
        if (DEBUG && replyPacket.getData().length != length)
            return;

        byte[] body = replyPacket.getBody();
        ByteBuffer buf = ByteBuffer.allocate(body.length);
        buf.put(body);
        buf.flip();

//        byte[] number = new byte[3];
        byte a = buf.get();//高位字节
        byte ip = buf.get();
        short frameNo = buf.getShort();//帧序号
//        buf.get(number);
        int scrollCount = 0;//buf.getInt();
        buf.get(new byte[body.length - 4]);

//        int frameNo = ByteUtils.threebytesToInt(number);
//        boolean needreply = (a & 0x01) == 1 ? true : false;
        boolean needreply = (a & 0xF0) == 0 ? true : false;//求高位字节高四位，如果是0表示设备

        UdpDeviceDataBean udpDeviceDataBean = replyPacket.getDeviceInfo();
        udpDeviceDataBean.setSource(a & 0xFE);
        udpDeviceDataBean.setNeedReply(needreply);
//        udpDeviceDataBean.setEndIp(ip);
        udpDeviceDataBean.setFrameSN(frameNo);
        udpDeviceDataBean.setCommandType(Contants.HET_LAN_SEND_CONFIG_REQ);

        if (DEBUG) {
            mCount++;
            StringBuffer sb = new StringBuffer();
            sb.append(Thread.currentThread().getName());
            sb.append(" ");
            sb.append(replyPacket.getIp());
            sb.append(":");
//            sb.append(ip);
            sb.append(" 丢包:");
            sb.append(scrollCount - mCount);
            sb.append(" reply=");
            sb.append(udpDeviceDataBean.isNeedReply());
            sb.append(" 滚动累加:");
            sb.append(mCount);
            sb.append(" 实际次数:");
            sb.append(scrollCount);
            sb.append(" 字节数:");
            sb.append(mCount * replyPacket.getData().length);
            sb.append(" 帧序号:");
            sb.append(frameNo);
            Log.i("xxxxxs", sb.toString());
        }
        ReplyManager.getInstance().removeReplyPacket(replyPacket);
        if (udpDeviceDataBean.isNeedReply()) {
            //对数据进行回复
            ReplyManager.makePacketNoReply(replyPacket, IpUtils.getLocalIP(mContext));
            //TODO 此处代码应该干掉，因为在send里面又封装了一次
            byte[] data = PacketUtils.out(replyPacket);
            try {
                send(replyPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(byte[] body, byte[] userKey, String mac) throws Exception {
        send(body, userKey, mac, null);
    }

    /**
     * 客户端调用发送控制数据接口（command = 4007）
     *
     * @param body
     * @param mac
     * @throws Exception
     */
    public void send(byte[] body, byte[] userkey, String mac, OnSendListener onSendListener) throws Exception {
        if (mac != null && oldUserKey != null) {
            UdpDeviceDataBean udpDeviceDataBean = DeviceBindMap.runJudgeBindStatus.get(mac.toUpperCase());
            //如果是5A协议，则跳过,不做特殊发送处理
            if (udpDeviceDataBean != null && udpDeviceDataBean.getPacketStart() == 0x5A) {
                send(body, userkey, mac, HET_CONFIG_DATA, onSendListener);
                return;
            }
            //如果是正常设备，也不做特殊发送处理，pass
            boolean beNormal = DeviceBindMap.normalMacSets.contains(mac.toUpperCase());
            if (beNormal) {
                send(body, userkey, mac, HET_CONFIG_DATA, onSendListener);
                return;
            }
            //如果是特殊设备，则做特殊发送处理
            boolean beSpecial = DeviceBindMap.specialMacSets.contains(mac.toUpperCase());
            send(body, oldUserKey, mac, HET_CONFIG_DATA, onSendListener);
            Thread.sleep(100);
            if (beSpecial) {
                return;
            }
        }

        send(body, userkey, mac, HET_CONFIG_DATA, onSendListener);
    }

    private void send(byte[] body, byte[] userkey, String mac, short dataType, byte[] updateFlag, OnSendListener onSendListener) throws Exception {
//        dataType = dataType == HET_RUN_DATA ? Contants.HET_LAN_SEND_RUN_REQ : Contants.HET_LAN_SEND_CONFIG_REQ;
        if (userkey == null || userkey.length != 32) {
            Exception throwable = new Exception("mUserKey data is null or err...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        if (ByteUtils.isNull(mac)) {
            Exception throwable = new Exception("Mac addr is null...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }

        UdpDeviceDataBean udpDeviceDataBean = DeviceBindMap.runJudgeBindStatus.get(mac.toUpperCase());
        if (udpDeviceDataBean == null || ByteUtils.isNull(udpDeviceDataBean.getIp())) {
            Exception throwable = new Exception("This Mac Device is not inside lan list...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        dataType = getCmd(dataType, udpDeviceDataBean);
        udpDeviceDataBean.setCommandType(dataType);
        if (!isOpenProtocol(udpDeviceDataBean)) {
            udpDeviceDataBean.setDataStatus((byte) -128);//-128 = 1000 0000 发送数据 请求数据 应答数据 0数据需要应答1无需应答 0000
        }
        PacketModel packet = new PacketModel();

        if (udpDeviceDataBean.getProtocolVersion() == (byte) 0x42 ||
                udpDeviceDataBean.getPacketStart() == 0x5A) {
            if (body == null) {
                packet.setBody(userkey);
            } else {
                byte[] tmp = new byte[body.length + userkey.length];
                System.arraycopy(userkey, 0, tmp, 0, userkey.length);
                System.arraycopy(body, 0, tmp, userkey.length, body.length);
                packet.setBody(tmp);
            }
        } else {
            if (body == null) {
                byte[] tmp = new byte[1 + userkey.length];
                tmp[0] = 1;
                System.arraycopy(userkey, 0, tmp, 1, userkey.length);
                packet.setBody(tmp);
            } else {
                byte[] tmp = new byte[1 + body.length + userkey.length];
                tmp[0] = 2;
                System.arraycopy(userkey, 0, tmp, 1, userkey.length);
                System.arraycopy(body, 0, tmp, 1 + userkey.length, body.length);
                packet.setBody(tmp);
            }

            if (oldUserKey != null && userkey != null && Arrays.equals(oldUserKey, userkey)) {
                udpDeviceDataBean.setBeUseOldUserKey(true);
            } else {
                udpDeviceDataBean.setBeUseOldUserKey(false);
            }

//            System.out.println("udp.@@send.body:" + Thread.currentThread().getName() + ByteUtils.toHexString(packet.getBody()) + " " + udpDeviceDataBean.toString() + " old:" + ByteUtils.toHexString(oldUserKey) + " user:"+ByteUtils.toHexString(userkey));
        }

        if (body != null) {
            udpDeviceDataBean.setJustCtrlData(true);
            udpDeviceDataBean.setAgainData(false);
        }
        if (Contants.HET_LAN_SEND_CONFIG_REQ == udpDeviceDataBean.getCommandType() || udpDeviceDataBean
                .getCommandType() == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND) {
            udpDeviceDataBean.setNeedReply(true);
        }
        packet.setUpdateFlag(updateFlag);
        packet.setDeviceInfo(udpDeviceDataBean);
        packet.setOnSendListener(onSendListener);
        send(packet);
    }

    public void send(byte[] body, byte[] userkey, String mac, short dataType, OnSendListener onSendListener) throws Exception {
        send(body, userkey, mac, dataType, (byte[]) null, onSendListener);
    }

    private void send(byte[] body, byte[] userKey, String mac, short dataType, IObserver response, OnSendListener onSendListener) throws Exception {
//        dataType = getCmd(dataType);
        if (response != null && body != null) {
            int key = ByteUtils.bytesToInt(body, 0);
            responseMapping.put(key, response);
        }
        send(body, userKey, mac, dataType, onSendListener);
    }

    private void send(byte[] body, byte[] userkey, String mac, byte[] updateFlag, OnSendListener onSendListener) throws Exception {
        send(body, userkey, mac, HET_CONFIG_DATA, updateFlag, onSendListener);
    }

    public void send(byte[] body, byte[] userKey, String mac, IObserver response, OnSendListener onSendListener) throws Exception {
        send(body, userKey, mac, HET_CONFIG_DATA, response, onSendListener);
    }

    private void send(byte[] body, byte[] userKey, String mac, IObserver response, byte[] updateFlag, OnSendListener onSendListener) throws Exception {
        send(body, userKey, mac, HET_CONFIG_DATA, response, updateFlag, onSendListener);
    }

    private void send(byte[] body, byte[] userKey, String mac, short dataType, IObserver response, byte[] updateFlag, OnSendListener onSendListener) throws Exception {
//        dataType = getCmd(dataType);
        if (response != null && body != null) {
            int key = ByteUtils.bytesToInt(body, 0);
            responseMapping.put(key, response);
        }
        send(body, userKey, mac, dataType, updateFlag, onSendListener);
    }

    private short getCmd(short cmd, UdpDeviceDataBean udpDeviceDataBean) {
        if (cmd == HET_RUN_DATA) {
            if (isOpenProtocol(udpDeviceDataBean)) {
                cmd = Contants.OPEN.RUN._HET_OPEN_RUN_REQ;
            } else {
                cmd = Contants.HET_LAN_SEND_RUN_REQ;
            }
        } else {
            if (isOpenProtocol(udpDeviceDataBean)) {
                cmd = Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND;
            } else {
                cmd = Contants.HET_LAN_SEND_CONFIG_REQ;
            }
        }
        return cmd;
    }

    public void requestConfigData(byte[] userkey) throws Exception {
        GenerateOpenPacket generateOpenPacket = new GenerateOpenPacket();
        generateOpenPacket.setFrameNo(ByteUtils.calcFrameNumber());
        generateOpenPacket.setBody(userkey);
        send(generateOpenPacket.generateReqConfigPacket());
    }


    private void send(String json, byte[] userkey, String macAddr) throws Exception {
        send(json, userkey, macAddr, null);

    }

    public void send(String json, byte[] userkey, String macAddr, OnSendListener onSendListener) throws Exception {
        if (!ProtocolManager.getInstance().isLoad()) {
            Exception throwable = new Exception("ProtocolManager is not init...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        if (TextUtils.isEmpty(macAddr)) {
            Exception throwable = new Exception("macAddr is null..");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        UdpDeviceDataBean udpDeviceDataBean = DeviceBindMap.runJudgeBindStatus.get(macAddr.toUpperCase());
        if (udpDeviceDataBean == null || ByteUtils.isNull(udpDeviceDataBean.getIp())) {
            Exception throwable = new Exception("This Mac Device is not inside lan list...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        short cmd = getCmd(HET_CONFIG_DATA, udpDeviceDataBean);
        udpDeviceDataBean.setCommandType(cmd);
        PacketModel packet = new PacketModel();
        packet.setDeviceInfo(udpDeviceDataBean);
        packet.setJson(json);
        if (packet.getJson() == null) {
            Exception throwable = new Exception("json data is null...");
            onSendListener.onSendFailed(0, null, throwable);
            throw throwable;
        }
        byte[] body = ProtocolManager.getInstance().encode(getPackDataBean(packet));
        packet.setBody(body);
        packet.setOnSendListener(onSendListener);
        send(body, userkey, macAddr, onSendListener);
    }


    /**
     * 此Mac地址对应设备是否为小循环
     * true：小循环  false：非小循环
     *
     * @param mac
     */
    public boolean isLanOnline(String mac) {
        if (!ByteUtils.isNull(mac)) {
            UdpDeviceDataBean udpDeviceDataBean = DeviceBindMap.runJudgeBindStatus.get(mac.toUpperCase());
            if (udpDeviceDataBean != null && !ByteUtils.isNull(udpDeviceDataBean.getIp())) {
                return true;
            }
        }
        return false;
    }

    public void resetClient() throws RemoteException {
        Message msg = Message.obtain();
        msg.what = UdpService.MSG_RESET_CLIENT;
        if (mService != null)
            mService.send(msg);
    }

    /**
     * 扫描局域网设备
     *
     * @param key 用户控制秘钥，根据用户信息生成MD5加密的32位字节数组
     * @throws IOException
     * @throws RemoteException
     */
    public void startScan(byte[] key) {
        if (key != null && key.length > 0) {
            mUserKey = key;
        } else {
            if (mUserKey == null) {
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UserKey is null");
            }
        }
        if (multiVersionScan != null) {
            multiVersionScan.setUserKey(mUserKey);
            scan();
//            markScan = true;
            startFind(mUserKey);
        }
    }

    public void setUserKeyForUserId(byte[] key) {
        this.oldUserKey = key;
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UserKey.oldUserKey:" + ByteUtils.toHexString(key));
    }

    /**
     * 默认扫描5次
     */
    private void scan() {
        requestProtocolVersion();
    }

    private void requestProtocolVersion() {
        if (!protocolVersionThread.isRunning()) {
            protocolVersionThread.start();
        }
    }

    private void startDiscover(int cmd, PacketModel packet) {
        if (cmd == Contants.HET_LAN_TIMER_RUNNING) {
            if (packet.getDeviceInfo() != null) {
                multiVersionScan.setUserKey(this.mUserKey);
                multiVersionScan.start(this.mUserKey, packet.getDeviceInfo());
            }
        }
    }

    /**
     * 释放锁
     */
    private void releaseLock() {
        //Only for F241 and F242
        if (markScan || (dataType == DataType.OPEN))
            return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }, "releaseLock").start();
    }

    /**
     * 更加运行数据得到协议版本再查询
     *
     * @param userKey
     */
    private void startFind(final byte[] userKey) {
        if (scanThread != null) {
//            Logc.i(Logc.HetReportTag.INFO_WIFI,"startFind.scanThread.state:" + scanThread.getState());
            if (scanThread.getState() == Thread.State.WAITING) {
                markScan = false;
                releaseLock();
                return;
            }
        }

        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (DeviceBindMap.runJudgeBindStatus.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    Iterator<String> it = DeviceBindMap.runJudgeBindStatus.keySet().iterator();
                    Set<Integer> multiVersionProtocol = new HashSet<Integer>();
                    while (it.hasNext()) {
                        String key = it.next();
                        UdpDeviceDataBean dm = DeviceBindMap.runJudgeBindStatus.get(key.toUpperCase());
                        if (dm != null && userKey != null) {
                            int protocolVersion = dm.getProtocolVersion() & 0xFF;
                            if (multiVersionProtocol.contains(protocolVersion)) {
                                continue;
                            }
                            multiVersionProtocol.add(protocolVersion);
                            multiVersionScan.setUserKey(userKey);
                            multiVersionScan.start(userKey, dm);
                        }
                    }
                    lock.notifyAll();
                    markScan = true;
                }
            }
        }, "startFind-" + SystemUtils.getCurTime());
        scanThread.start();
    }

    private void savePort(int port) {
        Prefers.init(mContext).save(UdpService.MSG_ARG_PORT, port);
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UdpService.MSG_SET_VALUE:
                    dispathPackets(msg);
                    break;
                case UdpService.MSG_SERVICE_STATUS:
                    if (msg == null || msg.obj == null)
                        return;
                    Bundle bundle = (Bundle) msg.obj;
                    if (bundle == null)
                        return;
                    String log = bundle.getString(UdpService.MSG_ARG_DATA);
                    tips(log);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void tips(String msg) {
        if (debug) {
            if (logMessage != null) {
                logMessage.onLogMessage(msg);
            } else {
//                if (mContext != null) {
//                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }

}
