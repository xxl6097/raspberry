package com.java.pi.api;

import com.java.pi.bean.RaspPiBean;
import com.java.pi.httpserver.http.SimpleHttpUtils;
import com.java.pi.httpserver.util.GsonUtil;
import com.java.pi.util.RaspberryConst;

public class RaspBerryApi {
//light.yeelight
//    [
//            "light.gateway_light_286c07fa314f",
//            "light.yeelight",
//            "light.yeelight_ceiling_34ce00be19cc"
//            ]

    //http://192.168.31.114:8123/api/services/xiaomi_aqara/play_ringtone
    // {"gw_mac":"286c07fa314f","ringtone_id":"2","ringtone_vol":"30"}
    private static String MiSocketSwitch() {
        String host = "http://192.168.31.114:8123/api/services/switch/turn_off";
        String body = "{\"entity_id\": \"switch.mi_socket_plus\"}";
        try {
            String result = SimpleHttpUtils.post(host, body.getBytes());
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static String HomeAssistantRestart() throws InterruptedException {
        String body = "";
        String path = RaspberryConst.SERVICES.HB_RESTART;
        String result = RaspberryHttp(path, body);
        Thread.sleep(60*1000);
        return result;
    }

    public static String WakeUpMyMacOs() {
        return MiSwitch(RaspberryConst.ENTITY.MAC_WAKE_UP, RaspberryConst.STATE.TURN_ON);
    }


    public static String MiSocketUSBTurnOn() {
        return MiSwitch(RaspberryConst.ENTITY.MI_SOCKET_USB, RaspberryConst.STATE.TURN_ON);
    }

    public static String MiSocketUSBTurnOff() {
        return MiSwitch(RaspberryConst.ENTITY.MI_SOCKET_USB, RaspberryConst.STATE.TURN_OFF);
    }

    public static String MiSocketTurnOn() {
        return MiSwitch(RaspberryConst.ENTITY.MI_SOCKET, RaspberryConst.STATE.TURN_ON);
    }

    public static String YeelightTurnOff() {
        //http://192.168.31.114:8123/api/services/light/turn_off
        //{"entity_id": "light.yeelight"}
        return MiLight(RaspberryConst.ENTITY.LIGHT_YEELIGHT, RaspberryConst.STATE.TURN_OFF);
    }

    public static String YeelightTurnOn() {
        return MiLight(RaspberryConst.ENTITY.LIGHT_YEELIGHT, RaspberryConst.STATE.TURN_ON);
    }

    public static String MiSocketTurnOff() {
        return MiSwitch(RaspberryConst.ENTITY.MI_SOCKET, RaspberryConst.STATE.TURN_OFF);
    }

    public static String mqttSent(String topic, String payload) {
        String body = "{\"topic\": \"" + topic + "\",\"payload\": \"" + payload + "\"}";
        String path = RaspberryConst.SERVICES.MQTT;
        return RaspberryHttp(path, body);
    }

    private static String MiLight(String entityId, String param) {
        String body = "{\"entity_id\": \"" + entityId + "\"}";
        String path = RaspberryConst.SERVICES.LIGHT + param;
        return RaspberryHttp(path, body);
    }

    public static String playRingtone(String ringId) {
        return MiGateWay(RaspberryConst.STATE.PLAY_RINGTONE, ringId);
    }

    public static String stopRingtone() {
        return MiGateWay(RaspberryConst.STATE.STOP_RINGTONE, null);
    }

    private static String MiGateWay(String service, String ringtone_id) {
        //{"gw_mac":"286c07fa314f","ringtone_id":"12"}
        //http://192.168.31.114:8123/api/services/xiaomi_aqara/play_ringtone
        String gw_mac = "286c07fa314f";
        String body = "{\"gw_mac\": \"" + gw_mac + "\",\"ringtone_id\": \"" + ringtone_id + "\"}";
        if (ringtone_id == null) {
            body = "{\"gw_mac\": \"" + gw_mac + "\"}";
        }
        //String body = "{\"entity_id\": \""+ entityId +"\"}";
        String path = RaspberryConst.SERVICES.GATEWAY + service;
        return RaspberryHttp(path, body);
    }

    private static String MiSwitch(String entityId, String param) {
        //http://192.168.31.114:8123/api/services/switch/turn_off
        //"{\"entity_id\": \""+ entityId +"\"}";
        String body = "{\"entity_id\": \"" + entityId + "\"}";
        String path = RaspberryConst.SERVICES.SWITCH + param;
        return RaspberryHttp(path, body);
    }

    private static String RaspberryHttp(String path, String body) {
        String host = RaspberryConst.Pi.HOST + path;
        try {
            System.out.println("host:" + host);
            System.out.println("body:" + body);
            String result = SimpleHttpUtils.post(host, body.getBytes());
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static RaspPiBean getDeviceState(String entity) {
//        http://192.168.31.114:8123/api/states/switch.mi_socket_plus
        String host = RaspberryConst.Pi.HOST + RaspberryConst.SERVICES.STATE + entity;
        try {
            System.out.println("host:" + host);
            String result = SimpleHttpUtils.get(host);
            RaspPiBean raspPiBean = GsonUtil.getInstance().toObject(result, RaspPiBean.class);
            System.out.println(result);
            return raspPiBean;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;

    }


    public static void main(String[] args) {
//        MiSocketTurnOn();
//        MiSocketTurnOff();
//        MiSocketSwitch();
//        HomeAssistantRestart();
    }
}
