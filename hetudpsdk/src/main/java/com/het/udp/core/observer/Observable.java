package com.het.udp.core.observer;


import com.het.log.Logc;
import com.het.udp.core.smartlink.BindDataInteractions;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.utils.Contants;
import com.het.xml.protocol.ProtocolManager;
import com.het.xml.protocol.model.PacketDataBean;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Observable {
    //控制数据
    public final static short HET_CONFIG_DATA = 1;
    //运行数据
    public final static short HET_RUN_DATA = 2;
    //基本数据
    public final static short HET_BASIC_DATA = 3;
    //运行故障
    public final static short HET_RUN_ERROR_DATA = 4;
    private static Set<IObserver> obs = new HashSet<IObserver>();

    private IObserver dataInterceptor;

    public static synchronized void registerObserver(IObserver o) {
        if (o != null) {
            if (!obs.contains(o)) {
                obs.add(o);
            }
        }
    }

//    public static IObserver get(IObserver o) {
//        Iterator<IObserver> it = obs.iterator();
//        while (it.hasNext()) {
//            IObserver mgr = it.next();
//            if (o.equals(mgr)) {
//                Logc.i(Logc.HetReportTag.INFO_WIFI,mgr.getClass().toString());
//                return mgr;
//            }
//        }
//        return null;
//    }

    public static synchronized void unregisterObserver(IObserver o) {
        if (obs.contains(o)) {
            obs.remove(o);
        }
    }

    public void setDataInterceptor(IObserver dataInterceptor) {
        this.dataInterceptor = dataInterceptor;
    }

    public static synchronized void clear() {
        obs.clear();
    }

    public synchronized void notifyObservers(PacketModel obj) {
        if (obj == null)
            return;
        //数据拦截
        if (dataInterceptor != null) {
            dataInterceptor.receive(obj);
        }
        if (ProtocolManager.getInstance().isLoad() && obj.getBody() != null) {
            try {
                if (obj == null)
                    throw new IllegalArgumentException("Packet is null...");
                if (obj.getDeviceInfo() == null)
                    throw new IllegalArgumentException("devicemodel data is null...");
                if (obj.getBody() == null)
                    throw new IllegalArgumentException("json data is null...");
                String json = ProtocolManager.getInstance().decode(getPackDataBean(obj));
                obj.setJson(json);
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "小循环设备返回JSON:" + json);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        Iterator<IObserver> it = obs.iterator();
//		 Logc.i(Logc.HetReportTag.INFO_WIFI,"**notifyObservers命令字"+" size="+obs.size());
        while (it.hasNext()) {
            IObserver mgr = it.next();
//			Logc.i(Logc.HetReportTag.INFO_WIFI,"+++++***命令字" + mgr.getClass().toString());
//            mgr.receive(obj);
            obj.resetCommendType();
            if (BindDataInteractions.class.getSimpleName().equalsIgnoreCase(mgr.getClass().getSimpleName())) {
                UdpDeviceDataBean udpDeviceDataBean = obj.getDeviceInfo();
                if (udpDeviceDataBean != null) {
                    mgr.receive(obj);
                }
            } else {
                if (obj.isOpenProtocol()) {
                    UdpDeviceDataBean udpDeviceDataBean = obj.getDeviceInfo();
                    if (udpDeviceDataBean != null) {
                        if (udpDeviceDataBean.getCommandType() == Contants.OPEN.CONFIG
                                ._HET_OPEN_CONFIG_RECV || udpDeviceDataBean.getCommandType() ==
                                Contants.OPEN.CONFIG._HET_OPEN_CONFIG_RES || udpDeviceDataBean
                                .getCommandType() == Contants.OPEN.CONFIG._HET_OPEN_CONFIG_SEND) {
                            //控制数据
                            obj.setCommand(HET_CONFIG_DATA);
                            mgr.receive(obj);
                        } else if (udpDeviceDataBean.getCommandType() == Contants.OPEN.RUN
                                ._HET_OPEN_RUN_RECV || udpDeviceDataBean.getCommandType() == Contants
                                .OPEN.RUN._HET_OPEN_RUN_RES) {
                            //运行数据
                            obj.setCommand(HET_RUN_DATA);
                            mgr.receive(obj);
                        } else if (udpDeviceDataBean.getCommandType() == Contants.OPEN.RUNERROR._HET_OPEN_RUN_ERR_RECV) {
                            //运行故障数据
                            obj.setCommand(HET_RUN_ERROR_DATA);
                            mgr.receive(obj);
                        } else {
                            mgr.receive(obj);
                        }
                    }
                } else {
                    UdpDeviceDataBean udpDeviceDataBean = obj.getDeviceInfo();
                    if (udpDeviceDataBean != null) {
                        if (udpDeviceDataBean.getCommandType() == Contants.HET_LAN_SEND_CONFIG_RSP) {
                            //控制数据
                            obj.setCommand(HET_CONFIG_DATA);
                            mgr.receive(obj);
                        } else if (udpDeviceDataBean.getCommandType() == Contants.HET_LAN_TIMER_RUNNING) {
                            //运行数据
                            obj.setCommand(HET_RUN_DATA);
                            mgr.receive(obj);
                        } else if (udpDeviceDataBean.getCommandType() == Contants.HET_LAN_QUERY_CONFIG_AND_RUNNING || udpDeviceDataBean.getCommandType() == Contants.HET_NEW_BIND_RESPON_PROTOCOL_VERSION) {
                            //基本数据
                            obj.setCommand(HET_BASIC_DATA);
                            mgr.receive(obj);
                        }
                    }
                }
            }
        }
    }


    public static PacketDataBean getPackDataBean(PacketModel packet) {
        PacketDataBean packetDataBean = new PacketDataBean();
        packetDataBean.setBody(packet.getBody());
        packetDataBean.setJson(packet.getJson());
        packetDataBean.setCommand(packet.getCommand());
        packetDataBean.setDeviceMac(packet.getMacAddr());
        packetDataBean.setDeviceType(packet.getDeviceInfo().getDeviceType());
        packetDataBean.setDeviceSubType(packet.getDeviceInfo().getDeviceSubType());
        packetDataBean.setDataVersion((packet.getDeviceInfo().getDataVersion() == null) ? 1 : packet.getDeviceInfo().getDataVersion());
        return packetDataBean;
    }

}
