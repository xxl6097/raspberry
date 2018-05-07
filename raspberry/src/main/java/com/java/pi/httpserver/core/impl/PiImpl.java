package com.java.pi.httpserver.core.impl;

import com.java.pi.api.RaspBerryApi;
import com.java.pi.httpserver.core.AbstractGetHttpFactory;
import com.java.pi.httpserver.util.Util;
import com.java.pi.util.Logc;

import java.util.Map;

public class PiImpl extends AbstractGetHttpFactory {
    final String PATH_MI_SOCKET_PLUS = "/v1/pi/switch";
    final String PATH_MI_LIGHT = "/v1/pi/light";
    final String PATH_MI_GATEWAY = "/v1/pi/gateway";
    final String PATH_MI_MQTT = "/v1/pi/mqtt";

    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================PiImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.startsWith(PATH_MI_SOCKET_PLUS)) {
            result = miSwitch(param);
        }else if (path.startsWith(PATH_MI_MQTT)){
            result = processMQTT(param);
        }else if (path.startsWith(PATH_MI_LIGHT)){
            result = processLight(param);
        }else if (path.startsWith(PATH_MI_GATEWAY)){
            result = processGateWay(param);
        }
        return result;
    }

    private String processLight(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String state = param.get("state");
        if (Util.isEmpty(state)) {
            result = "state is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }

        if (state.equals("on")){
            result = RaspBerryApi.YeelightTurnOn();
        }else{
            result = RaspBerryApi.YeelightTurnOff();
        }


        String data = result;
        if (!Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processGateWay(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String state = param.get("state");
        if (Util.isEmpty(state)) {
            result = "state is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }

        String ringid = param.get("ringid");

        if (state.equals("on")){
            if (Util.isEmpty(ringid)) {
                ringid = "8";
            }
            result = RaspBerryApi.playRingtone(ringid);
        }else{
            result = RaspBerryApi.stopRingtone();
        }


        String data = result;
        if (!Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processMQTT(Map<String, String> param) {
        String result = "";
        String topic = param.get("topic");
        String callback = param.get("callback");
        if (Util.isEmpty(topic)) {
            result = "topic is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        String payload = param.get("payload");
        if (Util.isEmpty(payload)) {
            result = "payload is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }

        result = RaspBerryApi.mqttSent(topic,payload);
        String data = result;
        if (!Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String miSwitch(Map<String, String> param) {
        String result = "";
        String entityid = param.get("entityid");
        String callback = param.get("callback");
        if (Util.isEmpty(callback)) {
//            callback = "callback";
        }
        if (Util.isEmpty(entityid)) {
            result = "entityid is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        String state = param.get("state");
        if (Util.isEmpty(state)) {
            result = "state is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        if (entityid.equals("misocketplus")){
                if (state.equals("on")){
                    result = RaspBerryApi.MiSocketTurnOn();
                }else{
                    result = RaspBerryApi.MiSocketTurnOff();
                }
                if (result.equals("[]")){
                    RaspBerryApi.HomeAssistantRestart();
                }
            }else if(entityid.equals("misocketplushusb")){
                if (state.equals("on")){
                    result = RaspBerryApi.MiSocketUSBTurnOn();
                }else{
                    result = RaspBerryApi.MiSocketUSBTurnOff();
                }
                if (result.equals("[]")){
                RaspBerryApi.HomeAssistantRestart();
            }
        }else if(entityid.equals("mac")){
            result = RaspBerryApi.WakeUpMyMacOs();
        }else if(entityid.equals("homeassistant")){
            if (state.equals("restart")){
                result = RaspBerryApi.HomeAssistantRestart();
            }
        }else if(entityid.equals("homeassistant")){
            if (state.equals("restart")){
                result = RaspBerryApi.HomeAssistantRestart();
            }
        }

        String data = result;
        if (!Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }
}
