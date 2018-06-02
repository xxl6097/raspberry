/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.het.udp.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.het.log.Logc;
import com.het.udp.core.Utils.DataType;
import com.het.udp.core.Utils.IpUtils;
import com.het.udp.wifi.callback.IExtralCallBack;
import com.het.udp.wifi.callback.IRecevie;
import com.het.udp.wifi.core.UdpManager;
import com.het.udp.wifi.model.PacketBuffer;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.LOG;
import com.het.udp.wifi.utils.Prefers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This is an UdpService of implementing an application service that uses the
 * {@link Messenger} class for communicating with clients.  This allows for
 * remote interaction with a service, without needing to define an AIDL
 * interface.
 * <p/>
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

/**
 * Created by uuxia-mac on 2015/3/3.
 */

public class UdpService extends Service implements IRecevie {
    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;
    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;
    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int MSG_SET_VALUE = 3;
    /**
     * Command to service to send a UDP Packet.  This can be sent to the
     * service to supply a new value.
     */
    public static final int MSG_SEND_UDP = 4;
    public static final int MSG_RESET_CLIENT = 5;
    public static final int MSG_EXTRAL = 7;
    /**
     * UDP服务创建状态
     **/
    public static final int MSG_SERVICE_STATUS = 6;
    public static final String MSG_ARG_IP = "ip";
    public static final String MSG_ARG_PORT = "port";
    public static final String MSG_ARG_DATA = "data";
    public static final String MSG_ARG_SERVICENAME = "service_name";


    public static final String MSG_ARG_PORTS = "ports";
    /**
     * 本地IP地址
     */
    public static String localip = null;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    /**
     * Keeps track of all current registered clients.
     */
    protected ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    /**
     * 存储连接设备信息*
     */
    public static ConcurrentHashMap<Integer, UdpManager> udpManegerMapper = new ConcurrentHashMap<Integer, UdpManager>();
    /**
     * 发送UDP广播地址
     */
    public String mBraodIp = "192.168.1.255";
    /**
     * 发送UDP广播端口
     */
//    public static int trasPort = DataType.HET.getPort();

    public WifiManager.MulticastLock lock;

    public String serviceName;

    private static UdpService instance = null;

    protected String getAction() {
        return "android.intent.action.UdpService";
    }

    public static UdpService getInstance() {
        return instance;
    }

