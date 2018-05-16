package com.java.pi.http.core.impl;

import com.java.pi.api.RaspBerryApi;
import com.java.pi.services.MiSocketPlusManager;
import com.java.pi.util.Logc;

import java.util.Map;

public class RaspberryImpl extends com.java.pi.http.core.AbstractGetHttpFactory {
    final String PATH_MI_SOCKET_PLUS = "/v1/pi/switch";
    final String PATH_MI_LIGHT = "/v1/pi/light";
    final String PATH_MI_LIGHT_STATE = "/v1/pi/state";
    final String PATH_MI_GATEWAY = "/v1/pi/gateway";
    final String PATH_MI_MQTT = "/v1/pi/mqtt";
    final String PATH_MI_TIME = "/v1/pi/timer";
    final String PATH_DEV_STATE = "/v1/dev/state";

    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================RaspberryImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (com.java.pi.http.util.Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.equals(PATH_MI_SOCKET_PLUS)) {
            result = miSwitch(param);
        }else if (path.equals(PATH_MI_MQTT)){
            result = processMQTT(param);
        }else if (path.equals(PATH_MI_LIGHT)){
            result = processLight(param);
        }else if (path.equals(PATH_MI_GATEWAY)){
            result = processGateWay(param);
        }else if (path.equals(PATH_MI_LIGHT_STATE)){
            Logc.e("### PATH_MI_LIGHT_STATE:"+path);
            result = processState(param);
        }else if (path.equals(PATH_MI_TIME)){
            Logc.e("### PATH_MI_TIME:"+path);
            result = processTimer(param);
        }else if (path.equals(PATH_DEV_STATE)){
            Logc.e("### PATH_DEV_STATE:"+path);
            result = getDevState(param);
        }
        Logc.e("### RaspberryImpl:"+result);
        return result;
    }

    private String getDevState(Map<String, String> param) {
        String result = "";
        String entityid = param.get("entityid");
        if (com.java.pi.http.util.Util.isEmpty(entityid)) {
            result = "{\"code:\"-1}";
            return result;
        }

        if (entityid.equals("timer")){
            boolean state = MiSocketPlusManager.getInstance().isAlive();
            if (state){
                result = "{\"code:\"0}";
            }else{
                result = "{\"code:\"-1}";
            }
        }

        Logc.e(result);
        return result;
    }

    private String processTimer(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String state = param.get("state");
        if (com.java.pi.http.util.Util.isEmpty(state)) {
            result = "state is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        if (state.equals("on")){
            MiSocketPlusManager.getInstance().setState(true);
        }else{
            MiSocketPlusManager.getInstance().setState(false);
        }
        result = "sucess";
        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processState(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String entityid = param.get("entityid");
        if (com.java.pi.http.util.Util.isEmpty(entityid)) {
            result = "entityid is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        result = RaspBerryApi.MiState(entityid);
        Logc.e("### MiState:"+result);
        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processLightState(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        result = RaspBerryApi.YeelightState();
        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processLight(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String state = param.get("state");
        if (com.java.pi.http.util.Util.isEmpty(state)) {
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
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processGateWay(Map<String, String> param) {
        String result = "";
        String callback = param.get("callback");
        String state = param.get("state");
        if (com.java.pi.http.util.Util.isEmpty(state)) {
            result = "state is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }

        String ringid = param.get("ringid");

        if (state.equals("on")){
            if (com.java.pi.http.util.Util.isEmpty(ringid)) {
                ringid = "8";
            }
            result = RaspBerryApi.playRingtone(ringid);
        }else{
            result = RaspBerryApi.stopRingtone();
        }


        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String processMQTT(Map<String, String> param) {
        String result = "";
        String topic = param.get("topic");
        String callback = param.get("callback");
        if (com.java.pi.http.util.Util.isEmpty(topic)) {
            result = "topic is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        String payload = param.get("payload");
        if (com.java.pi.http.util.Util.isEmpty(payload)) {
            result = "payload is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }

        result = RaspBerryApi.mqttSent(topic,payload);
        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }

    private String miSwitch(Map<String, String> param) {
        String result = "";
        String entityid = param.get("entityid");
        String callback = param.get("callback");
        if (com.java.pi.http.util.Util.isEmpty(callback)) {
//            callback = "callback";
        }
        if (com.java.pi.http.util.Util.isEmpty(entityid)) {
            result = "entityid is null";
            String data = ";" + callback + "(" + result + ");";
            return data;
        }
        String state = param.get("state");
        if (com.java.pi.http.util.Util.isEmpty(state)) {
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
                    try {
                        RaspBerryApi.HomeAssistantRestart();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else if(entityid.equals("misocketplushusb")){
                if (state.equals("on")){
                    result = RaspBerryApi.MiSocketUSBTurnOn();
                }else{
                    result = RaspBerryApi.MiSocketUSBTurnOff();
                }
                if (result.equals("[]")){
                    try {
                        RaspBerryApi.HomeAssistantRestart();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }else if(entityid.equals("mac")){
            result = RaspBerryApi.WakeUpMyMacOs();
        }else if(entityid.equals("homeassistant")){
            if (state.equals("restart")){
                try {
                    result = RaspBerryApi.HomeAssistantRestart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else if(entityid.equals("homeassistant")){
            if (state.equals("restart")){
                try {
                    result = RaspBerryApi.HomeAssistantRestart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        String data = result;
        if (!com.java.pi.http.util.Util.isEmpty(callback)){
            data = ";" + callback + "(" + result + ");";
        }
        Logc.e(data);
        return data;
    }
}
