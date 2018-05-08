package com.het.httpserver.core.impl;

import com.het.httpserver.core.AbstractGetHttpFactory;
import com.het.httpserver.core.AbstractHttpBusiness;
import com.het.httpserver.util.BaiduFanyi;
import com.het.httpserver.util.Logc;
import com.het.httpserver.util.Util;

import java.util.Map;

public class FanyiImpl extends AbstractGetHttpFactory {


    final String FANYI_PATH = "/v1/api/fanyi";
    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================LocalInfoImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.startsWith(FANYI_PATH)) {
            String callback = param.get("callback");
            String query = param.get("query");
            String html = BaiduFanyi.say(query);
            result = ";"+callback + "("+ html+");";
        }
        return result;
    }


}
