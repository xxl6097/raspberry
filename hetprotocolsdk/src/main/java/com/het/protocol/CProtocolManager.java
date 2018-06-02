package com.het.protocol;

import com.het.udp.wifi.model.PacketModel;

import java.io.IOException;

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: CProtocolManager.java
 * Create: 2016/3/28 19:05
 * Author: uuxia
 */
public class CProtocolManager {
    static {
        System.loadLibrary("hetprotocol");
    }

    public static native PacketModel parseData(PacketModel packetModel) throws IOException;

    public static native byte[] packageData(PacketModel packetModel) throws IOException;

}
