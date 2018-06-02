package com.het.udp.wifi.utils;

/**
 * Created by uuxia-mac on 15/6/21.
 */
public class LOG {
    /**
     * 总开关
     */
    public static boolean MAIN_LOG_OFF = true;
    /**
     * UDP收发数据开关
     */
    public static boolean UDP_SEND_RECV_OFF = true || MAIN_LOG_OFF;
    /**
     * 协议版本日志开关
     */
    public static boolean PACKET_VERSION_OFF = false;
    /**
     * 广播路由器密码日志开关
     */
    public static boolean SSID_PASS_OFF = false;

    /**汉枫模组**/
    public static boolean HanFengV3 = true;
}
