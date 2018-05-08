package com.het.httpserver.bean;

import com.het.httpserver.util.GsonUtil;
import com.het.httpserver.util.Logc;

public class ApiResult<T> {
    private int code;
    private String msg;
    private T data;



    public ApiResult() {
    }

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


    public String toJson() {
        Logc.e("==uu obj:" + this.toString());
        String json = GsonUtil.getInstance().toJson(this);
        Logc.e("==uu json:" + json);
        return json;
    }
}