    public static IExtralCallBack extralCallBack;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LOG.HanFengV3 = true;
        serviceName = getAction();//Prefers.init(this).getString(MSG_ARG_SERVICENAME, null);
        if (!TextUtils.isEmpty(serviceName)) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService onCreate, serviceName:" + serviceName + " getPackageName():" + getPackageName());
//            DaemonService.startDaemonService(this, this.getClass()/*UdpService.class*/, serviceName, getPackageName(), 60);
        }
        initSocket();
        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("localWifi");
        lock.acquire();
        tips("UdpService服务创建成功");
    }

    private void initSocket() {
        Set<Integer> ports = getPorts();
        if (ports != null && ports.size() > 0) {
            Iterator<Integer> it = ports.iterator();
            while (it.hasNext()) {
                int port = it.next();
                if (port <= 0) {
                    port = DataType.HET.getPort();
                }
                createSession(port);
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService onCreate..." + port + " " + udpManegerMapper.toString());
            }
        }
    }

    public void reset() {
        mBraodIp = IpUtils.getBroadcastAddress(this);
        resetMapper();
    }

    private void resetMapper() {
        Iterator<Integer> it = udpManegerMapper.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            UdpManager nUdpSocket = udpManegerMapper.get(key);
            if (nUdpSocket == null && key != 0) {
                createSession(key);
            }
            nUdpSocket.setBroadCasetIp(mBraodIp);
        }
    }

    private void closeAllSession() {
        Iterator<Integer> it = udpManegerMapper.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            UdpManager item = udpManegerMapper.get(key);
            if (item != null) {
                item.close();
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "destroy " + item);
                item = null;
            }
            it.remove();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBraodIp = IpUtils.getBroadcastAddress(this);
        localip = IpUtils.getLocalIP(this);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService onStartCommand, braodcastip:" + mBraodIp + " localIp:" + localip);
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent t) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "@@@UdpService.onBind()");
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lock.release();
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService.onDestroy()");
        closeAllSession();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService.onUnbind()");
        return super.onUnbind(intent);
    }

    public void onRebind(Intent intent) {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "UdpService.nRebind()");
        super.onRebind(intent);
    }

    private void registerClient(Message msg) {
        if (msg == null)
            return;
        int port = msg.arg1;
        int port2 = msg.arg2;
        if (port <= 0) {
            port = DataType.HET.getPort();
        }
        createSession(port);
        savePorts(port);
        if (port2 > 0) {
            createSession(port2);
            savePorts(port2);
        }
        mClients.add(msg.replyTo);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "####registerClient v3 port:" + port + "  " + msg.replyTo);

    }

    /**
     * 接收到客户端数据
     *
     * @param msg
     * @throws IOException
     */
    private void recvClinet(Message msg) {
        if (msg == null)
            return;
        if (msg.obj == null)
            return;

        Bundle bundle = (Bundle) msg.obj;
        if (bundle != null) {
            byte[] data = bundle.getByteArray(MSG_ARG_DATA);
            if (data != null && data.length > 0) {
                String ip = bundle.getString(MSG_ARG_IP);
                int port = bundle.getInt(MSG_ARG_PORT);
                send(data, ip, port);
                if (data[0] != 5 && data[data.length - 1] != 5 && data[data.length - 1] != data[0]) {
//                    Logc.i(Logc.HetReportTag.INFO_WIFI,Thread.currentThread().getName() + " recvClinet.." + ByteUtils.toHexString(data));
                }
            }
        }
    }

    private void recvExtralData(Message msg) {
        if (msg == null)
            return;
        if (msg.obj == null)
            return;
        if (extralCallBack == null)
            return;
        Bundle bundle = (Bundle) msg.obj;
        if (bundle != null) {
            int type = bundle.getInt("type");
            String data = bundle.getString("extral");
            extralCallBack.onExtral(type, data);
        }
    }

    /**
     * 发送UDP数据
     *
     * @param data
     * @throws IOException
     */
    private void send(byte[] data, String ip, int port) {
        if (data != null && data.length > 0 && port > 0) {
            if (ByteUtils.isNull(ip)) {
                ip = mBraodIp;
            }
            offer(ip, port, data);
        }
    }

    public void receive(String ip, byte[] data) {
        if (data != null && data.length > 0 && !ByteUtils.isNull(ip)) {
//            Logc.i(Logc.HetReportTag.INFO_WIFI,Thread.currentThread().getName() + " recv=" + ip + " " + ByteUtils.toHexString(data));
            Message msg = Message.obtain();
            msg.what = MSG_SET_VALUE;
            Bundle recvData = new Bundle();
            recvData.putString(MSG_ARG_IP, ip);
            recvData.putByteArray(MSG_ARG_DATA, data);
            msg.obj = recvData;
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "receive data failed:receive=" + e.getMessage());
            }
        }
    }

    public void receive(String ip, int port, byte[] data) {
        if (data != null && data.length > 0 && !ByteUtils.isNull(ip)) {
            Message msg = Message.obtain();
            msg.what = MSG_SET_VALUE;
            Bundle recvData = new Bundle();
            recvData.putString(MSG_ARG_IP, ip);
            recvData.putInt(MSG_ARG_PORT, port);
            recvData.putByteArray(MSG_ARG_DATA, data);
            msg.obj = recvData;
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "receive data failed:receive=" + e.getMessage());
            }
        }
    }

    @Override
    public void onRecevie(PacketBuffer packet) {
        if (packet == null)
            return;
        byte[] data = packet.getData();
        if (data == null)
            return;
        String ip = packet.getIp();
        receive(ip, packet.getPort(), data);
    }

    private synchronized void createSession(int port) {
        if (port <= 0) {
            return;
        }
        UdpManager hand = udpManegerMapper.get(port);
        if (hand != null) {
            return;
        }
        try {
            mBraodIp = IpUtils.getBroadcastAddress(this);
            UdpManager udpClient = new UdpManager(mBraodIp, port);
            udpClient.setCallback(this);
            localip = IpUtils.getLocalIP(this);
            if (udpClient != null) {
                udpClient.setLocalIp(localip);
            }
            udpClient.setBroadCasetIp(mBraodIp);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "create udp channel sucessfull,braodcastip:" + mBraodIp + " port:" + port + " localIp:" + localip);
            if (udpClient != null) {
                UdpManager item = udpManegerMapper.get(port);
                if (item != null) {
                    item.close();
                }
                udpManegerMapper.put(port, udpClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "UDP Channel 创建失败.." + e.getMessage());
        }
    }

    private synchronized void createSession1(int port) {
        if (port <= 0) {
            return;
        }
        try {
            mBraodIp = IpUtils.getBroadcastAddress(this);
            UdpManager udpClient = new UdpManager(mBraodIp, port);
            udpClient.setCallback(this);
            localip = IpUtils.getLocalIP(this);
            if (udpClient != null) {
                udpClient.setLocalIp(localip);
            }
            udpClient.setBroadCasetIp(mBraodIp);
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "create udp channel sucessfull,braodcastip:" + mBraodIp + " port:" + port + " localIp:" + localip);
            if (udpClient != null) {
                UdpManager item = udpManegerMapper.get(port);
                if (item != null) {
                    item.close();
                }
                udpManegerMapper.put(port, udpClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "UDP Channel 创建失败.." + e.getMessage());
        }
    }

    private void offer(String ip, int port, byte[] data) {
        UdpManager udpSocket = null;
        //汉枫广播密码
        if (port == DataType.HF.getPort()) {
            udpSocket = udpManegerMapper.get(DataType.HET.getPort());
        } else {
            udpSocket = udpManegerMapper.get(port);
        }

        //TODO 这个地方有可能报端口占用bug
        if (udpSocket == null) {
            int licensor = port;
            try {
                if (port == DataType.HF.getPort()) {
                    licensor = DataType.HET.getPort();
                }
                udpSocket = new UdpManager(mBraodIp, licensor);
                udpSocket.setCallback(UdpService.this);
                udpManegerMapper.put(licensor, udpSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (udpSocket != null) {
            udpSocket.send(data, ip, port);
        }
    }

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESET_CLIENT:
                    reset();
                    break;
                case MSG_REGISTER_CLIENT:
                    registerClient(msg);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_EXTRAL:
                    recvExtralData(msg);
                    break;
                case MSG_SEND_UDP:
                    recvClinet(msg);
                    break;
                case MSG_SET_VALUE:
//                    Logc.i(Logc.HetReportTag.INFO_WIFI,"~~~~~~~~~~~~mClients=" + mClients.size());
                    for (int i = mClients.size() - 1; i >= 0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null, MSG_SET_VALUE, msg.obj));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                case MSG_SERVICE_STATUS:
//                    Logc.i(Logc.HetReportTag.INFO_WIFI,"~~~~~~~~~~~~mClients=" + mClients.size());
                    for (int i = mClients.size() - 1; i >= 0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null, MSG_SERVICE_STATUS, msg.obj));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

//    private void savePort(int port){
//        Prefers.init(this).save(MSG_ARG_PORT, port);
//    }
//
//    private int getPort(){
//        return Prefers.init(this).getInt(MSG_ARG_PORT,DataType.HET.getPort());
//    }

    private void savePorts(int port) {
        if (port <= 0) {
            //为兼容
            port = 18899;
        }
        Integer[] ports = Prefers.init(this).getIntArray(MSG_ARG_PORTS);
        Set<Integer> setint = new HashSet<Integer>();
        setint.add(port);
        if (ports != null) {
            Collections.addAll(setint, ports);
        }
        Integer[] result = setint.toArray(new Integer[]{});
        Prefers.init(this).saveArray(MSG_ARG_PORTS, result);
    }

    private Set<Integer> getPorts() {
        Integer[] ports = Prefers.init(this).getIntArray(MSG_ARG_PORTS);
        Set<Integer> setint = new HashSet<Integer>();
        if (ports != null) {
            Collections.addAll(setint, ports);
        }
        return setint;
    }

    public static void main(String[] args) {
        Integer[] ports = null;
        Set<Integer> setint = new HashSet<Integer>();
        setint.add(18899);
        setint.add(28899);
        if (ports != null) {
            Collections.addAll(setint, ports);
        }
        Integer[] ppp = setint.toArray(new Integer[]{});


//        System.out.println(ppp.length);
    }

    public void tips(String tip) {
        Message msg = Message.obtain();
        msg.what = MSG_SERVICE_STATUS;
        Bundle recvData = new Bundle();
        recvData.putString(MSG_ARG_DATA, tip);
        msg.obj = recvData;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
