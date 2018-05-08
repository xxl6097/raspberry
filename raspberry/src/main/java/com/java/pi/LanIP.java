package com.java.pi;

import com.java.pi.util.Logc;

/**
 * 使用java线程扫描局域网ip简单方案
 * @author Administrator
 *
 */
public class LanIP {

    public static void main(String[] args) {
//        boolean reachable = false;
//        Process process;
//        try {
//            String ipAddress = "192.168.1.1";
//            process = Runtime.getRuntime().exec("ping " + ipAddress);
//            int returnVal = process.waitFor();
//            System.out.println("Sending Ping Request to " + returnVal);
//            reachable = returnVal == 0?true:false;
//            System.out.println(reachable ? "Host is reachable" : "Host is NOT reachable");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        parseDeviceType("HET_000819_0FAD");

    }

    private static boolean parseDeviceType(String ssid) {
        String[] strs = ssid.split("_");
        if (strs == null || strs.length <= 1)
            return false;
        String deviceInfo = strs[1];
        //000b03
        if (deviceInfo.length() < 6)
            return false;
        String da = deviceInfo.substring(0, 4);
        String xi = deviceInfo.substring(4, 6);
        try {
            int i = Integer.parseInt(da, 16);
            int p = Integer.parseInt(xi, 16);
            Logc.i("uu==== i:" + i + " p:" + p);
            return true;
        } catch (Exception e) {
            Logc.i("uu==== parseDeviceType:" + ssid + " exception:" + e.getMessage());
            return false;
        }
    }
}