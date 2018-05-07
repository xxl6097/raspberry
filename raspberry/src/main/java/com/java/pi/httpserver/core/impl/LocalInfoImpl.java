package com.java.pi.httpserver.core.impl;

import com.java.pi.httpserver.core.AbstractGetHttpFactory;
import com.java.pi.httpserver.http.SimpleHttpUtils;
import com.java.pi.httpserver.util.Util;
import com.java.pi.util.Logc;

import java.util.Map;

public class LocalInfoImpl extends AbstractGetHttpFactory {
    final String PATH_TOPICS = "/v1/ip/getlocationbyip";
    final String PATH_GET_CITY = "/v1/ip/getCityByIp";
    final String PATH_CITY = "/v1/ip/getCity";
    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================LocalInfoImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.startsWith(PATH_TOPICS)) {
            String callback = param.get("callback");
            String ip = param.get("ip");
            String html = null;
            try {
                html = SimpleHttpUtils.get("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = ";"+callback + "("+ html+");";
        }else if (path.startsWith(PATH_GET_CITY)) {
            String callback = param.get("callback");
            String ip = param.get("ip");
            String html = Util.locateCityName(ip);
            result = ";"+callback + "("+ html+");";
        }else if (path.startsWith(PATH_CITY)) {
            String callback = param.get("callback");
            String ip = param.get("ip");
            String html = Util.locateCityName(ip);
            result = ";"+callback + "("+ html+");";
        }
        return result;
    }


}
