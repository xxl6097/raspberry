package com.java.pi.httpserver.core.impl;


import com.java.pi.httpserver.bean.ApiResult;
import com.java.pi.httpserver.core.AbstractHttpBusiness;
import com.java.pi.httpserver.core.http.NanoHTTPD;
import com.java.pi.httpserver.util.GsonUtil;
import com.java.pi.util.Logc;

import java.util.Map;

/**
 * ————————————————————————————————
 * Copyright (C) 2014-2017, by het, Shenzhen, All rights reserved.
 * ————————————————————————————————
 * <p>
 * <p>描述：同步手机app的数据（列表、控制、运行、详情等扥）</p>
 * 名称: OnSyncDataImpl <br>
 * 作者: uuxia<br>
 * 版本: 1.0<br>
 * 日期: 2017/12/19 15:38<br>
 **/
public class UnknowImpl extends AbstractHttpBusiness {
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================UnknowImpl :" + path + " "
                + (param == null ? "param is null" : param.toString()));
        /*String callback = param.get("callback");
        String ip = param.get("ip");
        String html = null;
        try {
            html = SimpleHttpUtils.get("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = ";"+callback + "("+ html+");";*/
        return GsonUtil.getInstance().toJson(new ApiResult<String>());
    }

    @Override
    protected String onPost(NanoHTTPD.IHTTPSession session) {
        return "UnknowImpl";
    }

    @Override
    protected String onGet(String path, Map<String, String> param) {
        return "UnknowImpl";
    }
}
