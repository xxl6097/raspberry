package com.het.udp.wifi.callback;

import com.het.udp.wifi.model.PacketBuffer;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public interface IRecevie {
    void onRecevie(PacketBuffer packet);
}
