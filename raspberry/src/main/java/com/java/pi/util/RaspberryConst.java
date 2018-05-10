package com.java.pi.util;

import java.io.File;

public class RaspberryConst {
    public final static class Pi{
        public final static String HOST = "http://192.168.31.114:8123";
        //http://192.168.31.114:8123/api/services/mqtt/publish
        public final static String MQTT = "mqtt";

    }

    public final static class ENTITY {
        public final static String MI_SOCKET = "switch.mi_socket_plus";
        public final static String MI_SOCKET_USB = "switch.mi_socket_plus_usb";
        public final static String MAC_WAKE_UP = "switch.mi_socket_plus_usb";
        public final static String LIGHT_YEELIGHT = "light.yeelight";
    }

    public final static class SERVICES{
        public final static String MQTT = "/api/services/mqtt/publish";
        public final static String LIGHT = "/api/services/light/";
        public final static String GATEWAY = "/api/services/xiaomi_aqara/";
        public final static String SWITCH = "/api/services/switch/";
        public final static String STATE = "/api/states/";

        public final static String HB_RESTART = "/api/services/homeassistant/restart";
    }

    public final static class STATE{
        public final static String TURN_ON = "turn_on";
        public final static String TURN_OFF = "turn_off";
        public final static String PLAY_RINGTONE = "play_ringtone";
        public final static String STOP_RINGTONE = "stop_ringtone";
    }

    public final static class HTTP{
        public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
                + "web" + File.separator;
    }
}
