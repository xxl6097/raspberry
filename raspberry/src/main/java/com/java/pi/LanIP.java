package com.java.pi;

/**
 * 使用java线程扫描局域网ip简单方案
 * @author Administrator
 *
 */
public class LanIP {

    public static void main(String[] args) {
        boolean reachable = false;
        Process process;
        try {
            String ipAddress = "192.168.1.1";
            process = Runtime.getRuntime().exec("ping " + ipAddress);
            int returnVal = process.waitFor();
            System.out.println("Sending Ping Request to " + returnVal);
            reachable = returnVal == 0?true:false;
            System.out.println(reachable ? "Host is reachable" : "Host is NOT reachable");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}