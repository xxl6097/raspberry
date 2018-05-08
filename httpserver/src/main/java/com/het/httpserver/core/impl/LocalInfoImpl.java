package com.het.httpserver.core.impl;

import com.het.httpserver.bean.FanyiBean;
import com.het.httpserver.bean.IpLocationBean;
import com.het.httpserver.bean.TaobaoLocationBean;
import com.het.httpserver.core.AbstractGetHttpFactory;
import com.het.httpserver.core.AbstractHttpBusiness;
import com.het.httpserver.http.SimpleHttpUtils;
import com.het.httpserver.util.BaiduFanyi;
import com.het.httpserver.util.GsonUtil;
import com.het.httpserver.util.Logc;
import com.het.httpserver.util.Util;

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
            String html = processLan(Util.locateCityName(ip));
            result = ";"+callback + "("+ html+");";
        }
        return result;
    }

    private String processLan(String ret){
        IpLocationBean ipBean = GsonUtil.getInstance().toObject(ret, IpLocationBean.class);
        if (ipBean==null)
            return null;
        TaobaoLocationBean taobaoLocationBean = new TaobaoLocationBean();
        taobaoLocationBean.setCode(0);
        TaobaoLocationBean.DataBean dataBean = new TaobaoLocationBean.DataBean();
        dataBean.setIp(ipBean.getQuery());
        dataBean.setCountry(say(ipBean.getCountry()));
        dataBean.setCity(say(ipBean.getCity()));
        dataBean.setRegion(say(ipBean.getRegionName()));
        dataBean.setIsp(say(ipBean.getIsp()));
        dataBean.setCountry_id(say(ipBean.getCountryCode()));
        taobaoLocationBean.setData(dataBean);
        return GsonUtil.getInstance().toJson(taobaoLocationBean);
    }

    private String say(String words){
        String ret = BaiduFanyi.say(words);
        FanyiBean bean = GsonUtil.getInstance().toObject(ret, FanyiBean.class);
        try {
            words= bean.getTrans_result().get(0).getDst();
        }catch (Exception e){
        }
        return words;
    }
}
