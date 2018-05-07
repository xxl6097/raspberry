package com.java.pi.api;

import com.java.pi.httpserver.http.SimpleHttpUtils;
import com.java.pi.util.Logc;
import com.java.pi.util.PIConst;

public class RaspBerryApi {
//light.yeelight
//    [
//            "light.gateway_light_286c07fa314f",
//            "light.yeelight",
//            "light.yeelight_ceiling_34ce00be19cc"
//            ]

    //http://192.168.31.114:8123/api/services/xiaomi_aqara/play_ringtone
    // {"gw_mac":"286c07fa314f","ringtone_id":"2","ringtone_vol":"30"}
    private static String MiSocketSwitch(){
        String host = "http://192.168.31.114:8123/api/services/switch/turn_off";
        String body = "{\"entity_id\": \"switch.mi_socket_plus\"}";
        try {
            String result = SimpleHttpUtils.post(host,body.getBytes());
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static String HomeAssistantRestart(){
        String body = "";
        String path = "/api/services/homeassistant/restart";
        return RaspberryHttp(path,body);
    }

    public static String WakeUpMyMacOs(){
        return MiSwitch(PIConst.Pi.MAC_WAKE_UP,PIConst.Pi.TURN_ON);
    }


    public static String MiSocketUSBTurnOn(){
        return MiSwitch(PIConst.Pi.MI_SOCKET_USB,PIConst.Pi.TURN_ON);
    }

    public static String MiSocketUSBTurnOff(){
        return MiSwitch(PIConst.Pi.MI_SOCKET_USB,PIConst.Pi.TURN_OFF);
    }

    public static String MiSocketTurnOn(){
        return MiSwitch(PIConst.Pi.MI_SOCKET,PIConst.Pi.TURN_ON);
    }

    public static String YeelightTurnOff(){
        //http://192.168.31.114:8123/api/services/light/turn_off
        //{"entity_id": "light.yeelight"}
        return MiLight(PIConst.Pi.LIGHT_YEELIGHT,PIConst.Pi.TURN_OFF);
    }

    public static String YeelightTurnOn(){
        return MiLight(PIConst.Pi.LIGHT_YEELIGHT,PIConst.Pi.TURN_ON);
    }

    public static String MiSocketTurnOff(){
        return MiSwitch(PIConst.Pi.MI_SOCKET,PIConst.Pi.TURN_OFF);
    }

    public static String mqttSent(String topic,String payload){
        String body = "{\"topic\": \""+ topic +"\",\"payload\": \"" + payload + "\"}";
        String path = "/api/services/mqtt/publish";
        return RaspberryHttp(path,body);
    }

    private static String MiLight(String entityId,String param){
        String body = "{\"entity_id\": \""+ entityId +"\"}";
        String path = "/api/services/light/"+param;
        return RaspberryHttp(path,body);
    }

    public static String playRingtone(String ringId){
        return MiGateWay("play_ringtone",ringId);
    }

    public static String stopRingtone(){
        return MiGateWay("stop_ringtone",null);
    }

    private static String MiGateWay(String service,String ringtone_id){
        //{"gw_mac":"286c07fa314f","ringtone_id":"12"}
        //http://192.168.31.114:8123/api/services/xiaomi_aqara/play_ringtone
        String gw_mac = "286c07fa314f";
        String body = "{\"gw_mac\": \""+ gw_mac +"\",\"ringtone_id\": \"" + ringtone_id + "\"}";
        if (ringtone_id==null){
            body = "{\"gw_mac\": \""+ gw_mac +"\"}";
        }
        //String body = "{\"entity_id\": \""+ entityId +"\"}";
        String path = "/api/services/xiaomi_aqara/"+service;
        return RaspberryHttp(path,body);
    }

    private static String MiSwitch(String entityId,String param){
        String body = "{\"entity_id\": \""+ entityId +"\"}";
        String path = "/api/services/switch/"+param;
        return RaspberryHttp(path,body);
    }

    private static String  RaspberryHttp(String path,String body){
        String host = PIConst.Pi.HOST + path;
        try {
            System.out.println("host:"+host);
            System.out.println("body:"+body);
            String result = SimpleHttpUtils.post(host,body.getBytes());
            System.out.println(result);
            return result;
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
