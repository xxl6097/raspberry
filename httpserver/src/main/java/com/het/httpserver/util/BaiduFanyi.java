package com.het.httpserver.util;

import com.baidu.translate.demo.TransApi;

public class BaiduFanyi {
    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20180418000147840";
    private static final String SECURITY_KEY = "3lNTR8d9bfq4giBZcH2P";
    public static String say(String query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String r = api.getTransResult(query, "auto", "auto");
        System.out.println(r);
        return r;
    }
}
