package com.tech.tcp.core.callback;

import com.tech.tcp.core.bean.PacketDataBean;

public abstract class OnDataListener {
    private PacketDataBean packet;

    public void setPacket(PacketDataBean packet) {
        this.packet = packet;
    }

    public PacketDataBean getPacket() {
        return packet;
    }

    public abstract void onSucess(PacketDataBean paket);
    public abstract void onFailed(PacketDataBean paket);
}
