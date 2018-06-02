package com.het.udp.core.smartlink.callback;

/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2015-12-02 19:41
 * Description:
 */
/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife -
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: OnDiffComplayEvents.java
 * Create: 2015/12/2 19:41
 */
public abstract class OnDiffComplayEvents {
    /**
     */
    public final static int ACCESS_ROUTER_STYLE_SMARTLINK = 0x0003;
    /**
     * Ti
     */
    public final static int ACCESS_ROUTER_STYLE_TI = 0x0002;
    /**
     */
    public final static int ACCESS_ROUTER_STYLE_REALTEK = 0x0001;

    private int type = 1;

    public OnDiffComplayEvents(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract void onConfigure(Object value);
}
