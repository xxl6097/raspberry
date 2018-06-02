package com.het.udp.core.smartlink.ti;

import java.util.ArrayList;

/**
 * Created by UUXIA on 2015/6/25.
 */
public class SmartConfigEncode {
    private ArrayList<Integer> mData;

    public SmartConfigEncode(String ssid, byte[] key, byte[] freeData, String token, boolean hasEncryption) throws Exception {
        SmartConfig20 sc = new SmartConfig20();
        sc.setmSsid(ssid);
        sc.setmKey(key);
        sc.setmFreeData(freeData);
        sc.setmToken(token);
        sc.setHasEncryption(hasEncryption);
        sc.encodePackets();
        this.mData = sc.getmData();
    }

    public ArrayList<Integer> getmData() throws Exception {
        return this.mData;
    }
}
