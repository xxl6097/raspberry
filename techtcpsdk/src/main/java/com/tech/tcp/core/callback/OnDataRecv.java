package com.tech.tcp.core.callback;

public interface OnDataRecv {
    void onConnected();
    void messageReceived(byte[] data);
}
