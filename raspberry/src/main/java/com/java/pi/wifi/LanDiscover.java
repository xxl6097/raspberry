package com.java.pi.wifi;


import com.java.pi.util.Logc;
import com.java.pi.util.OSinfo;
import com.java.pi.util.Ping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LanDiscover {

    private static final String TAG = LanDiscover.class.getSimpleName();
    private Runtime mRun = Runtime.getRuntime();// 获取当前运行环境，来执行ping，相当于windows的cmd
    private static String ping = "ping -c 1 -w 3 ";// 其中 -c 1为发送的次数，-w 表示发送后等待响应的时间 秒
    private List<Device> lists = new ArrayList<>();// ping成功的IP地址
    private Thread thread = null;

    public LanDiscover() {
    }

    public void discoverLoop(final long time,final String targetIp,final OnDiscoverDevice onDiscoverDevice) {
        if (targetIp == null)
            return;
        if (thread!=null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        discover(targetIp,onDiscoverDevice);
                        Thread.sleep(time);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    // 根据ip 网段去 发送arp 请求
    public void discover(String mDevAddress, OnDiscoverDevice discover) throws InterruptedException {
//        mDevAddress = getLocAddress();// 获取本机IP地址
        String mLocAddress = getLocAddrIndex(mDevAddress);// 获取本地ip前缀
        if (!mLocAddress.equals("")) {
            String ipseg = mLocAddress.substring(0, mLocAddress.lastIndexOf(".") + 1);
            for (int i = 1; i < 255; i++) {
                String newip = ipseg + String.valueOf(i);
                if (newip.equals(mLocAddress))
                    continue;
                Thread ut = new UDPThread(newip);
                ut.start();
            }
//            Logc.w(TAG, "线程发完毕");
            lists.clear();
            Thread.sleep(1000);
            Map<String, Device> mm = null;
            if (OSinfo.isWindows()) {
                mm = getWinArp();
            } else {
                mm = getIpMacFromFile();
            }


            Logc.i(TAG, "当前系统：" + OSinfo.getOSname().toString());
            Logc.i(TAG, "arp列表个数 " + mm.size());
            ping(mm, discover);
        }
    }

    private void ping(final Map<String, Device> map, final OnDiscoverDevice discover){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Device> it = map.values().iterator();
                while (it.hasNext()) {
                    Device device = it.next();
                    if (device == null || device.ip == null || device.ip.equals(""))
                        continue;
                    String currnetIp = device.ip;
                    long start = System.currentTimeMillis();
                    long end = System.currentTimeMillis() - start;
                    boolean result = Ping.ping(currnetIp,1,3000);
                    if (result) {
//                        Logc.d(TAG, "在线 " + device.mac + " " + device.ip + " 耗时：" + end);
                        lists.add(device);
                        if (discover != null) {
                            discover.onLogc("在线 " + device.mac + " " + device.ip + " 耗时：" + end);
                            discover.onDeviceState(true, device);
                        }
                    } else {
                        // 扫描失败
//                        Logc.e(TAG, "离线 " + device.mac + " " + device.ip + " 耗时：" + end);
                        if (discover != null) {
                            discover.onLogc("离线 " + device.mac + " " + device.ip + " 耗时：" + end);
                            discover.onDeviceState(false, device);
                        }
                    }
                }
//                Logc.d(TAG, "在线总数 " + lists.size());
                System.out.println();
                if (discover != null) {
                    discover.onLogc("在线总数 " + lists.size());
                }

            }
        }).start();
    }

    private void pings(final Map<String, Device> map, final OnDiscoverDevice discover) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Device> it = map.values().iterator();
                while (it.hasNext()) {
                    Device device = it.next();
                    if (device == null || device.ip == null || device.ip.equals(""))
                        continue;
                    String currnetIp = device.ip;
                    String string = ping + currnetIp;
                    Process mProcess = null;
                    try {
                        long start = System.currentTimeMillis();
                        mProcess = mRun.exec(string);
                        int result = mProcess.waitFor();
                        long end = System.currentTimeMillis() - start;
                        if (result == 0) {
                            Logc.d(TAG, result+"在线 " + device.mac + " " + device.ip + " 耗时：" + end);
                            lists.add(device);
                            if (discover != null) {
                                discover.onLogc(result+"在线 " + device.mac + " " + device.ip + " 耗时：" + end);
                                discover.onDeviceState(true, device);
                            }
                        } else {
                            // 扫描失败
                            Logc.e(TAG, result+"离线 " + device.mac + " " + device.ip + " 耗时：" + end);
                            if (discover != null) {
                                discover.onLogc(result+"离线 " + device.mac + " " + device.ip + " 耗时：" + end);
                                discover.onDeviceState(false, device);
                            }
                        }
                    } catch (Exception e) {
                        Logc.e(TAG, "扫描异常" + e.toString());
                        if (discover != null) {
                            discover.onLogc("扫描异常" + e.toString());
                        }
                    } finally {
                        if (mProcess != null) {
                            mProcess.destroy();
                        }
                    }
                }
                Logc.d(TAG, "在线总数 " + lists.size());
                if (discover != null) {
                    discover.onLogc("在线总数 " + lists.size());
                }

            }
        }).start();
    }


    public static String getLocAddress() {
        String hostIp = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;

            while (true) {
                while (e.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) e.nextElement();
                    Enumeration ias = ni.getInetAddresses();

                    while (ias.hasMoreElements()) {
                        ia = (InetAddress) ias.nextElement();
                        if (!(ia instanceof Inet6Address)) {
                            String ip = ia.getHostAddress();
                            if (!"127.0.0.1".equals(ip)) {
                                hostIp = ia.getHostAddress();
                                break;
                            }
                        }
                    }
                }
                Logc.i(TAG, "本机IP:" + hostIp);
                return hostIp;
            }
        } catch (SocketException var6) {
            var6.printStackTrace();
            return hostIp;
        }
    }

    /**
     * TODO<获取本机IP前缀>
     *
     * @param devAddress // 本机IP地址
     * @return String
     */
    private String getLocAddrIndex(String devAddress) {
        if (!devAddress.equals("")) {
            return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static String getHardwareAddress(String ip) {
        final String NOMAC = "00:00:00:00:00:00";
        final String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        final int BUF = 8 * 1024;
        String hw = NOMAC;
        BufferedReader bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUF);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            } else {
                Logc.e(TAG, "ip is null");
            }
        } catch (IOException e) {
            Logc.e(TAG, "Can't open/read file ARP: " + e.getMessage());
            return hw;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Logc.e(TAG, e.getMessage());
            }
        }
        return hw;
    }

    /**
     * 从proc/net/arp中读取ip_mac对
     */
    private Map<String, Device> getIpMacFromFile() {
        String line;
        String ip;
        String mac;

        String regExp = "((([0-9,A-F,a-f]{1,2}" + ":" + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern;
        Matcher matcher;

        Map<String, Device> maps = new HashMap<>();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            if (bufferedReader != null) {
                bufferedReader.readLine(); //忽略标题行
                while ((line = bufferedReader.readLine()) != null) {
                    ip = line.substring(0, line.indexOf(" "));
                    //Logc.d(TAG, line);
                    pattern = Pattern.compile(regExp);
                    matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        mac = matcher.group(1);
                        if (!mac.equals("00:00:00:00:00:00")) {
                            String mm = mac.toUpperCase(Locale.US);
                            maps.put(ip, new Device(ip, mm));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Logc.e(TAG, e.getMessage());
            }
        }
        return maps;
    }

    public static Map<String, Device> getWinArp() {
        String regExp = "((([0-9,A-F,a-f]{1,2}" + "-" + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern;
        Matcher matcher;
        Process p = null;
        String line;
        String ip;
        String mac;
        Map<String, Device> maps = new HashMap<>();
        BufferedReader bufferedReader = null;
        try {
            p = Runtime.getRuntime().exec("arp -a");
            bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            bufferedReader.readLine();
            bufferedReader.readLine();
            bufferedReader.readLine();
            while (((line = bufferedReader.readLine()) != null && !line.isEmpty())) {
                ip = line.trim().split(" ")[0];
                pattern = Pattern.compile(regExp);
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    mac = matcher.group(1);
                    if (!mac.equals("00-00-00-00-00-00") && !mac.equals("ff-ff-ff-ff-ff-ff")) {
                        String mm = mac.toUpperCase(Locale.US);
                        maps.put(ip, new Device(ip, mm));
//                        System.out.println(mac+"|"+ip);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return maps;
    }

    public static class Device {
        public String ip;
        public String mac;
        public String name;

        public Device(String ip) {
            this.ip = ip;
        }

        public Device(String ip, String mac) {
            this.ip = ip;
            this.mac = mac;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "ip='" + ip + '\'' +
                    ", mac='" + mac + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public interface OnDiscoverDevice {
        boolean onDeviceState(boolean online, Device device);

        void onLogc(String str);
    }
}