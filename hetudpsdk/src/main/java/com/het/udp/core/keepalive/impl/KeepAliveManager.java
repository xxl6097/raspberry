package com.het.udp.core.keepalive.impl;

import android.text.TextUtils;

import com.het.log.Logc;
import com.het.udp.core.Utils.DeviceBindMap;
import com.het.udp.core.keepalive.OnDeviceOnlineListener;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.utils.ByteUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by UUXIA on 2015/6/15.
 * 设备心跳管理类
 */
public class KeepAliveManager implements Runnable {

    /**监听器容器**/
    private Vector<OnDeviceOnlineListener> listeners = new Vector<>();

    /**设备在线存活时长**/
    private final static long keepalivetime = 21 * 1000;

    /**线程间隔时长**/
    private final static long interval = 5 * 1000;

    /**单例对象**/
    public static KeepAliveManager instance = null;

    /**线程标识**/
    private static boolean working = true;

    /**心跳线程**/
    private Thread keepAliveThread = null;

    /**线程锁**/
//    private byte[] lock = new byte[0];

    /**获取单例**/
    public static KeepAliveManager getInstnce() {
        if (instance == null) {
            synchronized (KeepAliveManager.class) {
                if (instance == null) {
                    instance = new KeepAliveManager();
                }
            }
        }
        return instance;
    }

