package com.het.udp.wifi.callback;

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: OnSendListener.java
 * Create: 2016/3/14 10:20
 * Author: uuxia
 */
public abstract class OnSendListener {
    private int cmd;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public OnSendListener(int cmd) {
        this.cmd = cmd;
    }

    public OnSendListener() {
    }

    abstract public void onSendSucess(int cmd,Object object);
    abstract public void onSendFailed(int cmd,Object object,Throwable throwable);
}
