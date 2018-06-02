package com.tech.tcp.core.bean;

import com.tech.tcp.core.callback.OnDataListener;

import java.io.Serializable;

public class PacketDataBean implements Serializable {
    private int frameId;
    private boolean flush;
    private byte[] data;
    private OnDataListener listener;

    public PacketDataBean() {
        this(null);
    }

    public PacketDataBean(byte[] data) {
        this(false,data);
    }

    public PacketDataBean(boolean flush, byte[] data) {
        this.flush = flush;
        this.data = data;
        if (listener != null) {
            listener.setPacket(this);
        }
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public boolean isFlush() {
        return flush;
    }

    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public OnDataListener getListener() {
        return listener;
    }

    public void setListener(OnDataListener listener) {
        this.listener = listener;
    }
}
