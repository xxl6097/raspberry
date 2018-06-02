package com.tech.tcp.core;

import com.tech.tcp.core.base.BaseQueue;
import com.tech.tcp.core.bean.PacketDataBean;
import com.tech.tcp.core.callback.OnDataListener;
import com.tech.tcp.core.callback.OnDataRecv;
import com.tech.tcp.core.tcp.TcpClient;

public class TcpHandler extends BaseQueue<PacketDataBean> {
    private TcpClient client;
    private OnDataRecv onDataRecv;
    private String host;
    private int port;

    public TcpHandler(String host, int port, OnDataRecv listener) {
        client = new TcpClient();
        this.onDataRecv = listener;
        this.host = host;
        this.port = port;
    }

    @Override
    protected void execute() {
        client.connect(host, port, onDataRecv);
        while (running) {
            try {
                PacketDataBean data = queue.take();
                boolean sucess = client.send(data.getData());
                OnDataListener listener = data.getListener();
                if (listener != null) {
                    if (sucess) {
                        listener.onSucess(listener.getPacket());
                    } else {
                        listener.onFailed(listener.getPacket());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public boolean send(PacketDataBean data) {
        if (data == null)
            return false;
        if (data.getData() == null)
            return false;
        if (data.isFlush()) {
            queue.clear();
        }
        return queue.offer(data);
    }

    @Override
    public void close() {
        if (client != null) {
            client.stop();
        }
        running = false;
    }

}