    public void resgisterDeviceOnlineListener(final OnDeviceOnlineListener listener) {
        /*if (listener != null) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }*/
        if (listeners.contains(listener))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (listeners){
                    if (listener != null) {
                        if (!listeners.contains(listener)) {
                            listeners.add(listener);
                        }
                    }
                    listeners.notifyAll();
                }
            }
        }, "resgisterDeviceOnlineListener-" + ByteUtils.getCurrentTime()).start();
    }

    public void unresgisterDeviceOnlineListener(final OnDeviceOnlineListener listener) {
        /*if (listener != null) {
            if (listeners.contains(listener)) {
                listener.relese();
                listeners.remove(listener);
            }
        }*/
        if (!listeners.contains(listener))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (listeners){
                    if (listener != null) {
                        if (listeners.contains(listener)) {
                            listener.relese();
                            listeners.remove(listener);
                        }
                    }
                    listeners.notifyAll();
                }
            }
        }, "unresgisterDeviceOnlineListenerE-" + ByteUtils.getCurrentTime()).start();
    }

    /**
     * 注册监听到观察者队列
     *
     * @param onDeviceOnlineListener
     */
    public void resgisterDeviceOnlineListenerOld(final OnDeviceOnlineListener onDeviceOnlineListener) {
        if (listeners.contains(onDeviceOnlineListener))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (keepAliveThread == null){
                    if (onDeviceOnlineListener != null) {
                        if (!listeners.contains(onDeviceOnlineListener)) {
                            listeners.add(onDeviceOnlineListener);
                        }
                    }
                    return;
                }
                Thread.State state = keepAliveThread.getState();
                if (state.equals(Thread.State.WAITING)) {
                    synchronized (listeners) {
                        if (onDeviceOnlineListener != null) {
                            if (!listeners.contains(onDeviceOnlineListener)) {
                                listeners.add(onDeviceOnlineListener);
                            }
                            listeners.notifyAll();
                        }
                    }
                } else {
                    if (onDeviceOnlineListener != null) {
                        if (!listeners.contains(onDeviceOnlineListener)) {
                            listeners.add(onDeviceOnlineListener);
                        }
                    }
                }
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"keepalive 注册-" + onDeviceOnlineListener.getClass().getName());
            }
        }, "注册-" + ByteUtils.getCurrentTime()).start();
    }

    /**
     * 注销监听
     *
     * @param onDeviceOnlineListener
     */
    public void unresgisterDeviceOnlineListenerOld(final OnDeviceOnlineListener onDeviceOnlineListener) {
        if (!listeners.contains(onDeviceOnlineListener))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (keepAliveThread == null){
                    if (onDeviceOnlineListener != null) {
                        if (listeners.contains(onDeviceOnlineListener)) {
                            onDeviceOnlineListener.relese();
                            listeners.remove(onDeviceOnlineListener);
                        }
                    }
                    return;
                }
                Thread.State state = keepAliveThread.getState();
                if (state.equals(Thread.State.WAITING)) {
                    synchronized (listeners) {
                        if (onDeviceOnlineListener != null) {
                            if (listeners.contains(onDeviceOnlineListener)) {
                                onDeviceOnlineListener.relese();
                                listeners.remove(onDeviceOnlineListener);
                            }
                            listeners.notifyAll();
                        }
                    }
                } else {
                    if (onDeviceOnlineListener != null) {
                        if (listeners.contains(onDeviceOnlineListener)) {
                            onDeviceOnlineListener.relese();
                            listeners.remove(onDeviceOnlineListener);
                        }
                    }
                }
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"keepalive 销毁-" + onDeviceOnlineListener.getClass().getName());
            }
        }, "销毁-" + ByteUtils.getCurrentTime()).start();
    }

    /**
     * 事件触发
     */
    private void trigger() {
        Iterator<OnDeviceOnlineListener> it = listeners.iterator();
        while (it.hasNext()) {
            OnDeviceOnlineListener mgr = it.next();
            HashSet<String> macList = mgr.getVectorMacAddr();
            Iterator<String> macIt = macList.iterator();
            while (macIt.hasNext()) {
                String mac = macIt.next();
                if (TextUtils.isEmpty(mac))
                    continue;
                UdpDeviceDataBean run = DeviceBindMap.runJudgeBindStatus.get(mac.toUpperCase());
                if (run == null) {
                    mgr.onLine(false, mac);
//                    System.out.println("uuuuuu===Delegate======== trigger====1  [" +Thread.currentThread().getName() +"] "+listeners.toString());
                    continue;
                }
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"keepalive " + (keepalivetime) + "ms heartBeat=" + (System.currentTimeMillis() - run.getKeepaliveTime()) + "ms run=" + run.getDeviceMac());
                if (System.currentTimeMillis() - run.getKeepaliveTime() >= keepalivetime) {
                    DeviceBindMap.runJudgeBindStatus.remove(mac);
                    mgr.onLine(false, mac);
                } else {
                    if (run != null) {
                        run.setOnLine(true);
                        DeviceBindMap.runJudgeBindStatus.put(mac.toUpperCase(), run);
                    }
                    mgr.onLine(true, mac);
                }
            }
        }

        /**清理runJudgeBindStatus中保存的设备**/
        Collection<UdpDeviceDataBean> collDevice = DeviceBindMap.runJudgeBindStatus.values();
        if (collDevice != null){
            Iterator<UdpDeviceDataBean> itDevice = collDevice.iterator();
            if (itDevice != null){
                while (itDevice.hasNext()){
                    UdpDeviceDataBean run = itDevice.next();
                    if (System.currentTimeMillis() - run.getKeepaliveTime() >= keepalivetime) {
                        itDevice.remove();
                    }
                }
            }

        }

//        lock.notify();
    }

    public synchronized void init() {
        working = true;
        if (keepAliveThread == null) {
            keepAliveThread = new Thread(getInstnce(), "KeepAliveManager-心跳包管理");
            keepAliveThread.start();
        }
    }

    public void close() {
        working = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (listeners) {
                    listeners.clear();
                    listeners.notifyAll();
                    keepAliveThread = null;
                }
            }
        }, "keepalive.close").start();
    }

    @Override
    public void run() {
        synchronized (listeners) {
            while (working) {
                if (listeners.size() == 0) {
                    try {
                        listeners.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
//                        lock.notify();
                    }
                }
                trigger();
                try {
                    listeners.wait(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
//                    lock.notify();
                }
//                System.out.println("uuuuuu===Delegate======== trigger====0  [" +Thread.currentThread().getName() +"] "+listeners.toString());
            }
        }
    }
}
