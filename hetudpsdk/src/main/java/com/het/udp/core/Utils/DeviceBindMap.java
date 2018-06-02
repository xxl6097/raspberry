package com.het.udp.core.Utils;


import com.het.udp.wifi.model.UdpDeviceDataBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceBindMap {
    /**
     * unbind device Mapper
     */
    public static ConcurrentHashMap<String, UdpDeviceDataBean> unbindDeviceMap = new ConcurrentHashMap<String, UdpDeviceDataBean>();
    /**
     * 存储连接设备信息*
     */
    public static ConcurrentHashMap<String, UdpDeviceDataBean> bindDeviceMap = new ConcurrentHashMap<String, UdpDeviceDataBean>();

    /**
     * 讲运行数据作为心跳包
     */
//    public static Map<String, UdpDeviceDataBean> runJudgeBindStatus = new HashMap<String, UdpDeviceDataBean>();
    //线程安全Map
    //此Map保存只要发送了4005，有回复0005者，皆保存至此Map集合中
    public static ConcurrentHashMap<String, UdpDeviceDataBean> runJudgeBindStatus = new ConcurrentHashMap<String, UdpDeviceDataBean>();

    public static Set<String> specialMacSets = new HashSet<>();
    public static Set<String> normalMacSets = new HashSet<>();


    /**
     * for test
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        putMap();
//        getMap();

        specialMacSets.add("acc3455");
        specialMacSets.add("bbfaf3");
        String macs = "bbfAf3";
        boolean tt = specialMacSets.contains(macs.toLowerCase());
//        System.out.println(tt);
    }

    private static void putMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    UdpDeviceDataBean dm = new UdpDeviceDataBean();
                    dm.setIp("192.168.1.1");
                    dm.setDeviceId("deviceId_" + i);
                    DeviceBindMap.runJudgeBindStatus.put("UUXIA_" + i, dm);
//                    System.out.println("put++++++++++++++++++++++++++++++++");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static void getMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Iterator<String> it = DeviceBindMap.runJudgeBindStatus.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        UdpDeviceDataBean dm = DeviceBindMap.runJudgeBindStatus.get(key.toUpperCase());
                        if (dm != null) {
                            DeviceBindMap.runJudgeBindStatus.remove(key);
//                            System.out.println("get------------------------");
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * get unbind device lsit
     * @return
     */
    public static List<UdpDeviceDataBean> getUnbindDeviceList(){
        List<UdpDeviceDataBean> list = new ArrayList<UdpDeviceDataBean>(unbindDeviceMap.values());
        return list;
    }

}
