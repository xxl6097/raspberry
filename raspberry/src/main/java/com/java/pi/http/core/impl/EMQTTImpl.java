package com.java.pi.http.core.impl;

import com.java.pi.util.HttpConst;
import com.java.pi.util.Logc;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class EMQTTImpl extends com.java.pi.http.core.AbstractGetHttpFactory {
    final String HOST = "http://"+ HttpConst.HOST+":8080";
    final String GET_CLIENT = "/api/v2/nodes/emq@127.0.0.1/clients";
    final String GET_TOPICS = "/api/v2/nodes/emq@127.0.0.1/subscriptions/";
    final String PATH_CLIENTS = "/v1/mqtt/clients";
    final String PATH_TOPICS = "/v1/mqtt/topics";
    final String username = "admin";
    final String password = "public";
    final String callback = "callback";

    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================EMQTTImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (com.java.pi.http.util.Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.startsWith(PATH_CLIENTS)) {
            result = getClients(param);
        } else if (path.startsWith(PATH_TOPICS)) {
            result = getTopics(param);
        }
        return result;
    }

    private String getClients(Map<String, String> param) {
        String result = null;
        String baseUrl = param.get("HOST");
        if (com.java.pi.http.util.Util.isEmpty(baseUrl)) {
            baseUrl = this.HOST;
        }
        String username = param.get("username");
        if (com.java.pi.http.util.Util.isEmpty(username)) {
            username = this.username;
        }
        String password = param.get("password");
        if (com.java.pi.http.util.Util.isEmpty(password)) {
            password = this.password;
        }
        String callback = param.get("callback");
        if (com.java.pi.http.util.Util.isEmpty(callback)) {
            callback = this.callback;
        }

        String page_size = param.get("page_size");
        if (com.java.pi.http.util.Util.isEmpty(callback)) {
            page_size = "100";
        }

        String curr_page = param.get("curr_page");
        if (com.java.pi.http.util.Util.isEmpty(callback)) {
            curr_page = "100";
        }
        //page_size=1&curr_page=1

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("username", username);
            headers.put("password", password);
            result = com.java.pi.http.http.SimpleHttpUtils.get(baseUrl + GET_CLIENT + "?page_size="+page_size+"&curr_page"+curr_page, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = ";" + callback + "(" + result + ");";
        return data;
    }

    private String getTopics(Map<String, String> param) {
        String result = null;
        String baseUrl = param.get("HOST");
        if (com.java.pi.http.util.Util.isEmpty(baseUrl)) {
            baseUrl = this.HOST;
        }
        String username = param.get("username");
        if (com.java.pi.http.util.Util.isEmpty(username)) {
            username = this.username;
        }
        String password = param.get("password");
        if (com.java.pi.http.util.Util.isEmpty(password)) {
            password = this.password;
        }
        String callback = param.get("callback");
        if (com.java.pi.http.util.Util.isEmpty(callback)) {
            callback = this.callback;
        }
        String client_id = param.get("client_id");
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("username", username);
            headers.put("password", password);
            result = com.java.pi.http.http.SimpleHttpUtils.get(baseUrl + GET_TOPICS+URLEncoder.encode(client_id), headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = ";" + callback + "(" + result + ");";
        return data;
    }
}
